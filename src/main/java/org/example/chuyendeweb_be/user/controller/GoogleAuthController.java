//package org.example.chuyendeweb_be.user.controller;
//
//import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
//import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
//import com.google.api.client.http.javanet.NetHttpTransport;
//import com.google.api.client.json.gson.GsonFactory;
//import lombok.Data;
//import lombok.RequiredArgsConstructor;
//import org.example.chuyendeweb_be.user.dto.AuthResponseDTO;
//import org.example.chuyendeweb_be.user.entity.Role;
//import org.example.chuyendeweb_be.user.entity.User;
//import org.example.chuyendeweb_be.user.repository.RoleRepository;
//import org.example.chuyendeweb_be.user.repository.UserRepository;
//import org.example.chuyendeweb_be.user.security.JwtService;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.ResponseCookie;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Collections;
//import java.util.Map;
//import java.util.Optional;
//import java.util.logging.Logger;
//
//@RestController
//@RequestMapping("/api/auth")
//@RequiredArgsConstructor
//public class GoogleAuthController {
//    private static final Logger logger = Logger.getLogger(GoogleAuthController.class.getName());
//
//    private final JwtService jwtService;
//    private final UserRepository userRepository;
//    private final RoleRepository roleRepository;
//
//    @Value("${spring.security.oauth2.client.registration.google.client-id}")
//    private String googleClientId;
//
//    @PostMapping("/google")
//    public ResponseEntity<?> googleLogin(@RequestBody GoogleLoginRequest request) {
//        logger.info("⚡ Nhận yêu cầu đăng nhập bằng Google");
//
//        try {
//            logger.info("📥 ID Token nhận được: " + request.getIdToken());
//
//            // Xác minh id_token
//            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
//                    new NetHttpTransport(), GsonFactory.getDefaultInstance())
//                    .setAudience(Collections.singletonList(googleClientId))
//                    .build();
//
//            GoogleIdToken idToken = verifier.verify(request.getIdToken());
//            if (idToken == null) {
//                logger.warning("❌ ID Token không hợp lệ hoặc hết hạn");
//                return ResponseEntity.badRequest().body(Map.of("error", "Invalid Google ID token"));
//            }
//
//            GoogleIdToken.Payload payload = idToken.getPayload();
//            String email = payload.getEmail();
//            String name = (String) payload.get("name");
//
//            logger.info("✅ Token hợp lệ - Email: " + email + ", Name: " + name);
//
//            // Tìm hoặc tạo mới user
//            Optional<User> optionalUser = userRepository.findByEmailWithRole(email);
//            User user;
//
//            if (optionalUser.isPresent()) {
//                user = optionalUser.get();
//                logger.info("🔁 User đã tồn tại: " + user.getUsername());
//            } else {
//                logger.info("🆕 User chưa tồn tại, tiến hành tạo mới");
//
//                user = new User();
//                user.setUsername(name != null ? name : email.split("@")[0]);
//                user.setEmail(email);
//                user.setPassword("");
//                user.setLocked(false);
//                user.setFailed(0);
//                user.setTokenVersion(jwtService.generateTokenVersion());
//
//                Role defaultRole = roleRepository.findByRoleName("ROLE_CLIENT")
//                        .orElseThrow(() -> {
//                            logger.severe("🚨 ROLE_CLIENT không tồn tại trong cơ sở dữ liệu");
//                            return new RuntimeException("ROLE_CLIENT not found");
//                        });
//
//                user.setRole(defaultRole);
//                userRepository.save(user);
//                logger.info("✅ Tạo user mới thành công: " + user.getUsername());
//            }
//
//            // Tạo JWT token
//            UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
//                    .username(user.getEmail())
//                    .password("")
//                    .authorities(user.getRole().getRoleName())
//                    .build();
//
//            AuthResponseDTO authResponse = jwtService.generateOAuth2Tokens(userDetails, user.getTokenVersion());
//
//            logger.info("🔐 Tạo JWT thành công");
//            logger.info("📦 AccessToken (truncated): " + authResponse.getAccessToken().substring(0, 20) + "...");
//            logger.info("📦 RefreshToken (truncated): " + authResponse.getRefreshToken().substring(0, 20) + "...");
//
//            // Tạo cookie
//            HttpHeaders headers = new HttpHeaders();
//            headers.add(HttpHeaders.SET_COOKIE, ResponseCookie.from("accessToken", authResponse.getAccessToken())
//                    .httpOnly(true)
//                    .secure(true)
//                    .sameSite("None")
//                    .path("/")
//                    .maxAge(15 * 60)
//                    .build().toString());
//
//            headers.add(HttpHeaders.SET_COOKIE, ResponseCookie.from("refreshToken", authResponse.getRefreshToken())
//                    .httpOnly(true)
//                    .secure(true)
//                    .sameSite("None")
//                    .path("/api/auth/refresh-token")
//                    .maxAge(30 * 60)
//                    .build().toString());
//
//            logger.info("🍪 Cookie đã được set thành công");
//
//            return ResponseEntity.ok()
//                    .headers(headers)
//                    .body(Map.of(
//                            "message", "Đăng nhập Google thành công",
//                            "accessToken", authResponse.getAccessToken(),
//                            "refreshToken", authResponse.getRefreshToken(),
//                            "user", Map.of(
//                                    "id", user.getId(),
//                                    "username", user.getUsername(),
//                                    "email", user.getEmail(),
//                                    "roleName", user.getRole().getRoleName()
//                            )
//                    ));
//
//        } catch (Exception e) {
//            logger.severe("🔥 Lỗi xử lý đăng nhập Google: " + e.getMessage());
//            e.printStackTrace();
//            return ResponseEntity.badRequest().body(Map.of("error", "Lỗi đăng nhập Google: " + e.getMessage()));
//        }
//    }
//
//    @Data
//    static class GoogleLoginRequest {
//        private String idToken;
//    }
//}
