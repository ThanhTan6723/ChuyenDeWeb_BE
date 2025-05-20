package org.example.chuyendeweb_be.config;

import lombok.RequiredArgsConstructor;
import org.apache.catalina.connector.Connector;
import org.example.chuyendeweb_be.security.JwtAuthenticationFilter;
//import org.example.chuyendeweb_be.security.OAuth2LoginSuccessHandler;
import org.example.chuyendeweb_be.security.OAuth2LoginSuccessHandler;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
//                .requiresChannel(channel -> channel
//                        .requestMatchers(r -> r.getHeader("X-Forwarded-Proto") != null).requiresSecure()
//                )
//                .csrf(csrf -> csrf.disable())
//                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//                .authorizeHttpRequests(authz -> authz
//                        .requestMatchers("/api/auth/**", "/oauth2/**", "/login/**").permitAll()
//                        .anyRequest().authenticated())
//               .oauth2Login(oauth2 -> oauth2.successHandler(oAuth2LoginSuccessHandler))
                .requiresChannel(channel -> channel
                        .requestMatchers(r -> r.getHeader("X-Forwarded-Proto") != null).requiresSecure()
                )
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/auth/login", "/api/auth/register", "/api/auth/refresh-token", "/oauth2/**", "/api/auth/logout","/login/**",("/api/images/**")).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2.successHandler(oAuth2LoginSuccessHandler))
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000", "https://localhost:3000", "https://localhost:8443"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        tomcat.addAdditionalTomcatConnectors(httpToHttpsRedirectConnector());
        return tomcat;
    }

    private Connector httpToHttpsRedirectConnector() {
        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        connector.setScheme("http");
        connector.setPort(8080);
        connector.setSecure(false);
        connector.setRedirectPort(8443);
        return connector;
    }
}