package org.example.chuyendeweb_be.user.enums;

public enum OrderStatus {
    PENDING,      // Đơn mới tạo
    CONFIRMED,    // Đã xác nhận
    SHIPPED,      // Đã giao cho đơn vị vận chuyển
    ON_DELIVERY,    //Đang giao
    DELIVERED,    // Đã giao thành công
    CANCELLED,     // Đã hủy
    REFUSED, // Đã từ chối
}