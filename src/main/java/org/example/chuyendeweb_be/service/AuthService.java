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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
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

    public AuthResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent())
            throw new RuntimeException("Username already exists");

        Role userRole = roleRepository.findByRoleName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Default role not found"));

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(userRole);
        userRepository.save(user);

        UserDetails userDetails = loadUserDetails(user);
        return new AuthResponseDTO(jwtService.generateToken(userDetails), jwtService.generateRefreshToken(userDetails));
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new BadCredentialsException("Invalid password");

        UserDetails userDetails = loadUserDetails(user);
        return new AuthResponseDTO(jwtService.generateToken(userDetails), jwtService.generateRefreshToken(userDetails));
    }

    private UserDetails loadUserDetails(User user) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(new SimpleGrantedAuthority(user.getRole().getRoleName()))
                .build();
    }
}

