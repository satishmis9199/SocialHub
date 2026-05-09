package com.social.SocialHub.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;
    @Autowired
    private CustomOAuth2SuccessHandler successHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/**", "/login","/user/post/comments/**", "/access-denied").permitAll()
                        .anyRequest().authenticated()
                )

                // 🔥 EXCEPTION HANDLING (MAIN PART)
                .exceptionHandling(ex -> ex

                        // ❌ Not authenticated → login page
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendRedirect("/api/login");
                        })

                        // ❌ Access denied → custom page
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.sendRedirect("/api/access-denied");
                        })

                )
                .oauth2Login(oauth -> oauth
                        .successHandler(successHandler)   // 🔥 बस ये
                )

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}