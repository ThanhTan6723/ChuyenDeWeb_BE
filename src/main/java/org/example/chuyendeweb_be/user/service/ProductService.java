package org.example.chuyendeweb_be.user.service;

import org.example.chuyendeweb_be.user.dto.ImageDTO;
import org.example.chuyendeweb_be.user.dto.ProductDetailDTO;
import org.example.chuyendeweb_be.user.dto.ProductGridDTO;
import org.example.chuyendeweb_be.user.dto.ProductVariantDTO;
import org.example.chuyendeweb_be.user.entity.Product;
import org.example.chuyendeweb_be.user.entity.ProductImage;
import org.example.chuyendeweb_be.user.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductRepository productRepository;

    public Page<Product> getAllProductsForGrid(int page, int size) {
        logger.info("Fetching products for page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findAll(pageable);
    }

    public List<Product> getBestSellers(int size) {
        logger.info("Fetching best sellers, size: {}", size);
        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "salesCount"));
        return productRepository.findAll(pageable).getContent();
    }

    public ProductDetailDTO getProductDetails(Long id) {
        logger.info("Fetching details for product ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
        return mapToDetailDTO(product);
    }

    private ProductDetailDTO mapToDetailDTO(Product product) {
        ProductDetailDTO dto = new ProductDetailDTO();
        dto.setId(product.getId());
        dto.setName(product.getName() != null ? product.getName() : "Unknown");
        dto.setDescription(product.getDescription() != null ? product.getDescription() : "");
        dto.setBrand(product.getBrand() != null ? product.getBrand().getName() : "Unknown");
        dto.setCategory(product.getCategory() != null ? product.getCategory().getName() : "Unknown");

        List<ProductVariantDTO> variants = product.getProductVariantList() != null
                ? product.getProductVariantList().stream().map(variant -> {
            ProductVariantDTO variantDTO = new ProductVariantDTO();
            variantDTO.setId(variant.getId());
            variantDTO.setAttribute(variant.getProductAttribute() != null ? variant.getProductAttribute() : "N/A");
            variantDTO.setVariant(variant.getVariant() != null ? variant.getVariant() : "N/A");
            variantDTO.setPrice(variant.getPrice() != null ? variant.getPrice(): new BigDecimal(0));
            variantDTO.setQuantity(variant.getQuantity() != 0 ? variant.getQuantity() : 0);

            List<ImageDTO> images = variant.getProductImageList() != null
                    ? variant.getProductImageList().stream().map(img -> {
                ImageDTO imageDTO = new ImageDTO();
                imageDTO.setPublicId(img.getImage() != null && img.getImage().getPublicId() != null
                        ? img.getImage().getPublicId() : "default_image");
                imageDTO.setMain(img.isMainImage());
                return imageDTO;
            }).collect(Collectors.toList())
                    : Collections.singletonList(new ImageDTO("default_image", true));

            variantDTO.setImages(images);
            return variantDTO;
        }).collect(Collectors.toList())
                : Collections.singletonList(new ProductVariantDTO(null, "N/A", "N/A", new BigDecimal(0), 0, Collections.singletonList(new ImageDTO("default_image", true))));

        dto.setVariants(variants);
        return dto;
    }

    public List<ProductGridDTO> mapToDTO(Page<Product> productPage) {
        return productPage.stream().map(this::mapProductToDTO).collect(Collectors.toList());
    }

    public List<ProductGridDTO> mapToDTO(List<Product> products) {
        return products.stream().map(this::mapProductToDTO).collect(Collectors.toList());
    }

    private ProductGridDTO mapProductToDTO(Product product) {
        ProductGridDTO dto = new ProductGridDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setBrand(product.getBrand() != null ? product.getBrand().getName() : "Unknown");
        dto.setCategory(product.getCategory() != null ? product.getCategory().getName() : "Unknown");

        if (!product.getProductVariantList().isEmpty()) {
            var variants = product.getProductVariantList();
            var mainVariant = variants.stream()
                    .filter(v -> v.getProductImageList().stream().anyMatch(ProductImage::isMainImage))
                    .findFirst()
                    .orElseGet(() -> {
                        logger.warn("No main image found for product ID: {}. Using first variant.", product.getId());
                        return variants.get(0);
                    });

            dto.setPrice(mainVariant.getPrice());
            dto.setStock(mainVariant.getQuantity());
            dto.setAttributes(mainVariant.getProductAttribute() + " - " + mainVariant.getVariant());

            mainVariant.getProductImageList().stream()
                    .filter(ProductImage::isMainImage)
                    .findFirst()
                    .ifPresent(img -> dto.setMainImageUrl(img.getImage().getPublicId()));
        } else {
            logger.warn("No variants found for product ID: {}", product.getId());
            dto.setStock(0);
            dto.setAttributes("N/A");
        }
        return dto;
    }


}