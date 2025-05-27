package org.example.chuyendeweb_be.service;

import lombok.RequiredArgsConstructor;
import org.example.chuyendeweb_be.dto.AuthResponseDTO;
import org.example.chuyendeweb_be.dto.LoginRequestDTO;
import org.example.chuyendeweb_be.dto.RegisterRequestDTO;
import org.example.chuyendeweb_be.entity.Role;
import org.example.chuyendeweb_be.entity.User;
import org.example.chuyendeweb_be.mapper.UserMapper;
import org.example.chuyendeweb_be.repository.RoleRepository;
import org.example.chuyendeweb_be.repository.UserRepository;
import org.example.chuyendeweb_be.security.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

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
            throw new RuntimeException("Username already exists");

        Role userRole = roleRepository.findByRoleName("ROLE_CLIENT")
                .orElseThrow(() -> new RuntimeException("Default role not found"));

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(userRole);
        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        return new AuthResponseDTO(
                jwtService.generateToken(userDetails),
                jwtService.generateRefreshToken(userDetails)
        );
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        System.out.println("Login with email: " + request.getEmail() + ", phone: " + request.getPhone());
        User user = userRepository.findByEmailOrPhone(request.getEmail(), request.getPhone())
                .orElseThrow(() -> new UsernameNotFoundException("Email or phone not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new BadCredentialsException("Invalid password");

        UserDetails userDetails = loadUserDetails(user);
        return new AuthResponseDTO(
                jwtService.generateToken(userDetails),
                jwtService.generateRefreshToken(userDetails)
        );
    }

    public AuthResponseDTO refreshToken(String refreshToken) {
        // Xác thực refresh token
        String username = jwtService.extractUsername(refreshToken);
        if (username == null) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        // Tải thông tin người dùng
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Kiểm tra xem refresh token có hợp lệ không
        if (!jwtService.isTokenValid(refreshToken, userDetails)) {
            throw new BadCredentialsException("Refresh token expired or invalid");
        }

        // Tạo mới access token và refresh token
        return new AuthResponseDTO(
                jwtService.generateToken(userDetails),
                jwtService.generateRefreshToken(userDetails)
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
            throw new UsernameNotFoundException("No authenticated user found");
        }

        Object principal = authentication.getPrincipal();
        String username = ((UserDetails) principal).getUsername();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return user.getId();
    }
}