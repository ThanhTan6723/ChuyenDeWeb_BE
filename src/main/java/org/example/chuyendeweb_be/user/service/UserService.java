package org.example.chuyendeweb_be.user.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.example.chuyendeweb_be.user.dto.CreateUserDTO;
import org.example.chuyendeweb_be.user.dto.UpdateUserDTO;
import org.example.chuyendeweb_be.user.dto.UserDTO;
import org.example.chuyendeweb_be.user.entity.Role;
import org.example.chuyendeweb_be.user.entity.User;
import org.example.chuyendeweb_be.user.mapper.UserMapper;
import org.example.chuyendeweb_be.user.repository.RoleRepository;
import org.example.chuyendeweb_be.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private UserMapper userMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final Validator validator;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<UserDTO> getAllUsers() {
        logger.info("Fetching all users");
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }
    public UserDTO createUser(CreateUserDTO createUserDTO) {
        logger.info("Tạo người dùng mới: {}", createUserDTO.getUsername());

        // Validate DTO
        var violations = validator.validate(createUserDTO);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        // Kiểm tra username và email đã tồn tại
        if (userRepository.findByUsername(createUserDTO.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Tên người dùng đã tồn tại");
        }
        if (userRepository.findByEmail(createUserDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email đã tồn tại");
        }

        // Tìm role
        Role role = roleRepository.findByRoleName(createUserDTO.getRoleName())
                .orElseThrow(() -> new IllegalArgumentException("Vai trò không tồn tại: " + createUserDTO.getRoleName()));

        // Tạo user entity
        User user = new User();
        user.setUsername(createUserDTO.getUsername());
        user.setEmail(createUserDTO.getEmail());
        user.setPhone(createUserDTO.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(createUserDTO.getPassword()));
        user.setRole(role);
        user.setFailed(0);
        user.setLocked(false);

        // Lưu user
        User savedUser = userRepository.save(user);
        logger.info("Đã tạo người dùng thành công: {}", savedUser.getUsername());

        return userMapper.toDto(savedUser);
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