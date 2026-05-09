package com.social.SocialHub.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.AuthenticationException;
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

                        // PUBLIC APIs
                        .requestMatchers(
                                "/api/**",
                                "/login",
                                "/access-denied",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        ).permitAll()

                        // EVERYTHING ELSE
                        .anyRequest().authenticated()
                )

                // ===============================
                // EXCEPTION HANDLING
                // ===============================
                .exceptionHandling(ex -> ex

                        // UNAUTHORIZED
                        .authenticationEntryPoint((request, response, authException) -> {

                            String uri = request.getRequestURI();

                            System.out.println("❌ Unauthorized Request: " + uri);

                            // API CALL
                            if (uri.startsWith("/user/") || uri.startsWith("/api/")) {

                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

                                response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                                response.getWriter().write("""
                                    {
                                      "error":"Unauthorized",
                                      "message":"Login required"
                                    }
                                """);

                            } else {

                                // NORMAL PAGE REDIRECT
                                response.sendRedirect("/api/login");
                            }
                        })

                        // ACCESS DENIED
                        .accessDeniedHandler((request, response, accessDeniedException) -> {

                            String uri = request.getRequestURI();

                            System.out.println("⛔ Access Denied: " + uri);

                            if (uri.startsWith("/user/") || uri.startsWith("/api/")) {

                                response.setStatus(HttpServletResponse.SC_FORBIDDEN);

                                response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                                response.getWriter().write("""
                                    {
                                      "error":"Forbidden"
                                    }
                                """);

                            } else {

                                response.sendRedirect("/api/access-denied");
                            }
                        })

                )

                // GOOGLE LOGIN
                .oauth2Login(oauth -> oauth
                        .successHandler(successHandler)
                )

                // JWT FILTER
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {

        return config.getAuthenticationManager();
    }
}