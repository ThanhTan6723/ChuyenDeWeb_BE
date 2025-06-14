package org.example.chuyendeweb_be.user.controller;

import org.example.chuyendeweb_be.user.dto.CategoryDTO;
import org.example.chuyendeweb_be.user.dto.ResponseDTO;
import org.example.chuyendeweb_be.user.dto.VoucherDTO;
import org.example.chuyendeweb_be.user.repository.CategoryRepository;
import org.example.chuyendeweb_be.user.repository.ProductVariantRepository;
import org.example.chuyendeweb_be.user.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api")
public class VoucherController {

    @Autowired
    private VoucherService voucherService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @PostMapping("/vouchers")
    public ResponseEntity<ResponseDTO<VoucherDTO>> createVoucher(@Valid @RequestBody VoucherDTO voucherDTO) {
        try {
            VoucherDTO createdVoucher = voucherService.createVoucher(voucherDTO);
            return ResponseEntity.ok(new ResponseDTO<>("success", "Tạo voucher thành công", createdVoucher));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ResponseDTO<>("error", e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>("error", "Lỗi hệ thống khi tạo voucher", null));
        }
    }

    @GetMapping("/vouchers/admin")
    public ResponseEntity<ResponseDTO<List<VoucherDTO>>> getAllVouchersForAdmin() {
        try {
            List<VoucherDTO> vouchers = voucherService.getAllVouchersForAdmin();
            return ResponseEntity.ok(new ResponseDTO<>("success", "Lấy danh sách voucher thành công", vouchers));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>("error", "Lỗi hệ thống khi lấy danh sách voucher", null));
        }
    }

    @GetMapping("/vouchers/user")
    public ResponseEntity<ResponseDTO<List<VoucherDTO>>> getActiveVouchersForUser() {
        try {
            List<VoucherDTO> vouchers = voucherService.getActiveVouchersForUser();
            return ResponseEntity.ok(new ResponseDTO<>("success", "Lấy danh sách voucher đang hoạt động thành công", vouchers));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>("error", "Lỗi hệ thống khi lấy danh sách voucher", null));
        }
    }

    @GetMapping("/categories")
    public ResponseEntity<ResponseDTO<List<CategoryDTO>>> getAllCategories() {
        try {
            List<CategoryDTO> categories = categoryRepository.findAll().stream()
                    .map(category -> new CategoryDTO(category.getId(), category.getName()))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(new ResponseDTO<>("success", "Lấy danh sách danh mục thành công", categories));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>("error", "Lỗi hệ thống khi lấy danh sách danh mục", null));
        }
    }

    @GetMapping("/product-variants")
    public ResponseEntity<ResponseDTO<List<Map<String, Object>>>> getAllProductVariants() {
        try {
            List<Map<String, Object>> productVariants = productVariantRepository.findAll().stream()
                    .map(productVariant -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", productVariant.getId());
                        map.put("productName", productVariant.getProduct().getName());
                        map.put("attribute", productVariant.getProductAttribute());
                        map.put("variant", productVariant.getVariant());
                        map.put("price", productVariant.getPrice());
                        map.put("quantity", productVariant.getQuantity());
                        return map;
                    })
                    .collect(Collectors.toList());
            return ResponseEntity.ok(new ResponseDTO<>("success", "Lấy danh sách biến thể sản phẩm thành công", productVariants));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>("error", "Lỗi hệ thống khi lấy danh sách biến thể sản phẩm", null));
        }
    }

    // Lấy danh sách voucher đã lưu của user
    @GetMapping("/e-vouchers/user")
    public ResponseEntity<ResponseDTO<List<VoucherDTO>>> getUserSavedVouchers(Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ResponseDTO<>("error", "Chưa đăng nhập", null));
            }
            String username = authentication.getName();
            List<VoucherDTO> vouchers = voucherService.getUserSavedVouchers(username);
            return ResponseEntity.ok(new ResponseDTO<>("success", "Lấy danh sách voucher đã lưu thành công", vouchers));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>("error", "Lỗi hệ thống khi lấy danh sách voucher đã lưu", null));
        }
    }

    // Lưu voucher cho user
    @PostMapping("/e-vouchers")
    public ResponseEntity<ResponseDTO<String>> saveVoucherForUser(
            @RequestBody Map<String, Long> request, Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ResponseDTO<>("error", "Chưa đăng nhập", null));
            }
            String username = authentication.getName();
            Long voucherId = request.get("voucherId");
            voucherService.saveVoucherForUser(username, voucherId);
            return ResponseEntity.ok(new ResponseDTO<>("success", "Lưu voucher thành công", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ResponseDTO<>("error", e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>("error", "Lỗi hệ thống khi lưu voucher", null));
        }
    }

    // Kiểm tra user đã lưu voucher chưa
    @GetMapping("/e-vouchers/check")
    public ResponseEntity<ResponseDTO<Boolean>> checkUserSavedVoucher(
            @RequestParam Long voucherId, Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ResponseDTO<>("error", "Chưa đăng nhập", null));
            }
            String username = authentication.getName();
            boolean isSaved = voucherService.checkUserSavedVoucher(username, voucherId);
            return ResponseEntity.ok(new ResponseDTO<>("success", "Kiểm tra thành công", isSaved));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO<>("error", "Lỗi hệ thống khi kiểm tra voucher", null));
        }
    }
}