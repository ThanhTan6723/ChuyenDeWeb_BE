package org.example.chuyendeweb_be.user.service;

import org.example.chuyendeweb_be.user.dto.CategoryDTO;
import org.example.chuyendeweb_be.user.dto.DiscountTypeDTO;
import org.example.chuyendeweb_be.user.dto.ProductVariantDTO;
import org.example.chuyendeweb_be.user.dto.VoucherDTO;
import org.example.chuyendeweb_be.user.entity.Category;
import org.example.chuyendeweb_be.user.entity.DiscountType;
import org.example.chuyendeweb_be.user.entity.ProductVariant;
import org.example.chuyendeweb_be.user.entity.Voucher;
import org.example.chuyendeweb_be.user.repository.CategoryRepository;
import org.example.chuyendeweb_be.user.repository.DiscountTypeRepository;
import org.example.chuyendeweb_be.user.repository.ProductVariantRepository;
import org.example.chuyendeweb_be.user.repository.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VoucherService {

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private DiscountTypeRepository discountTypeRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public VoucherDTO createVoucher(VoucherDTO voucherDTO) {
        // Validate dates
        if (voucherDTO.getEndDate().isBefore(voucherDTO.getStartDate())) {
            throw new IllegalArgumentException("Ngày kết thúc phải sau ngày bắt đầu");
        }

        // Check if code already exists
        if (voucherRepository.existsByCode(voucherDTO.getCode())) {
            throw new IllegalArgumentException("Mã voucher đã tồn tại");
        }

        // Validate discount type requirements
        if (voucherDTO.getDiscountType().getId() == 2 && voucherDTO.getCategory() == null) {
            throw new IllegalArgumentException("Phải chọn danh mục khi loại giảm giá là 'Category'");
        }
        if (voucherDTO.getDiscountType().getId() == 3 && voucherDTO.getProductVariantDTO() == null) {
            throw new IllegalArgumentException("Phải chọn sản phẩm khi loại giảm giá là 'Product'");
        }
        if (voucherDTO.getDiscountType().getId() == 1 && (voucherDTO.getCategory() != null || voucherDTO.getProductVariantDTO() != null)) {
            throw new IllegalArgumentException("Không được chọn danh mục hoặc sản phẩm khi loại giảm giá là 'All'");
        }

        Voucher voucher = new Voucher();
        mapVoucherDTOToEntity(voucherDTO, voucher);

        Voucher savedVoucher = voucherRepository.save(voucher);
        return mapVoucherToDTO(savedVoucher);
    }

    public List<VoucherDTO> getAllVouchersForAdmin() {
        return voucherRepository.findAll().stream()
                .map(this::mapVoucherToDTO)
                .collect(Collectors.toList());
    }

    public List<VoucherDTO> getActiveVouchersForUser() {
        return voucherRepository.findAll().stream()
                .filter(voucher -> voucher.getIsActive() &&
                        !voucher.getEndDate().isBefore(LocalDate.now()) &&
                        !voucher.getStartDate().isAfter(LocalDate.now()) &&
                        voucher.getQuantity() > 0)
                .map(this::mapVoucherToDTO)
                .collect(Collectors.toList());
    }

    private void mapVoucherDTOToEntity(VoucherDTO dto, Voucher voucher) {
        voucher.setCode(dto.getCode());

        // Map discount type
        DiscountType discountType = discountTypeRepository.findById(dto.getDiscountType().getId())
                .orElseThrow(() -> new IllegalArgumentException("Loại giảm giá không tồn tại"));
        voucher.setDiscountType(discountType);

        voucher.setDiscountPercentage(dto.getDiscountPercentage());

        // Map product variant if present
        if (dto.getProductVariantDTO() != null) {
            ProductVariant productVariant = productVariantRepository.findById(dto.getProductVariantDTO().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Biến thể sản phẩm không tồn tại"));
            voucher.setProductVariant(productVariant);
        }

        // Map category if present
        if (dto.getCategory() != null) {
            Category category = categoryRepository.findById(dto.getCategory().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Danh mục không tồn tại"));
            voucher.setCategory(category);
        }

        voucher.setQuantity(dto.getQuantity());
        voucher.setStartDate(dto.getStartDate());
        voucher.setEndDate(dto.getEndDate());
        voucher.setMinimumOrderValue(dto.getMinimumOrderValue());
        voucher.setMaximumDiscount(dto.getMaximumDiscount());
        voucher.setIsActive(dto.getIsActive());
    }

    private VoucherDTO mapVoucherToDTO(Voucher voucher) {
        VoucherDTO dto = new VoucherDTO();
        dto.setId(voucher.getId());
        dto.setCode(voucher.getCode());
        dto.setDiscountType(new DiscountTypeDTO(voucher.getDiscountType().getId(), voucher.getDiscountType().getType()));
        dto.setDiscountPercentage(voucher.getDiscountPercentage());

        // Map ProductVariant to ProductVariantDTO
        if (voucher.getProductVariant() != null) {
            ProductVariantDTO productVariantDTO = new ProductVariantDTO();
            productVariantDTO.setId(voucher.getProductVariant().getId());
            productVariantDTO.setAttribute(voucher.getProductVariant().getProductAttribute());
            productVariantDTO.setVariant(voucher.getProductVariant().getVariant());
            productVariantDTO.setPrice(voucher.getProductVariant().getPrice());
            productVariantDTO.setQuantity(voucher.getProductVariant().getQuantity());
            dto.setProductVariantDTO(productVariantDTO);
        }

        // Map Category to CategoryDTO
        if (voucher.getCategory() != null) {
            dto.setCategory(new CategoryDTO(voucher.getCategory().getId(), voucher.getCategory().getName()));
        }

        dto.setQuantity(voucher.getQuantity());
        dto.setStartDate(voucher.getStartDate());
        dto.setEndDate(voucher.getEndDate());
        dto.setMinimumOrderValue(voucher.getMinimumOrderValue());
        dto.setMaximumDiscount(voucher.getMaximumDiscount());
        dto.setIsActive(voucher.getIsActive());
        return dto;
    }
}