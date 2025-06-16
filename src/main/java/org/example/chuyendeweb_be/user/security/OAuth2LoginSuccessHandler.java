//package org.example.chuyendeweb_be.user.security;
//
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.example.chuyendeweb_be.user.dto.AuthResponseDTO;
//import org.example.chuyendeweb_be.user.entity.Role;
//import org.example.chuyendeweb_be.user.entity.User;
//import org.example.chuyendeweb_be.user.repository.RoleRepository;
//import org.example.chuyendeweb_be.user.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.ResponseCookie;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.util.logging.Logger;
//
//@Component
//@RequiredArgsConstructor
//public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
//
//    private static final Logger logger = Logger.getLogger(OAuth2LoginSuccessHandler.class.getName());
//    private final JwtService jwtService;
//    private final CustomUserDetailsService userDetailsService;
//    private final UserRepository userRepository;
//    private final RoleRepository roleRepository;
//    private final String frontendUrl = "https://localhost:3000";
//
//    @Value("${spring.security.oauth2.client.registration.google.client-id}")
//    private String googleClientId;
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
//                                        Authentication authentication) throws IOException, ServletException {
//        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
//        String email = oAuth2User.getAttribute("email");
//        String name = oAuth2User.getAttribute("name");
//
//        if (email == null) {
//            logger.severe("Failed to retrieve email from OAuth2 provider");
//            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Không lấy được email từ Google");
//            return;
//        }
//
//        try {
//            // Tải hoặc tạo user
//            User user = userRepository.findByEmailWithRole(email).orElseGet(() -> {
//                User newUser = new User();
//                newUser.setUsername(name != null ? name : email.split("@")[0]);
//                newUser.setEmail(email);
//                newUser.setPassword("");
//                newUser.setLocked(false);
//                newUser.setFailed(0);
//                newUser.setTokenVersion(jwtService.generateTokenVersion());
//                Role defaultRole = roleRepository.findByRoleName("ROLE_CLIENT")
//                        .orElseThrow(() -> new RuntimeException("ROLE_CLIENT not found"));
//                newUser.setRole(defaultRole);
//                userRepository.save(newUser);
//                logger.info("Created new user with email: " + email);
//                return newUser;
//            });
//
//            // Cập nhật tokenVersion nếu null
//            if (user.getTokenVersion() == null) {
//                user.setTokenVersion(jwtService.generateTokenVersion());
//                userRepository.save(user);
//                logger.info("Updated token version for user: " + email);
//            }
//
//            // Tạo token
//            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
//            AuthResponseDTO authResponse = jwtService.generateOAuth2Tokens(userDetails, user.getTokenVersion());
//
//            // Tạo cookie
//            ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", authResponse.getAccessToken())
//                    .httpOnly(true)
//                    .secure(true)
//                    .sameSite("None")
//                    .path("/")
//                    .maxAge(15 * 60)
//                    .build();
//
//            ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", authResponse.getRefreshToken())
//                    .httpOnly(true)
//                    .secure(true)
//                    .sameSite("None")
//                    .path("/api/auth/refresh-token")
//                    .maxAge(30 * 60)
//                    .build();
//
//            response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
//            response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
//            logger.info("Set cookies: accessToken=[hidden], refreshToken=[hidden]");
//
//            // Redirect tới frontend dựa trên vai trò
//            String redirectUrl = user.getRole().getRoleName().equals("ROLE_ADMIN") ?
//                    frontendUrl + "/admin" : frontendUrl + "/home";
//            logger.info("Redirecting to: " + redirectUrl);
//            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
//        } catch (Exception ex) {
//            logger.severe("OAuth2 login error for email: " + email + ", message: " + ex.getMessage());
//            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi đăng nhập Google: " + ex.getMessage());
//        }
//    }
//}