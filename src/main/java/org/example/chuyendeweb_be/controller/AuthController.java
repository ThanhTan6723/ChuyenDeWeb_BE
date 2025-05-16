package org.example.chuyendeweb_be.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.chuyendeweb_be.dto.AuthResponseDTO;
import org.example.chuyendeweb_be.dto.LoginRequestDTO;
import org.example.chuyendeweb_be.dto.RegisterRequestDTO;
import org.example.chuyendeweb_be.entity.User;
import org.example.chuyendeweb_be.repository.UserRepository;
import org.example.chuyendeweb_be.security.JwtService;
import org.example.chuyendeweb_be.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO request) {
        AuthResponseDTO authResponse = authService.register(request);
        return ResponseEntity.ok()
                .headers(createAuthCookies(authResponse))
                .body(authResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequestDTO request) {
        AuthResponseDTO authResponse = authService.login(request);

        // Get user details after successful login
        User user = userRepository.findByEmailOrPhone(request.getEmail(), request.getPhone())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Map<String, Object> responseBody = Map.of(
                "message", "Login successful!",
                "accessToken", authResponse.getAccessToken(),
                "refreshToken", authResponse.getRefreshToken(),
                "user", Map.of(
                        "id", user.getId(),
                        "username", user.getUsername(),
                        "email", user.getEmail()
                )
        );

        return ResponseEntity.ok()
                .headers(createAuthCookies(authResponse))
                .body(responseBody);
    }


    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@CookieValue(name = "refreshToken", required = false) String refreshToken) {
        if (refreshToken == null) {
            return ResponseEntity.badRequest().body("Refresh token is required");
        }

        try {
            AuthResponseDTO authResponse = authService.refreshToken(refreshToken);

            // Get user from token
            String emailOrPhone = jwtService.extractUsername(authResponse.getAccessToken());
            User user = userRepository.findByEmailOrPhone(emailOrPhone, emailOrPhone)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            Map<String, Object> responseBody = Map.of(
                    "accessToken", authResponse.getAccessToken(),
                    "refreshToken", authResponse.getRefreshToken(),
                    "user", Map.of(
                            "id", user.getId(),
                            "username", user.getUsername(),
                            "email", user.getEmail()
                    )
            );

            return ResponseEntity.ok()
                    .headers(createAuthCookies(authResponse))
                    .body(responseBody);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid refresh token");
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // Tạo cookie trống để xóa token
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, createClearedCookie("accessToken").toString());
        headers.add(HttpHeaders.SET_COOKIE, createClearedCookie("refreshToken").toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body("Logged out successfully");
    }

    private HttpHeaders createAuthCookies(AuthResponseDTO authResponse) {
        HttpHeaders headers = new HttpHeaders();

        // Tạo cookie access token
        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", authResponse.getAccessToken())
                .httpOnly(true)       // Chặn JavaScript truy cập (chống XSS)
                .secure(true)         // Chỉ gửi qua HTTPS
                .sameSite("Strict")   // Chống CSRF
                .path("/")
                .maxAge(15 * 60)      // 15 phút (khớp với thời gian hết hạn của token)
                .build();

        // Tạo cookie refresh token
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", authResponse.getRefreshToken())
                .httpOnly(true)       // Chặn JavaScript truy cập (chống XSS)
                .secure(true)         // Chỉ gửi qua HTTPS
                .sameSite("Strict")   // Chống CSRF
                .path("/api/auth/refresh-token") // Chỉ gửi tới endpoint refresh token
                .maxAge(24 * 60 * 60) // 1 ngày
                .build();

        headers.add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return headers;
    }

    private ResponseCookie createClearedCookie(String name) {
        return ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Strict")
                .path(name.equals("refreshToken") ? "/api/auth/refresh-token" : "/")
                .maxAge(0)
                .build();
    }
}