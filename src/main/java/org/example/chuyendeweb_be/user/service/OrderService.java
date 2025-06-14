package org.example.chuyendeweb_be.user.service;

import lombok.RequiredArgsConstructor;
import org.example.chuyendeweb_be.user.dto.OrderDTO;
import org.example.chuyendeweb_be.user.dto.OrderDetailDTO;
import org.example.chuyendeweb_be.user.dto.OrderDetailResponseDTO;
import org.example.chuyendeweb_be.user.entity.*;
import org.example.chuyendeweb_be.user.enums.OrderStatus;
import org.example.chuyendeweb_be.user.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final PaymentRepository paymentRepository;
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

        if (orderDTO.getOrderDetails() == null || orderDTO.getOrderDetails().isEmpty()) {
            throw new IllegalArgumentException("Danh sách sản phẩm trong đơn hàng không được để trống");
        }

        Payment payment = paymentRepository.findById(orderDTO.getPaymentId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phương thức thanh toán"));

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
        order.setOrderStatus(OrderStatus.PENDING);
        order.setPayment(payment);

        // Calculate total money and create order details
        BigDecimal totalMoney = BigDecimal.ZERO;
        List<OrderDetail> orderDetails = new ArrayList<>();
        List<Long> cartItemIdsToDelete = new ArrayList<>();
        List<Long> paidProductVariantIds = orderDTO.getOrderDetails().stream()
                .map(OrderDetailDTO::getProductVariantId)
                .collect(Collectors.toList());

        // Process cart items for the order
        for (CartItem cartItem : new ArrayList<>(cart.getCartItems())) {
            ProductVariant variant = cartItem.getProductVariant();

            // Only process items that match the product variant IDs in the order
            if (paidProductVariantIds.contains(variant.getId())) {
                // Find corresponding OrderDetailDTO for quantity
                OrderDetailDTO orderDetailDTO = orderDTO.getOrderDetails().stream()
                        .filter(dto -> dto.getProductVariantId().equals(variant.getId()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thông tin sản phẩm trong đơn hàng cho variant: " + variant.getId()));

                // Check stock
                if (variant.getQuantity() < orderDetailDTO.getQuantity()) {
                    throw new IllegalStateException("Insufficient stock for product variant: " + variant.getId());
                }

                // Create order detail
                OrderDetail detail = new OrderDetail();
                detail.setOrder(order);
                detail.setVariant(variant);
                detail.setQuantity(orderDetailDTO.getQuantity());
                detail.setProductPrice(variant.getPrice());
                detail.setPriceWithQuantity(variant.getPrice().multiply(BigDecimal.valueOf(orderDetailDTO.getQuantity())));

                orderDetails.add(detail);
                totalMoney = totalMoney.add(detail.getPriceWithQuantity());
                cartItemIdsToDelete.add(cartItem.getId());

                // Update product variant quantity
                variant.setQuantity(variant.getQuantity() - orderDetailDTO.getQuantity());
                productVariantRepository.save(variant);
            }
        }

        if (orderDetails.isEmpty()) {
            throw new IllegalArgumentException("Không có sản phẩm hợp lệ nào trong đơn hàng");
        }

        // Add shipping cost and apply discount
        totalMoney = totalMoney.add(order.getShip()).subtract(order.getDiscountValue());
        order.setTotalMoney(totalMoney);

        // Save order and order details
        Order savedOrder = orderRepository.save(order);
        orderDetails.forEach(detail -> detail.setOrder(savedOrder));
        orderDetailRepository.saveAll(orderDetails);

        // Delete only the cart items that were included in the order
        if (!cartItemIdsToDelete.isEmpty()) {
            cartItemRepository.deleteAllById(cartItemIdsToDelete);
            cart.getCartItems().removeIf(item -> cartItemIdsToDelete.contains(item.getId()));
            cartRepository.save(cart);
        }

        return savedOrder;
    }

    public Page<Order> getUserOrders(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable);
    }

    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    public Page<Order> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        return orderRepository.findByOrderStatus(status, pageable);
    }

    public Page<Order> getUserOrdersByStatus(Long userId, OrderStatus status, Pageable pageable) {
        return orderRepository.findByUserIdAndOrderStatus(userId, status, pageable);
    }

    public List<OrderDetailResponseDTO> getOrderDetails(Long orderId) {
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(orderId);
        return orderDetails.stream()
                .map(this::convertToOrderDetailResponseDTO)
                .collect(Collectors.toList());
    }

    private OrderDetailResponseDTO convertToOrderDetailResponseDTO(OrderDetail detail) {
        OrderDetailResponseDTO dto = new OrderDetailResponseDTO();
        dto.setId(detail.getId());
        dto.setVariantId(detail.getVariant().getId());
        dto.setProductName(detail.getVariant().getProduct().getName());
        dto.setVariantAttribute(detail.getVariant().getProductAttribute());
        dto.setVariantName(detail.getVariant().getVariant());
        dto.setQuantity(detail.getQuantity());
        dto.setProductPrice(detail.getProductPrice());
        dto.setPriceWithQuantity(detail.getPriceWithQuantity());

        // Get the main image from the product variant
        if (!detail.getVariant().getProductImageList().isEmpty()) {
            dto.setMainImage(detail.getVariant().getProductImageList().get(0).getImage().getPublicId());
        }

        return dto;
    }
}