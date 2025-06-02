package org.example.chuyendeweb_be.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.chuyendeweb_be.user.dto.OrderDTO;
import org.example.chuyendeweb_be.user.dto.OrderResponseDTO;
import org.example.chuyendeweb_be.user.entity.Order;
import org.example.chuyendeweb_be.user.service.OrderService;
import org.example.chuyendeweb_be.user.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    private final AuthService authService;

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderDTO orderDTO) {
        Long userId = authService.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).body(createResponse(false, "Người dùng chưa được xác thực"));
        }

        try {
            Order order = orderService.createOrder(userId, orderDTO);
            OrderResponseDTO responseDTO = convertToOrderResponseDTO(order);
            return ResponseEntity.ok(createResponse(true, "Tạo đơn hàng thành công", responseDTO));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(createResponse(false, e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getUserOrders() {
        Long userId = authService.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).body(createResponse(false, "Người dùng chưa được xác thực"));
        }

        List<Order> orders = orderService.getUserOrders(userId);
        List<OrderResponseDTO> responseDTOs = orders.stream()
                .map(this::convertToOrderResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(createResponse(true, "Lấy danh sách đơn hàng thành công", responseDTOs));
    }

    private OrderResponseDTO convertToOrderResponseDTO(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setBookingDate(order.getBookingDate());
        dto.setDeliveryDate(order.getDeliveryDate());
        dto.setConsigneeName(order.getConsigneeName());
        dto.setConsigneePhone(order.getConsigneePhone());
        dto.setAddress(order.getAddress());
        dto.setOrderNotes(order.getOrderNotes());
        dto.setShip(order.getShip());
        dto.setDiscountValue(order.getDiscountValue());
        dto.setTotalMoney(order.getTotalMoney());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setPaymentMethod(order.getPayment().getMethodName());
        return dto;
    }

    private Map<String, Object> createResponse(boolean success, String message) {
        Map<String, Object> res = new HashMap<>();
        res.put("success", success);
        res.put("message", message);
        return res;
    }

    private Map<String, Object> createResponse(boolean success, String message, Object data) {
        Map<String, Object> res = createResponse(success, message);
        res.put("data", data);
        return res;
    }
}