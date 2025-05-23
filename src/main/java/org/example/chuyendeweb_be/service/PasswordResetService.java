package org.example.chuyendeweb_be.service;

import org.example.chuyendeweb_be.entity.PasswordResetToken;
import org.example.chuyendeweb_be.entity.User;
import org.example.chuyendeweb_be.repository.PasswordResetTokenRepository;
import org.example.chuyendeweb_be.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    public void createPasswordResetToken(String email, String frontendUrl) throws Exception {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new Exception("Không tìm thấy email.");
        }

        User user = userOpt.get();

        // Tạo token mới
        String token = UUID.randomUUID().toString();

        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken(token);
        passwordResetToken.setUser(user);
        passwordResetToken.setExpiryDate(Instant.now().plus(15, ChronoUnit.MINUTES)); // Token hết hạn sau 15 phút

        tokenRepository.save(passwordResetToken);

        // Gửi email
        String resetUrl = frontendUrl + "/reset-password?token=" + token;
        emailService.sendSimpleMessage(
                user.getEmail(),
                "Đặt lại mật khẩu",
                "Bạn nhận được email này để đặt lại mật khẩu. Vui lòng truy cập liên kết sau:\n" + resetUrl +
                        "\nLiên kết sẽ hết hạn sau 15 phút."
        );
    }

    public void resetPassword(String token, String newPassword) throws Exception {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);
        if (tokenOpt.isEmpty()) {
            throw new Exception("Token không hợp lệ hoặc đã hết hạn.");
        }

        PasswordResetToken passwordResetToken = tokenOpt.get();

        if (passwordResetToken.getExpiryDate().isBefore(Instant.now())) {
            throw new Exception("Token đã hết hạn.");
        }

        User user = passwordResetToken.getUser();

        // Mã hóa mật khẩu mới
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Xóa token sau khi đổi mật khẩu thành công
        tokenRepository.delete(passwordResetToken);
    }
}
