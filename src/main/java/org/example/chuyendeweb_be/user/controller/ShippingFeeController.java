package org.example.chuyendeweb_be.user.controller;

import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@RestController
@RequestMapping("/api/shipping")
public class ShippingFeeController {

    private static final String API_URL = "https://services.giaohangtietkiem.vn/services/shipment/fee";
    private static final String TOKEN = "ee42b44d5c4e4824e2f7d0d1cc74af58d328ccee";
    private static final String SHOP_PROVINCE = "Hồ Chí Minh";
    private static final String SHOP_DISTRICT = "Thủ Đức";
    private static final String SHOP_WARD = "Phường Linh Trung";
    private static final String SHOP_STREET = "Khu phố 6";

    public static class ShippingFeeRequest {
        private String city;
        private String district;
        private String ward;
        private String address;
        private double weight;
        private int value;

        // Getters and setters
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getDistrict() { return district; }
        public void setDistrict(String district) { this.district = district; }
        public String getWard() { return ward; }
        public void setWard(String ward) { this.ward = ward; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public double getWeight() { return weight; }
        public void setWeight(double weight) { this.weight = weight; }
        public int getValue() { return value; }
        public void setValue(int value) { this.value = value; }
    }

    @PostMapping("/fee")
    public ResponseEntity<?> getShippingFee(@RequestBody ShippingFeeRequest request) {
        try {
            // Validate input
            if (request.getCity() == null || request.getDistrict() == null || request.getWard() == null ||
                    request.getAddress() == null || request.getWeight() <= 0 || request.getValue() < 0) {
                return ResponseEntity.badRequest().body("Invalid input parameters");
            }

            HttpClient client = HttpClient.newHttpClient();

            // Build JSON request body for GHTK API
            JSONObject requestBodyJson = new JSONObject();
            requestBodyJson.put("pick_province", SHOP_PROVINCE);
            requestBodyJson.put("pick_district", SHOP_DISTRICT);
            requestBodyJson.put("pick_ward", SHOP_WARD);
            requestBodyJson.put("pick_street", SHOP_STREET);
            requestBodyJson.put("province", request.getCity());
            requestBodyJson.put("district", request.getDistrict());
            requestBodyJson.put("ward", request.getWard());
            requestBodyJson.put("address", request.getAddress());
            requestBodyJson.put("weight", Math.ceil(request.getWeight()));
            requestBodyJson.put("value", request.getValue());

            // Build HTTP request
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(new URI(API_URL))
                    .header("Token", TOKEN)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson.toString()))
                    .build();

            // Send request to GHTK API
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            // Handle response
            if (response.statusCode() == 200) {
                JSONObject json = new JSONObject(response.body());
                if (json.getBoolean("success")) {
                    double shipMoney = json.getJSONObject("fee").getDouble("fee");
                    return ResponseEntity.ok().body(shipMoney);
                } else {
                    return ResponseEntity.status(400).body("GHTK API Error: " + json.getString("message"));
                }
            } else {
                return ResponseEntity.status(response.statusCode()).body("Error from GHTK: " + response.body());
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Server Error: " + e.getMessage());
        }
    }
}