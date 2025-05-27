package org.example.chuyendeweb_be.user.mapper;

import org.example.chuyendeweb_be.user.dto.ImageDTO;
import org.example.chuyendeweb_be.user.dto.ProductDTO;
import org.example.chuyendeweb_be.user.dto.ProductImageDTO;
import org.example.chuyendeweb_be.user.dto.ProductVariantDTO;
import org.example.chuyendeweb_be.user.entity.Image;
import org.example.chuyendeweb_be.user.entity.Product;
import org.example.chuyendeweb_be.user.entity.ProductImage;
import org.example.chuyendeweb_be.user.entity.ProductVariant;
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
