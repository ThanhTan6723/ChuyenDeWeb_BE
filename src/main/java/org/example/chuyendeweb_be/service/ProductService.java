package org.example.chuyendeweb_be.service;

import org.example.chuyendeweb_be.dto.ProductGridDTO;
import org.example.chuyendeweb_be.entity.Product;
import org.example.chuyendeweb_be.entity.ProductImage;
import org.example.chuyendeweb_be.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public Page<Product> getAllProductsForGrid(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findAll(pageable);
    }

    public List<ProductGridDTO> mapToDTO(Page<Product> productPage) {
        return productPage.stream().map(product -> {
            ProductGridDTO dto = new ProductGridDTO();
            dto.setId(product.getId());
            dto.setName(product.getName());
            dto.setDescription(product.getDescription());
            dto.setBrand(product.getBrand() != null ? product.getBrand().getName() : "Unknown");
            dto.setCategory(product.getCategory() != null ? product.getCategory().getName() : "Unknown");

            if (!product.getProductVariantList().isEmpty()) {
                var variants = product.getProductVariantList();
                // Find the variant with main_image = true
                var mainVariant = variants.stream()
                        .filter(v -> v.getProductImageList().stream().anyMatch(ProductImage::isMainImage))
                        .findFirst()
                        .orElse(variants.get(0)); // Fallback to first variant if no main image

                // Use details from the main variant
                dto.setPrice(mainVariant.getPrice());
                dto.setStock(mainVariant.getQuantity());
                dto.setAttributes(mainVariant.getProductAttribute() + " - " + mainVariant.getVariant());

                // Set main image URL from Cloudinary
                mainVariant.getProductImageList().stream()
                        .filter(ProductImage::isMainImage)
                        .findFirst()
                        .ifPresent(img -> dto.setMainImageUrl(img.getImage().getPublicId()));
            } else {
                dto.setPrice(product.getPrice() != null ? product.getPrice() : 0.0);
                dto.setStock(0);
                dto.setAttributes("N/A");
            }
            return dto;
        }).collect(Collectors.toList());
    }
}