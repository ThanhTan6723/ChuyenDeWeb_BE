package org.example.chuyendeweb_be.user.dto;

import lombok.Data;

@Data
public class ShippingFeeRequest {
    private String city;
    private String district;
    private String ward;
    private String address;
    private double weight;
    private double value;

}
