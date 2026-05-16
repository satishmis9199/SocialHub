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

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String uri = request.getRequestURI();

        // Skip websocket
        if (uri.startsWith("/ws")) {

            filterChain.doFilter(request, response);

            return;
        }

        try {

            String token = extractToken(request);

            // No token
            if (token == null || token.isBlank()) {

                logger.warn("⚠️ No JWT token found");

                SecurityContextHolder.clearContext();

                filterChain.doFilter(request, response);

                return;
            }

            // Validate token
            if (!jwtUtil.validateToken(token)) {

                logger.warn("❌ Invalid JWT Token");

                SecurityContextHolder.clearContext();

                filterChain.doFilter(request, response);

                return;
            }

            // Extract data
            UUID userId =
                    jwtUtil.extractId(token);

            String username =
                    jwtUtil.extractUsername(token);

            logger.info(
                    "✅ JWT VALID : {}",
                    username
            );

            // Load user
            UserEntity user =
                    userRepository
                            .findById(userId)
                            .orElse(null);

            if (user == null) {

                logger.warn("❌ User not found");

                SecurityContextHolder.clearContext();

                filterChain.doFilter(request, response);

                return;
            }

            // Create principal
            CustomUserDetail userDetails =
                    new CustomUserDetail(user);

            // Create auth token
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            // Set auth
            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authToken);

            logger.info(
                    "✅ AUTHENTICATION SET : {}",
                    username
            );

        } catch (Exception e) {

            logger.error(
                    "🔥 JWT PROCESSING ERROR : {}",
                    e.getMessage(),
                    e
            );

            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    // =========================================
    // EXTRACT TOKEN
    // =========================================
    private String extractToken(
            HttpServletRequest request
    ) {

        // COOKIE
        if (request.getCookies() != null) {

            for (Cookie cookie : request.getCookies()) {

                if ("token".equals(cookie.getName())) {

                    logger.info(
                            "🍪 TOKEN FOUND IN COOKIE"
                    );

                    return cookie.getValue();
                }
            }
        }

        // AUTH HEADER
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