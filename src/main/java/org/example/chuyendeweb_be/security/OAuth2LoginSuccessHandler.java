package org.example.chuyendeweb_be.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
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

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email"); // google/facebook trả về email
        UserDetails userDetails = userDetailsService.loadUserByUsername(email); // <-- dùng service để lấy thông tin
        String token = jwtService.generateToken(userDetails); // <-- tạo token từ UserDetails

        String redirectUrl = "http://localhost:3000/oauth2/redirect?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
