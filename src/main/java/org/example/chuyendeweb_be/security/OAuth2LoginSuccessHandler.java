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


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();


        String email = oAuth2User.getAttribute("email"); // google/facebook trả về email
        // Kiểm tra user có tồn tại chưa
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            // Nếu chưa thì tạo mới user
            User newUser = new User();
            newUser.setUsername(email); // hoặc tạo username riêng nếu muốn
            newUser.setEmail(email);
            newUser.setPassword(""); // OAuth không cần password
            newUser.setLocked(false);
            newUser.setFailed(0);

            Role defaultRole = roleRepository.findByRoleName("ROLE_CLIENT")
                    .orElseThrow(() -> new RuntimeException("Default role not found"));
            newUser.setRole(defaultRole);

            return userRepository.save(newUser);
        });
        UserDetails userDetails = userDetailsService.loadUserByUsername(email); // <-- dùng service để lấy thông tin
        String token = jwtService.generateToken(userDetails); // <-- tạo token từ UserDetails

//        String redirectUrl = "http://localhost:3000/oauth2/redirect?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
//        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
        String redirectUrl = "http://localhost:3000/home?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);

        // Tạo JWT cho người dùng OAuth2
        String accessToken = jwtService.generateToken(createUserDetails(oAuth2User));
        String refreshToken = jwtService.generateRefreshToken(createUserDetails(oAuth2User));

        // Tạo cookie access token
        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(15 * 60)
                .build();

        // Tạo cookie refresh token
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/api/auth/refresh-token")
                .maxAge(24 * 60 * 60)
                .build();

        // Thêm cookie vào response
        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        // Chuyển hướng về trang chủ của frontend
        getRedirectStrategy().sendRedirect(request, response, frontendUrl);
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