package org.example.chuyendeweb_be.user.controller;

import org.example.chuyendeweb_be.user.dto.CartItemDTO;
import org.example.chuyendeweb_be.user.entity.CartItem;
import org.example.chuyendeweb_be.user.entity.ProductImage;
import org.example.chuyendeweb_be.user.service.CartService;
import org.example.chuyendeweb_be.user.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private AuthService authService;

    @GetMapping
    public ResponseEntity<List<CartItemDTO>> getCart(HttpServletRequest request) {
        Long userId = authService.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<CartItem> cartItems = cartService.getCartItemsByUserId(userId);
        List<CartItemDTO> cartItemDTOs = cartItems.stream().map(this::convertToCartItemDTO).collect(Collectors.toList());
        return ResponseEntity.ok(cartItemDTOs);
    }

    @PostMapping
    public ResponseEntity<CartItemDTO> addToCart(@RequestBody CartItemDTO cartItemDTO, HttpServletRequest request) {
        // Kiểm tra dữ liệu đầu vào
        if (cartItemDTO == null || cartItemDTO.getProductVariantId() == null || cartItemDTO.getQuantity() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        Long userId = authService.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            CartItem cartItem = cartService.addCartItem(userId, cartItemDTO.getProductVariantId(), cartItemDTO.getQuantity());
            CartItemDTO responseDTO = convertToCartItemDTO(cartItem);
            return ResponseEntity.ok(responseDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping
    public ResponseEntity<List<CartItemDTO>> updateCart(@RequestBody List<CartItemDTO> cartItemDTOs, HttpServletRequest request) {
        Long userId = authService.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<CartItem> updatedItems = cartService.updateCart(userId, cartItemDTOs);
        List<CartItemDTO> responseItems = updatedItems.stream()
                .map(this::convertToCartItemDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseItems);
    }

    @DeleteMapping("/{productVariantId}")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long productVariantId, HttpServletRequest request) {
        Long userId = authService.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            cartService.removeCartItem(userId, productVariantId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    private CartItemDTO convertToCartItemDTO(CartItem cartItem) {
        CartItemDTO dto = new CartItemDTO();

        // Thông tin biến thể sản phẩm
        dto.setProductVariantId(cartItem.getProductVariant().getId());
        dto.setQuantity(cartItem.getQuantity());
        dto.setPrice(cartItem.getProductVariant().getPrice());

        // Chi tiết biến thể sản phẩm
        dto.setAttribute(cartItem.getProductVariant().getProductAttribute());
        dto.setVariant(cartItem.getProductVariant().getVariant());

        // Thông tin sản phẩm
        dto.setProductId(cartItem.getProductVariant().getProduct().getId());
        dto.setProductName(cartItem.getProductVariant().getProduct().getName());
        dto.setBrandName(cartItem.getProductVariant().getProduct().getBrand().getName());
        dto.setCategoryName(cartItem.getProductVariant().getProduct().getCategory().getName());

        // Xử lý hình ảnh
        List<ProductImage> images = cartItem.getProductVariant().getProductImageList();

        // Đặt hình ảnh chính
        images.stream()
                .filter(ProductImage::isMainImage)
                .findFirst()
                .ifPresent(mainImage ->
                        dto.setMainImageUrl("https://res.cloudinary.com/your-cloud-name/image/upload/" + mainImage.getImage())
                );

        // Đặt các hình ảnh phụ
        List<String> additionalImages = images.stream()
                .filter(img -> !img.isMainImage())
                .map(img -> "https://res.cloudinary.com/your-cloud-name/image/upload/" + img.getImage())
                .collect(Collectors.toList());
        dto.setAdditionalImageUrls(additionalImages);

        return dto;
    }
}