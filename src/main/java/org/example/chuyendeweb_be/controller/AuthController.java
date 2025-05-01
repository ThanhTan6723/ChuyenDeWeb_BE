package org.example.chuyendeweb_be.controller;

import lombok.RequiredArgsConstructor;
import org.example.chuyendeweb_be.dto.AuthResponseDTO;
import org.example.chuyendeweb_be.dto.LoginRequestDTO;
import org.example.chuyendeweb_be.dto.RegisterRequestDTO;
import org.example.chuyendeweb_be.service.AuthService;
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
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }
}

