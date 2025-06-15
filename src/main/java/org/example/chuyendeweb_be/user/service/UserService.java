package org.example.chuyendeweb_be.user.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.example.chuyendeweb_be.user.dto.CreateUserDTO;
import org.example.chuyendeweb_be.user.dto.UpdateUserDTO;
import org.example.chuyendeweb_be.user.dto.UserDTO;
import org.example.chuyendeweb_be.user.entity.ResetPassword;
import org.example.chuyendeweb_be.user.entity.Role;
import org.example.chuyendeweb_be.user.entity.User;
import org.example.chuyendeweb_be.user.mapper.UserMapper;
import org.example.chuyendeweb_be.user.repository.PasswordResetTokenRepository;
import org.example.chuyendeweb_be.user.repository.RoleRepository;
import org.example.chuyendeweb_be.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
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

        var violations = validator.validate(createUserDTO);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        if (userRepository.findByUsername(createUserDTO.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Tên người dùng đã tồn tại");
        }
        if (userRepository.findByEmail(createUserDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email đã tồn tại");
        }

        Role role = roleRepository.findByRoleName(createUserDTO.getRoleName())
                .orElseThrow(() -> new IllegalArgumentException("Vai trò không tồn tại: " + createUserDTO.getRoleName()));

        User user = new User();
        user.setUsername(createUserDTO.getUsername());
        user.setEmail(createUserDTO.getEmail());
        user.setPhone(createUserDTO.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(createUserDTO.getPassword()));
        user.setRole(role);
        user.setFailed(0);
        user.setLocked(false);

        User savedUser = userRepository.save(user);
        logger.info("Đã tạo người dùng thành công: {}", savedUser.getUsername());

        return userMapper.toDto(savedUser);
    }

    public UserDTO updateUser(Long userId, UpdateUserDTO updateUserDTO, String authenticatedUsername) throws IllegalAccessException {
        Set<ConstraintViolation<UpdateUserDTO>> violations = validator.validate(updateUserDTO);
        if (!violations.isEmpty()) {
            logger.warn("Lỗi xác thực cho UpdateUserDTO: {}", violations);
            throw new ConstraintViolationException(violations);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        if (!user.getUsername().equals(authenticatedUsername)) {
            logger.warn("Người dùng {} cố gắng cập nhật hồ sơ của {}", authenticatedUsername, user.getUsername());
            throw new IllegalAccessException("Không có quyền cập nhật thông tin người dùng khác");
        }

        if (!user.getUsername().equals(updateUserDTO.getUsername()) &&
                userRepository.findByUsername(updateUserDTO.getUsername()).isPresent()) {
            logger.warn("Username {} đã tồn tại", updateUserDTO.getUsername());
            throw new IllegalArgumentException("Username đã tồn tại");
        }

        if (!user.getEmail().equals(updateUserDTO.getEmail()) &&
                userRepository.findByEmail(updateUserDTO.getEmail()).isPresent()) {
            logger.warn("Email {} đã tồn tại", updateUserDTO.getEmail());
            throw new IllegalArgumentException("Email đã tồn tại");
        }

        user.setUsername(updateUserDTO.getUsername());
        user.setEmail(updateUserDTO.getEmail());
        user.setPhone(updateUserDTO.getPhone());

        logger.info("Đang cập nhật người dùng ID: {} với dữ liệu mới - Username: {}, Email: {}, Phone: {}",
                userId, updateUserDTO.getUsername(), updateUserDTO.getEmail(), updateUserDTO.getPhone());

        User updatedUser = userRepository.save(user);
        logger.info("Đã lưu thành công người dùng: {}", updatedUser.getUsername());

        return userMapper.toDto(updatedUser);
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));

        userRepository.delete(user);
        logger.info("Đã xóa người dùng với ID: {}", userId);
    }

    public void createPasswordResetToken(String email, String frontendUrl) throws Exception {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new Exception("Không tìm thấy email.");
        }

        User user = userOpt.get();
        String token = UUID.randomUUID().toString();

        ResetPassword passwordResetToken = new ResetPassword();
        passwordResetToken.setToken(token);
        passwordResetToken.setUser(user);
        passwordResetToken.setExpiryDate(Instant.now().plus(15, ChronoUnit.MINUTES));

        tokenRepository.save(passwordResetToken);

        String resetUrl = frontendUrl + "/reset-password?token=" + token;
        emailService.sendSimpleMessage(
                user.getEmail(),
                "Đặt lại mật khẩu",
                "Bạn nhận được email này để đặt lại mật khẩu. Vui lòng truy cập liên kết sau:\n" + resetUrl +
                        "\nLiên kết sẽ hết hạn sau 15 phút."
        );
    }

    public void resetPassword(String token, String newPassword) throws Exception {
        Optional<ResetPassword> tokenOpt = tokenRepository.findByToken(token);
        if (tokenOpt.isEmpty()) {
            throw new Exception("Token không hợp lệ hoặc đã hết hạn.");
        }

        ResetPassword passwordResetToken = tokenOpt.get();

        if (passwordResetToken.getExpiryDate().isBefore(Instant.now())) {
            throw new Exception("Token đã hết hạn.");
        }

        User user = passwordResetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        tokenRepository.delete(passwordResetToken);
    }
}