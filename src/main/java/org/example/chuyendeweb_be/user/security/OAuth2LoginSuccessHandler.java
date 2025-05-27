package org.example.chuyendeweb_be.user.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.chuyendeweb_be.user.entity.Role;
import org.example.chuyendeweb_be.user.entity.User;
import org.example.chuyendeweb_be.user.repository.RoleRepository;
import org.example.chuyendeweb_be.user.repository.UserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final String frontendUrl = "http://localhost:3000"; // Cấu hình qua biến môi trường trong sản xuất

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        if (email == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"error\": \"Không lấy được email từ nhà cung cấp OAuth2\"}");
            return;
        }

        // Kiểm tra hoặc tạo người dùng
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            user = new User();
            user.setUsername(oAuth2User.getAttribute("name") != null ? oAuth2User.getAttribute("name") : email);
            user.setEmail(email);
            user.setPassword(""); // Không cần mật khẩu cho OAuth2
            user.setLocked(false);
            user.setFailed(0);
            user.setTokenVersion(jwtService.generateTokenVersion()); // Tạo tokenVersion mới

            Role defaultRole = roleRepository.findByRoleName("ROLE_CLIENT")
                    .orElseThrow(() -> new RuntimeException("ROLE_CLIENT not found in DB"));
            user.setRole(defaultRole);

            userRepository.save(user);
        } else {
            // Cập nhật tokenVersion nếu cần
            if (user.getTokenVersion() == null) {
                user.setTokenVersion(jwtService.generateTokenVersion());
                userRepository.save(user);
            }
        }

        // Tạo token
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        String accessToken = jwtService.generateToken(userDetails, user.getTokenVersion());
        String refreshToken = jwtService.generateRefreshToken(userDetails, user.getTokenVersion());

        // Tạo cookies với các thuộc tính bảo mật
        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(true) // Bật trong môi trường sản xuất với HTTPS
                .sameSite("Strict") // Chống CSRF
                .path("/")
                .maxAge(15 * 60) // 15 phút
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/api/auth/refresh-token")
                .maxAge(7 * 24 * 60 * 60) // 7 ngày
                .build();

        // Thêm cookies vào response
        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        // Tạo phản hồi JSON thay vì redirect với query parameter
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(
                String.format(
                        "{\"message\": \"Đăng nhập OAuth2 thành công\", " +
                                "\"accessToken\": \"%s\", " +
                                "\"refreshToken\": \"%s\", " +
                                "\"user\": {\"id\": %d, \"username\": \"%s\", \"email\": \"%s\"}}",
                        accessToken, refreshToken, user.getId(), user.getUsername(), user.getEmail()
                )
        );

        // Redirect tới frontend (không gửi token qua query parameter)
        String redirectUrl = frontendUrl + "/home";
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}