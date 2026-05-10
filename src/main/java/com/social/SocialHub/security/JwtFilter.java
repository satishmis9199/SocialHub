package com.social.SocialHub.security;

import com.social.SocialHub.entity.UserEntity;
import com.social.SocialHub.repository.UserRepository;
import com.social.SocialHub.service.CustomUserDetail;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger =
            LoggerFactory.getLogger(JwtFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    private final boolean LOAD_FULL_USER = true;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String uri = request.getRequestURI();

        // =========================================
        // SKIP WEBSOCKET
        // =========================================
        if (uri.startsWith("/ws")) {

            filterChain.doFilter(request, response);

            return;
        }

        String token = extractToken(request);

        // =========================================
        // NO TOKEN
        // =========================================
        if (token == null || token.isBlank()) {

            logger.warn("⚠️ No JWT token found");

            SecurityContextHolder.clearContext();

            filterChain.doFilter(request, response);

            return;
        }

        // =========================================
        // ALREADY AUTHENTICATED
        // =========================================
        if (SecurityContextHolder
                .getContext()
                .getAuthentication() == null) {

            try {

                // =====================================
                // VALIDATE TOKEN
                // =====================================
                if (jwtUtil.validateToken(token)) {

                    UUID userId =
                            jwtUtil.extractId(token);

                    String username =
                            jwtUtil.extractUsername(token);

                    String role =
                            jwtUtil.extractRole(token);

                    logger.info(
                            "✅ JWT VALID : {}",
                            username
                    );

                    CustomUserDetail userDetails;

                    // =====================================
                    // LOAD FULL USER
                    // =====================================
                    if (LOAD_FULL_USER) {

                        UserEntity user =
                                userRepository
                                        .findById(userId)
                                        .orElse(null);

                        if (user == null) {

                            logger.warn(
                                    "❌ User not found"
                            );

                            SecurityContextHolder
                                    .clearContext();

                            filterChain.doFilter(
                                    request,
                                    response
                            );

                            return;
                        }

                        userDetails =
                                new CustomUserDetail(user);

                    } else {

                        userDetails =
                                new CustomUserDetail(
                                        userId,
                                        username,
                                        role
                                );
                    }

                    // =====================================
                    // AUTHENTICATION
                    // =====================================
                    UsernamePasswordAuthenticationToken
                            authToken =

                            new UsernamePasswordAuthenticationToken(

                                    userDetails,

                                    null,

                                    userDetails.getAuthorities()
                            );

                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authToken);

                    logger.info(
                            "✅ AUTHENTICATION SET : {}",
                            username
                    );

                } else {

                    logger.warn(
                            "❌ Invalid JWT Token"
                    );

                    SecurityContextHolder.clearContext();
                }

            } catch (Exception e) {

                logger.error(
                        "🔥 JWT PROCESSING ERROR : {}",
                        e.getMessage(),
                        e
                );

                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    // =========================================
    // EXTRACT TOKEN
    // =========================================
    private String extractToken(
            HttpServletRequest request
    ) {

        // =====================================
        // COOKIE
        // =====================================
        if (request.getCookies() != null) {

            for (Cookie cookie : request.getCookies()) {

                if ("token".equals(cookie.getName())) {

                    logger.info("🍪 TOKEN FOUND IN COOKIE");

                    return cookie.getValue();
                }
            }
        }

        // =====================================
        // AUTH HEADER
        // =====================================
        String authHeader =
                request.getHeader("Authorization");

        if (authHeader != null
                && authHeader.startsWith("Bearer ")) {

            logger.info(
                    "📩 TOKEN FOUND IN HEADER"
            );

            return authHeader.substring(7);
        }

        return null;
    }
}