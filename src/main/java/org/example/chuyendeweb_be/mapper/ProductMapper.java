package org.example.chuyendeweb_be.mapper;

import org.example.chuyendeweb_be.dto.ImageDTO;
import org.example.chuyendeweb_be.dto.ProductDTO;
import org.example.chuyendeweb_be.dto.ProductImageDTO;
import org.example.chuyendeweb_be.dto.ProductVariantDTO;
import org.example.chuyendeweb_be.entity.Image;
import org.example.chuyendeweb_be.entity.Product;
import org.example.chuyendeweb_be.entity.ProductImage;
import org.example.chuyendeweb_be.entity.ProductVariant;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product toEntity(ProductDTO productDTO);

    ProductDTO toDTO(Product product);

    ProductVariant toVariantEntity(ProductVariantDTO productVariantDTO);

    ProductImage toProductImageEntity(ProductImageDTO productImageDTO);

    Image toImageEntity(ImageDTO imageDTO);

    ImageDTO toImageDTO(Image image);

    ProductVariantDTO toVariantDTO(ProductVariant productVariant);

    ProductImageDTO toProductImageDTO(ProductImage productImage);

}
