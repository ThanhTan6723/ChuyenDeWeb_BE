package org.example.chuyendeweb_be.admin.controller;

import lombok.RequiredArgsConstructor;
import org.example.chuyendeweb_be.user.dto.ProductDetailDTO;
import org.example.chuyendeweb_be.user.entity.*;
import org.example.chuyendeweb_be.user.repository.*;
import org.example.chuyendeweb_be.user.service.CloudinaryService;
import org.example.chuyendeweb_be.user.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class ProductAdminController {
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final ImageRepository imageRepository;
    private final ProductImageRepository productImageRepository;
    private final CloudinaryService cloudinaryService;
    private static final Logger logger = LoggerFactory.getLogger(ProductAdminController.class);

    @GetMapping("/brands")
    public ResponseEntity<List<Brand>> getAllBrands() {
        try {
            logger.info("Fetching all brands");
            List<Brand> brands = brandRepository.findAll();
            return ResponseEntity.ok(brands);
        } catch (Exception e) {
            logger.error("Error fetching brands: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        try {
            logger.info("Fetching all categories");
            List<Category> categories = categoryRepository.findAll();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            logger.error("Error fetching categories: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/products")
    public ResponseEntity<Map<String, Object>> getProductsForAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {
        try {
            logger.info("Fetching products for admin, page: {}, size: {}", page, size);
            if (page < 0 || size <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid page or size parameters"));
            }
            Page<Product> productPage = productService.getAllProductsForGrid(page, size);
            List<ProductDetailDTO> products = productPage.stream()
                    .map(productService::mapToDetailDTO)
                    .collect(Collectors.toList());
            Map<String, Object> response = new HashMap<>();
            response.put("products", products);
            response.put("totalPages", productPage.getTotalPages());
            response.put("currentPage", productPage.getNumber());
            response.put("totalItems", productPage.getTotalElements());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching products: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error: " + e.getMessage()));
        }
    }

    @PostMapping(value = "/products", consumes = {"multipart/form-data"})
    public ResponseEntity<Map<String, Object>> addProduct(
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("brand") String brandName,
            @RequestParam("category") String categoryName,
            @RequestParam Map<String, String> allParams,
            @RequestParam(value = "variants[0].images", required = false) List<MultipartFile> images) {
        try {
            logger.info("Adding new product: {}", name);
            logger.info("All params: {}", allParams);

            Brand brand = brandRepository.findByName(brandName)
                    .orElseGet(() -> {
                        Brand newBrand = new Brand();
                        newBrand.setName(brandName);
                        return brandRepository.save(newBrand);
                    });

            Category category = categoryRepository.findByName(categoryName)
                    .orElseGet(() -> {
                        Category newCategory = new Category();
                        newCategory.setName(categoryName);
                        return categoryRepository.save(newCategory);
                    });

            Product product = new Product();
            product.setName(name);
            product.setDescription(description);
            product.setBrand(brand);
            product.setCategory(category);
            product.setProductVariantList(new ArrayList<>());

            Map<Integer, ProductVariant> variants = new HashMap<>();
            allParams.forEach((key, value) -> {
                if (key.startsWith("variants[")) {
                    String[] parts = key.split("\\[|\\]|\\.");
                    if (parts.length == 4) {
                        int index = Integer.parseInt(parts[1]);
                        String field = parts[3];

                        variants.computeIfAbsent(index, k -> new ProductVariant());
                        ProductVariant variant = variants.get(index);

                        switch (field) {
                            case "attribute":
                                variant.setProductAttribute(value);
                                break;
                            case "variant":
                                variant.setVariant(value);
                                break;
                            case "price":
                                try {
                                    variant.setPrice(new BigDecimal(value));
                                } catch (NumberFormatException e) {
                                    logger.warn("Invalid price format for variant[{}]: {}", index, value);
                                }
                                break;
                            case "quantity":
                                try {
                                    variant.setQuantity(Integer.parseInt(value));
                                } catch (NumberFormatException e) {
                                    logger.warn("Invalid quantity format for variant[{}]: {}", index, value);
                                }
                                break;
                        }
                        variant.setProduct(product);
                    } else {
                        logger.warn("Invalid key format: {}", key);
                    }
                }
            });

            if (variants.isEmpty()) {
                logger.warn("No variants provided for product: {}", name);
            } else {
                for (ProductVariant variant : variants.values()) {
                    product.getProductVariantList().add(variant);
                    variant.setProductImageList(new ArrayList<>());
                }
            }

            Product savedProduct = productRepository.save(product);
            logger.info("Saved product and variants: {}", savedProduct);

            if (images != null && !images.isEmpty() && !variants.isEmpty()) {
                ProductVariant firstVariant = savedProduct.getProductVariantList().get(0);
                List<ProductImage> productImages = new ArrayList<>();
                for (int i = 0; i < images.size(); i++) {
                    MultipartFile file = images.get(i);
                    String imageUrl = cloudinaryService.upLoadImage(file);
                    String publicId = extractPublicIdFromUrl(imageUrl);
                    logger.info("Uploaded image to Cloudinary, public_id: {}", publicId);

                    Image image = new Image();
                    image.setPublicId(publicId);
                    Image savedImage = imageRepository.save(image);

                    ProductImage productImage = new ProductImage();
                    productImage.setProductVariant(firstVariant);
                    productImage.setImage(savedImage);
                    productImage.setMainImage(i == 0);
                    productImages.add(productImage);
                }
                productImageRepository.saveAll(productImages);
                firstVariant.getProductImageList().addAll(productImages);
                productRepository.save(savedProduct);
            }

            return ResponseEntity.ok(Map.of("message", "Product added successfully"));
        } catch (Exception e) {
            logger.error("Error adding product: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error: " + e.getMessage()));
        }
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable Long productId) {
        try {
            logger.info("Đang xóa sản phẩm với ID: {}", productId);
            productService.deleteProduct(productId);
            logger.info("Đã xóa thành công sản phẩm với ID: {}", productId);
            return ResponseEntity.ok(Map.of("message", "Xóa sản phẩm thành công"));
        } catch (IllegalArgumentException e) {
            logger.error("Lỗi khi xóa sản phẩm: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Lỗi hệ thống khi xóa sản phẩm: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Lỗi hệ thống, vui lòng thử lại sau"));
        }
    }

    private String extractPublicIdFromUrl(String imageUrl) {
        String[] parts = imageUrl.split("/");
        String lastPart = parts[parts.length - 1];
        return lastPart.substring(0, lastPart.lastIndexOf("."));
    }
}