package org.example.chuyendeweb_be.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.chuyendeweb_be.entity.Role;
import org.example.chuyendeweb_be.entity.User;
import org.example.chuyendeweb_be.repository.RoleRepository;
import org.example.chuyendeweb_be.repository.UserRepository;
import org.example.chuyendeweb_be.dto.AuthResponseDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
//import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;

    private final CustomUserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final String frontendUrl = "http://localhost:3000"; // Địa chỉ frontend


//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
//                                        Authentication authentication) throws IOException, ServletException {
//
//        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
//
//        String email = oAuth2User.getAttribute("email");
//
//        User user = userRepository.findByEmail(email).orElseGet(() -> {
//            User newUser = new User();
//            newUser.setUsername(email);
//            newUser.setEmail(email);
//            newUser.setPassword("");
//            newUser.setLocked(false);
//            newUser.setFailed(0);
//
//            Role defaultRole = roleRepository.findByRoleName("ROLE_CLIENT")
//                    .orElseThrow(() -> new RuntimeException("Default role not found"));
//            newUser.setRole(defaultRole);
//
//            return userRepository.save(newUser);
//        });
//
//        // Lấy UserDetails từ database
//        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
//
//
//        // Tạo token trực tiếp từ UserDetails (không ép kiểu gì nữa)
//        String accessToken = jwtService.generateToken(userDetails);
//        String refreshToken = jwtService.generateRefreshToken(userDetails);
//
//        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", accessToken)
//                .httpOnly(true)
//                .secure(true)
//                .sameSite("Strict")
//                .path("/")
//                .maxAge(15 * 60)
//                .build();
//
//        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
//                .httpOnly(true)
//                .secure(true)
//                .sameSite("Strict")
//                .path("/api/auth/refresh-token")
//                .maxAge(24 * 60 * 60)
//                .build();
//
//        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
//        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
//
//        //getRedirectStrategy().sendRedirect(request, response, frontendUrl);
//        String redirectUrl = frontendUrl + "/home?token=" + URLEncoder.encode(accessToken, StandardCharsets.UTF_8);
//        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
//
//    }
@Override
public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                    Authentication authentication) throws IOException, ServletException {

    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
    String email = oAuth2User.getAttribute("email");
    System.out.println("OAuth2 login attempt with email: " + email);

    // Nếu email null, báo lỗi
    if (email == null) {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Không lấy được email từ Google");
        return;
    }

    // Kiểm tra xem user đã tồn tại trong DB chưa
    User user = userRepository.findByEmail(email).orElse(null);

    if (user == null) {
        System.out.println("Email chưa có trong hệ thống, tạo user mới.");

        user = new User();
        user.setUsername(email); // hoặc oAuth2User.getAttribute("name") nếu muốn dùng tên đầy đủ
        user.setEmail(email);
        user.setPassword(""); // không cần password vì dùng Google
        user.setLocked(false);
        user.setFailed(0);

        // Gán role mặc định
        Role defaultRole = roleRepository.findByRoleName("ROLE_CLIENT")
                .orElseThrow(() -> new RuntimeException("ROLE_CLIENT not found in DB"));
        user.setRole(defaultRole);

        userRepository.save(user);
    } else {
        System.out.println("Đã tồn tại user: " + user.getEmail());
    }

    // Lấy UserDetails để tạo JWT
    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
    String accessToken = jwtService.generateToken(userDetails);
    String refreshToken = jwtService.generateRefreshToken(userDetails);

    // Tạo cookies (tùy chỉnh secure tùy môi trường)
    ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", accessToken)
            .httpOnly(true)
            .secure(false) // đổi thành true nếu dùng HTTPS
            .sameSite("Lax")
            .path("/")
            .maxAge(15 * 60)
            .build();

    ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
            .httpOnly(true)
            .secure(false)
            .sameSite("Lax")
            .path("/api/auth/refresh-token")
            .maxAge(24 * 60 * 60)
            .build();

    response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
    response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

    // Redirect về frontend kèm accessToken (cho frontend lưu vào localStorage nếu muốn)
    String redirectUrl = frontendUrl + "/home?token=" + URLEncoder.encode(accessToken, StandardCharsets.UTF_8);
    getRedirectStrategy().sendRedirect(request, response, redirectUrl);
}


    private UserDetails createUserDetails(OAuth2User oAuth2User) {
        // Lấy thông tin từ OAuth2User và tạo một UserDetails
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        // Cần thêm logic để lưu thông tin người dùng này vào DB nếu cần

        return org.springframework.security.core.userdetails.User.builder()
                .username(email)
                .password("") // Không cần password cho OAuth2
                .authorities("ROLE_CLIENT")
                .build();
    }
}