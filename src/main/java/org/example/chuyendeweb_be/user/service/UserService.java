package org.example.chuyendeweb_be.user.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.example.chuyendeweb_be.user.dto.UpdateUserDTO;
import org.example.chuyendeweb_be.user.dto.UserDTO;
import org.example.chuyendeweb_be.user.entity.User;
import org.example.chuyendeweb_be.user.mapper.UserMapper;
import org.example.chuyendeweb_be.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private UserMapper userMapper;
    private final UserRepository userRepository;
    private final Validator validator;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public UserDTO updateUser(Long userId, UpdateUserDTO updateUserDTO, String authenticatedUsername) throws IllegalAccessException {
        // Xác thực dữ liệu đầu vào
        Set<ConstraintViolation<UpdateUserDTO>> violations = validator.validate(updateUserDTO);
        if (!violations.isEmpty()) {
            logger.warn("Lỗi xác thực cho UpdateUserDTO: {}", violations);
            throw new ConstraintViolationException(violations);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        // Đảm bảo người dùng chỉ có thể cập nhật hồ sơ của chính họ
        if (!user.getUsername().equals(authenticatedUsername)) {
            logger.warn("Người dùng {} cố gắng cập nhật hồ sơ của {}", authenticatedUsername, user.getUsername());
            throw new IllegalAccessException("Không có quyền cập nhật thông tin người dùng khác");
        }

        // Kiểm tra trùng username
        if (!user.getUsername().equals(updateUserDTO.getUsername()) &&
                userRepository.findByUsername(updateUserDTO.getUsername()).isPresent()) {
            logger.warn("Username {} đã tồn tại", updateUserDTO.getUsername());
            throw new IllegalArgumentException("Username đã tồn tại");
        }

        // Kiểm tra trùng email
        if (!user.getEmail().equals(updateUserDTO.getEmail()) &&
                userRepository.findByEmail(updateUserDTO.getEmail()).isPresent()) {
            logger.warn("Email {} đã tồn tại", updateUserDTO.getEmail());
            throw new IllegalArgumentException("Email đã tồn tại");
        }

        // Cập nhật thông tin người dùng
        user.setUsername(updateUserDTO.getUsername());
        user.setEmail(updateUserDTO.getEmail());
        user.setPhone(updateUserDTO.getPhone());

        logger.info("Đang cập nhật người dùng ID: {} với dữ liệu mới - Username: {}, Email: {}, Phone: {}",
                userId, updateUserDTO.getUsername(), updateUserDTO.getEmail(), updateUserDTO.getPhone());

        User updatedUser = userRepository.save(user);
        logger.info("Đã lưu thành công người dùng: {}", updatedUser.getUsername());

        return userMapper.toDto(updatedUser);
    }

}