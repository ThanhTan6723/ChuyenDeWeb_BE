//package org.example.chuyendeweb_be.service;
//
//import org.example.chuyendeweb_be.mapper.UserMapper;
//import org.example.chuyendeweb_be.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//@Service
//public class AuthServiceImpl implements AuthService {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private UserMapper userMapper;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Autowired
//    private JwtProvider jwtProvider;
//
//    @Override
//    public UserResponseDto register(UserRegisterDto dto) {
//        if (userRepository.existsByUsername(dto.getUsername()))
//            throw new RuntimeException("Username taken!");
//        if (userRepository.existsByEmail(dto.getEmail()))
//            throw new RuntimeException("Email taken!");
//
//        User user = userMapper.toEntity(dto);
//        user.setPassword(passwordEncoder.encode(dto.getPassword()));
//        user.setRoles("ROLE_USER");
//        userRepository.save(user);
//
//        return userMapper.toDto(user);
//    }
//
//    @Override
//    public String login(UserLoginDto dto) {
//        User user = userRepository.findByUsername(dto.getUsername())
//                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
//
//        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword()))
//            throw new RuntimeException("Invalid credentials");
//
//        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
//                .username(user.getUsername())
//                .password(user.getPassword())
//                .roles(user.getRoles().replace("ROLE_", ""))
//                .build();
//
//        return jwtProvider.generateToken(userDetails);
//    }
//}
