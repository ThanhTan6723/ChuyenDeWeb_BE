package org.example.chuyendeweb_be.controller;

import lombok.RequiredArgsConstructor;
import org.example.chuyendeweb_be.dto.AuthResponseDTO;
import org.example.chuyendeweb_be.dto.LoginRequestDTO;
import org.example.chuyendeweb_be.dto.RegisterRequestDTO;
import org.example.chuyendeweb_be.service.AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody RegisterRequestDTO request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        AuthResponseDTO authResponse = authService.login(request);

        // Tạo cookie access token
        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", authResponse.getAccessToken())
                .httpOnly(true) // Chặn JavaScript truy cập (chống XSS)
                .secure(false)  // Đặt true nếu chạy HTTPS
                .path("/")
                .maxAge(24 * 60 * 60) // 1 ngày
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .body("Login success!");
    }


}

