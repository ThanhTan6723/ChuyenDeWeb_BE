package org.example.chuyendeweb_be.user.service;

import lombok.RequiredArgsConstructor;
import org.example.chuyendeweb_be.user.dto.OrderDTO;
import org.example.chuyendeweb_be.user.dto.OrderDetailDTO;
import org.example.chuyendeweb_be.user.entity.*;
import org.example.chuyendeweb_be.user.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductVariantRepository productVariantRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Transactional
    public Order createOrder(Long userId, OrderDTO orderDTO) {
        // Validate user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Find cart
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        if (cart.getCartItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        // Create new order
        Order order = new Order();
        order.setUser(user);
        order.setBookingDate(Instant.now());
        order.setConsigneeName(orderDTO.getConsigneeName());
        order.setConsigneePhone(orderDTO.getConsigneePhone());
        order.setAddress(orderDTO.getAddress());
        order.setOrderNotes(orderDTO.getOrderNotes());
        order.setShip(orderDTO.getShip());
        order.setDiscountValue(orderDTO.getDiscountValue() != null ? orderDTO.getDiscountValue() : BigDecimal.ZERO);
        order.setOrderStatus("PENDING");

        // Calculate total money and create order details
        BigDecimal totalMoney = BigDecimal.ZERO;
        List<OrderDetail> orderDetails = new ArrayList<>();

        for (CartItem cartItem : cart.getCartItems()) {
            ProductVariant variant = cartItem.getProductVariant();

            // Check stock
            if (variant.getQuantity() < cartItem.getQuantity()) {
                throw new IllegalStateException("Insufficient stock for product variant: " + variant.getId());
            }

            // Create order detail
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setVariant(variant);
            detail.setQuantity(cartItem.getQuantity());
            detail.setProductPrice(variant.getPrice());
            detail.setPriceWithQuantity(variant.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));

            orderDetails.add(detail);
            totalMoney = totalMoney.add(detail.getPriceWithQuantity());

            // Update product variant quantity
            variant.setQuantity(variant.getQuantity() - cartItem.getQuantity());
            productVariantRepository.save(variant);
        }

        // Add shipping cost and apply discount
        totalMoney = totalMoney.add(order.getShip()).subtract(order.getDiscountValue());
        order.setTotalMoney(totalMoney);

        // Save order and order details
        Order savedOrder = orderRepository.save(order);
        orderDetails.forEach(detail -> detail.setOrder(savedOrder));
        orderDetailRepository.saveAll(orderDetails);

        // Clear cart items properly
        cartItemRepository.deleteByCartId(cart.getId());
        cart.getCartItems().clear();
        cartRepository.save(cart);

        return savedOrder;
    }

    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId);
    }
}