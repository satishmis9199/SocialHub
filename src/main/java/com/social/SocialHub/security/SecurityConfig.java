package com.social.SocialHub.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.MediaType;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private CustomOAuth2SuccessHandler successHandler;

    // =====================================================
    // CUSTOM REQUEST CACHE
    // =====================================================
    @Bean
    public RequestCache requestCache() {

        return new HttpSessionRequestCache() {

            @Override
            public void saveRequest(
                    HttpServletRequest request,
                    HttpServletResponse response
            ) {

                String uri =
                        request.getRequestURI();

                // =====================================
                // IGNORE THESE URLS
                // =====================================
                if (

                        uri.startsWith("/ws")

                                || uri.contains("firebase")

                                || uri.contains(".js")

                                || uri.contains(".css")

                                || uri.contains(".png")

                                || uri.contains(".jpg")

                                || uri.contains(".jpeg")

                                || uri.contains(".svg")

                                || uri.contains(".ico")

                                || uri.startsWith("/api/")

                                || uri.contains("sockjs")

                                || uri.contains("favicon")

                ) {

                    return;
                }

                super.saveRequest(request, response);
            }
        };
    }

    // =====================================================
    // SECURITY FILTER CHAIN
    // =====================================================
    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http
    ) throws Exception {

        return http

                // =====================================
                // DISABLE CSRF
                // =====================================
                .csrf(csrf -> csrf.disable())

                // =====================================
                // SESSION POLICY
                // =====================================
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.IF_REQUIRED
                        )
                )

                // =====================================
                // REQUEST CACHE
                // =====================================
                .requestCache(cache ->
                        cache.requestCache(requestCache())
                )

                // =====================================
                // AUTHORIZATION
                // =====================================
                .authorizeHttpRequests(auth -> auth

                        // =================================
                        // PUBLIC URLS
                        // =================================
                        .requestMatchers(

                                "/api/login",

                                "/api/register",

                                "/oauth2/**",

                                "/login/**",

                                "/css/**",

                                "/js/**",

                                "/images/**",

                                "/uploads/**",

                                "/firebase-messaging-sw.js",

                                "/ws/**",

                                "/favicon.ico",

                                "/access-denied",

                                "/api/access-denied"

                        ).permitAll()

                        // =================================
                        // PROTECTED USER URLS
                        // =================================
                        .requestMatchers("/user/**")
                        .authenticated()

                        // =================================
                        // EVERYTHING ELSE
                        // =================================
                        .anyRequest()
                        .authenticated()
                )

                // =====================================
                // EXCEPTION HANDLING
                // =====================================
                .exceptionHandling(ex -> ex

                        // =================================
                        // UNAUTHORIZED
                        // =================================
                        .authenticationEntryPoint(

                                (request, response, authException) -> {

                                    String uri =
                                            request.getRequestURI();

                                    boolean isAjax =
                                            "XMLHttpRequest".equals(
                                                    request.getHeader(
                                                            "X-Requested-With"
                                                    )
                                            );

                                    System.out.println(
                                            "❌ Unauthorized : "
                                                    + uri
                                    );

                                    // =============================
                                    // AJAX / API
                                    // =============================
                                    if (

                                            isAjax

                                                    || uri.startsWith("/api/")

                                    ) {

                                        response.setStatus(
                                                HttpServletResponse.SC_UNAUTHORIZED
                                        );

                                        response.setContentType(
                                                MediaType.APPLICATION_JSON_VALUE
                                        );

                                        response.getWriter().write("""
                                                {
                                                    "success": false,
                                                    "message": "Please Login First",
                                                    "redirectUrl": "/api/login"
                                                }
                                                """);

                                    } else {

                                        // =============================
                                        // NORMAL PAGE REDIRECT
                                        // =============================
                                        response.sendRedirect(
                                                "/api/login?continue=" + uri
                                        );
                                    }
                                }
                        )

                        // =================================
                        // ACCESS DENIED
                        // =================================
                        .accessDeniedHandler(

                                (request, response, accessDeniedException) -> {

                                    String uri =
                                            request.getRequestURI();

                                    boolean isAjax =
                                            "XMLHttpRequest".equals(
                                                    request.getHeader(
                                                            "X-Requested-With"
                                                    )
                                            );

                                    System.out.println(
                                            "⛔ Access Denied : "
                                                    + uri
                                    );

                                    // =============================
                                    // AJAX / API
                                    // =============================
                                    if (

                                            isAjax

                                                    || uri.startsWith("/api/")

                                    ) {

                                        response.setStatus(
                                                HttpServletResponse.SC_FORBIDDEN
                                        );

                                        response.setContentType(
                                                MediaType.APPLICATION_JSON_VALUE
                                        );

                                        response.getWriter().write("""
                                                {
                                                    "success": false,
                                                    "message": "Access Denied",
                                                    "redirectUrl": "/api/access-denied"
                                                }
                                                """);

                                    } else {

                                        // =============================
                                        // NORMAL PAGE REDIRECT
                                        // =============================
                                        response.sendRedirect(
                                                "/api/access-denied"
                                        );
                                    }
                                }
                        )
                )

                // =====================================
                // GOOGLE LOGIN
                // =====================================
                .oauth2Login(oauth ->
                        oauth.successHandler(successHandler)
                )

                // =====================================
                // JWT FILTER
                // =====================================
                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .build();
    }

    // =====================================================
    // PASSWORD ENCODER
    // =====================================================
    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    // =====================================================
    // AUTH MANAGER
    // =====================================================
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {

        return config.getAuthenticationManager();
    }
}
