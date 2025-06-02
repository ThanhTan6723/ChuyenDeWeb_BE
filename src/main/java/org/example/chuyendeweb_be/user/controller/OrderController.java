package org.example.chuyendeweb_be.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.chuyendeweb_be.user.dto.*;
import org.example.chuyendeweb_be.user.entity.Order;
import org.example.chuyendeweb_be.user.entity.User;
import org.example.chuyendeweb_be.user.enums.OrderStatus;
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

    @GetMapping("/all")
    public ResponseEntity<?> getAllOrders() {
        try {
            List<Order> orders = orderService.getAllOrders();
            List<OrderResponseDTO> responseDTOs = orders.stream()
                    .map(this::convertToOrderResponseDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(createResponse(true, "Lấy danh sách đơn hàng thành công", responseDTOs));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createResponse(false, "Lỗi khi lấy danh sách đơn hàng: " + e.getMessage()));
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getOrdersByStatus(@PathVariable OrderStatus status) {
        try {
            List<Order> orders = orderService.getOrdersByStatus(status);
            List<OrderResponseDTO> responseDTOs = orders.stream()
                    .map(this::convertToOrderResponseDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(createResponse(true, "Lấy danh sách đơn hàng thành công", responseDTOs));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createResponse(false, "Lỗi khi lấy danh sách đơn hàng: " + e.getMessage()));
        }
    }

    @GetMapping("/{orderId}/details")
    public ResponseEntity<?> getOrderDetails(@PathVariable Long orderId) {
        try {
            List<OrderDetailResponseDTO> orderDetails = orderService.getOrderDetails(orderId);
            return ResponseEntity.ok(createResponse(true, "Lấy chi tiết đơn hàng thành công", orderDetails));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createResponse(false, "Lỗi khi lấy chi tiết đơn hàng: " + e.getMessage()));
        }
    }

    private OrderResponseDTO convertToOrderResponseDTO(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setUser(convertToUserDTO(order.getUser())); // Chuyển đổi User thành UserDTO
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

    private UserDTO convertToUserDTO(User user) {
        if (user == null) {
            return null;
        }
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        // RoleDTO cần được chuyển đổi nếu có, giả sử RoleDTO có constructor hoặc getter/setter tương ứng
        if (user.getRole() != null) {
            RoleDTO roleDTO = new RoleDTO();
            roleDTO.setId(user.getRole().getId());
            roleDTO.setRoleName(user.getRole().getRoleName());
            dto.setRole(roleDTO);
        }
        dto.setFailed(user.getFailed());
        dto.setLocked(user.getLocked());
        dto.setLockTime(user.getLockTime());
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