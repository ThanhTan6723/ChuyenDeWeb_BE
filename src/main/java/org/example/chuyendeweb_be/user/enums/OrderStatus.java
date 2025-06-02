package org.example.chuyendeweb_be.user.enums;

public enum OrderStatus {
    PENDING,      // Chờ xác nhận
    CONFIRMED,    // Đã xác nhận
    ON_DELIVERY,    //Đang giao hàng
    DELIVERED,    // Giao thành công
    CANCELLED,     // Đã hủy
    REFUSED, // Đã từ chối
}