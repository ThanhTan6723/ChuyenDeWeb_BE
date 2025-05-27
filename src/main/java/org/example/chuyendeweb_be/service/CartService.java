package org.example.chuyendeweb_be.service;

import org.example.chuyendeweb_be.dto.CartItemDTO;
import org.example.chuyendeweb_be.entity.Cart;
import org.example.chuyendeweb_be.entity.CartItem;
import org.example.chuyendeweb_be.entity.ProductVariant;
import org.example.chuyendeweb_be.repository.CartRepository;
import org.example.chuyendeweb_be.repository.CartItemRepository;
import org.example.chuyendeweb_be.repository.ProductVariantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Transactional
    public List<CartItem> getCartItemsByUserId(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    return cartRepository.save(newCart);
                });
        return cartItemRepository.findByCartId(cart.getId());
    }

    @Transactional
    public CartItem addCartItem(Long userId, Long productVariantId, Integer quantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    return cartRepository.save(newCart);
                });

        ProductVariant productVariant = productVariantRepository.findById(productVariantId)
                .orElseThrow(() -> new IllegalArgumentException("Product variant not found"));

        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProductVariant().getId().equals(productVariantId))
                .findFirst();

        CartItem cartItem;
        if (existingItem.isPresent()) {
            cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProductVariant(productVariant);
            cartItem.setQuantity(quantity);
            cart.getCartItems().add(cartItem);
        }
        return cartItemRepository.save(cartItem);
    }

    @Transactional
    public List<CartItem> updateCart(Long userId, List<CartItemDTO> cartItemDTOs) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        // Clear existing items
        cartItemRepository.deleteByCartId(cart.getId());
        cart.getCartItems().clear();

        // Add new items
        List<CartItem> updatedItems = cartItemDTOs.stream()
                .map(dto -> {
                    ProductVariant productVariant = productVariantRepository.findById(dto.getProductVariantId())
                            .orElseThrow(() -> new IllegalArgumentException("Product variant not found"));

                    CartItem cartItem = new CartItem();
                    cartItem.setCart(cart);
                    cartItem.setProductVariant(productVariant);
                    cartItem.setQuantity(dto.getQuantity());
                    return cartItem;
                })
                .collect(Collectors.toList());

        cart.setCartItems(updatedItems);
        cartRepository.save(cart);
        return cartItemRepository.saveAll(updatedItems);
    }

    @Transactional
    public void removeCartItem(Long userId, Long productVariantId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        CartItem itemToRemove = cart.getCartItems().stream()
                .filter(item -> item.getProductVariant().getId().equals(productVariantId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));

        cart.getCartItems().remove(itemToRemove);
        cartItemRepository.delete(itemToRemove);
        cartRepository.save(cart);
    }
}