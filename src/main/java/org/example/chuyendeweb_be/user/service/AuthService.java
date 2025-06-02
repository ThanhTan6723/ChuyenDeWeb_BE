package org.example.chuyendeweb_be.user.service;

import lombok.RequiredArgsConstructor;
import org.example.chuyendeweb_be.user.dto.AuthResponseDTO;
import org.example.chuyendeweb_be.user.dto.LoginRequestDTO;
import org.example.chuyendeweb_be.user.dto.RegisterRequestDTO;
import org.example.chuyendeweb_be.user.entity.Role;
import org.example.chuyendeweb_be.user.entity.User;
import org.example.chuyendeweb_be.user.mapper.UserMapper;
import org.example.chuyendeweb_be.user.repository.RoleRepository;
import org.example.chuyendeweb_be.user.repository.UserRepository;
import org.example.chuyendeweb_be.user.security.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public AuthResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent())
            throw new RuntimeException("Tên người dùng đã tồn tại");

        if (userRepository.findByEmail(request.getEmail()).isPresent())
            throw new RuntimeException("Email đã tồn tại");

        if (userRepository.findByEmailOrPhone(null, request.getPhone()).isPresent())
            throw new RuntimeException("Số điện thoại đã tồn tại");

        Role userRole = roleRepository.findByRoleName("ROLE_CLIENT")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò mặc định"));

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(userRole);
        user.setTokenVersion(jwtService.generateTokenVersion()); // Tạo token version
        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        return new AuthResponseDTO(
                jwtService.generateToken(userDetails, user.getTokenVersion()),
                jwtService.generateRefreshToken(userDetails, user.getTokenVersion())
        );
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        User user = userRepository.findByEmailOrPhone(request.getEmail(), request.getPhone())
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy email hoặc số điện thoại"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new BadCredentialsException("Mật khẩu không hợp lệ");

        userRepository.save(user);

        UserDetails userDetails = loadUserDetails(user);
        return new AuthResponseDTO(
                jwtService.generateToken(userDetails, user.getTokenVersion()),
                jwtService.generateRefreshToken(userDetails, user.getTokenVersion())
        );
    }

    public AuthResponseDTO refreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BadCredentialsException("Refresh token bị thiếu");
        }

        String username = jwtService.extractUsername(refreshToken);
        if (username == null) {
            throw new BadCredentialsException("Refresh token không hợp lệ");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng: " + username));

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (!jwtService.isRefreshTokenValid(refreshToken, userDetails, user.getTokenVersion())) {
            throw new BadCredentialsException("Refresh token đã hết hạn hoặc không hợp lệ");
        }

        userRepository.save(user);

        return new AuthResponseDTO(
                jwtService.generateToken(userDetails, user.getTokenVersion()),
                jwtService.generateRefreshToken(userDetails, user.getTokenVersion())
        );
    }

    private UserDetails loadUserDetails(User user) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(new SimpleGrantedAuthority(user.getRole().getRoleName()))
                .build();
    }

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() instanceof String) {
            throw new UsernameNotFoundException("Không tìm thấy người dùng đã xác thực");
        }

        Object principal = authentication.getPrincipal();
        String username = ((UserDetails) principal).getUsername();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với tên: " + username));
        return user.getId();
    }
}