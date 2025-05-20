package org.example.chuyendeweb_be.security;

import lombok.RequiredArgsConstructor;
import org.example.chuyendeweb_be.entity.User;
import org.example.chuyendeweb_be.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// security/CustomUserDetailsService.java
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
//        User user = userRepository.findByUsername(usernameOrEmail)
//                .or(() -> userRepository.findByEmail(usernameOrEmail))
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        User user = userRepository.findByUsernameOrEmailWithRole(usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword()!= null ? user.getPassword() : "")
                .authorities(new SimpleGrantedAuthority(user.getRole().getRoleName()))
                .build();
    }

}

