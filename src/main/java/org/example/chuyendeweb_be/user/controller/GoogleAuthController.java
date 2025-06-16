//package org.example.chuyendeweb_be.user.controller;
//
//import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
//import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
//import com.google.api.client.http.javanet.NetHttpTransport;
//import com.google.api.client.json.gson.GsonFactory;
//import lombok.Data;
//import lombok.RequiredArgsConstructor;
//import org.example.chuyendeweb_be.user.dto.AuthResponseDTO;
//import org.example.chuyendeweb_be.user.entity.User;
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
//
//    @Value("${spring.security.oauth2.client.registration.google.client-id}")
//    private String googleClientId;
//
//    @PostMapping("/google")
//    public ResponseEntity<?> googleLogin(@RequestBody GoogleLoginRequest request) {
//        logger.info("⚡ Received Google login request");
//
//        try {
//            // Verify id_token
//            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
//                    new NetHttpTransport(), GsonFactory.getDefaultInstance())
//                    .setAudience(Collections.singletonList(googleClientId))
//                    .build();
//
//            GoogleIdToken idToken = verifier.verify(request.getIdToken());
//            if (idToken == null) {
//                logger.warning("❌ Invalid or expired ID Token");
//                return ResponseEntity.badRequest().body(Map.of("error", "Invalid Google ID token"));
//            }
//
//            GoogleIdToken.Payload payload = idToken.getPayload();
//            String email = payload.getEmail();
//            String name = (String) payload.get("name");
//
//            logger.info("✅ Valid token - Email: " + email + ", Name: " + name);
//
//            // Find or create user
//            User user = findOrCreateUser(email, name);
//
//            // Create JWT token
//            UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
//                    .username(user.getEmail())
//                    .password("")
//                    .authorities(user.getRole().getRoleName())
//                    .build();
//
//            AuthResponseDTO authResponse = jwtService.generateOAuth2Tokens(userDetails, user.getTokenVersion());
//
//            // Create cookies
//            HttpHeaders headers = createAuthCookies(authResponse);
//
//            logger.info("✅ Successful Google login for: " + user.getUsername());
//            return ResponseEntity.ok()
//                    .headers(headers)
//                    .body(Map.of(
//                            "message", "Google login successful",
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
//            logger.severe("🔥 Google login error: " + e.getMessage());
//            return ResponseEntity.badRequest().body(Map.of("error", "Google login error: " + e.getMessage()));
//        }
//    }
//
//    private User findOrCreateUser(String email, String name) {
//        Optional<User> optionalUser = userRepository.findByEmailWithRole(email);
//
//        if (optionalUser.isPresent()) {
//            logger.info("🔁 Existing user: " + email);
//            return optionalUser.get();
//        }
//
//        logger.info("🆕 Creating new user: " + email);
//        User user = new User();
//        user.setUsername(name != null ? name : email.split("@")[0]);
//        user.setEmail(email);
//        user.setPassword("");
//        user.setLocked(false);
//        user.setFailed(0);
//        user.setTokenVersion(jwtService.generateTokenVersion());
//
//        user.setRole(jwtService.getDefaultRole());
//        return userRepository.save(user);
//    }
//
//    private HttpHeaders createAuthCookies(AuthResponseDTO authResponse) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.add(HttpHeaders.SET_COOKIE, ResponseCookie.from("accessToken", authResponse.getAccessToken())
//                .httpOnly(true)
//                .secure(true)
//                .sameSite("None")
//                .path("/")
//                .maxAge(15 * 60)
//                .build().toString());
//
//        headers.add(HttpHeaders.SET_COOKIE, ResponseCookie.from("refreshToken", authResponse.getRefreshToken())
//                .httpOnly(true)
//                .secure(true)
//                .sameSite("None")
//                .path("/api/auth/refresh-token")
//                .maxAge(7 * 24 * 60 * 60)
//                .build().toString());
//
//        return headers;
//    }
//
//    @Data
//    static class GoogleLoginRequest {
//        private String idToken;
//    }
//}