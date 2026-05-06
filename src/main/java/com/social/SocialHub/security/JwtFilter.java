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

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    private final boolean LOAD_FULL_USER = true; // 🔥 switch

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        logger.info("🔐 JwtFilter invoked for URI: {}", request.getRequestURI());

        String token = extractToken(request);

        if (token == null) {
            logger.warn("⚠️ No JWT token found in request");
        }

        if (token != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

            try {
                logger.debug("🔍 Validating token...");

                if (jwtUtil.validateToken(token)) {

                    UUID userId = jwtUtil.extractId(token);
                    String username = jwtUtil.extractUsername(token);
                    String role = jwtUtil.extractRole(token);

                    logger.info("✅ Token valid for user: {}", username);

                    CustomUserDetail userDetails;

                    if (LOAD_FULL_USER) {
                        logger.debug("📦 Fetching user from DB: {}", userId);

                        UserEntity user = userRepository.findById(userId).orElse(null);

                        if (user == null) {
                            logger.error("❌ User not found in DB for ID: {}", userId);
                            filterChain.doFilter(request, response);
                            return;
                        }

                        userDetails = new CustomUserDetail(user);

                    } else {
                        logger.debug("⚡ Using JWT-only user (no DB hit)");
                        userDetails = new CustomUserDetail(userId, username, role);
                    }

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    logger.info("🎯 Authentication set for user: {}", username);

                } else {
                    logger.warn("❌ Invalid JWT token");
                }

            } catch (Exception e) {
                logger.error("🔥 JWT processing error: {}", e.getMessage(), e);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {

        // 1️⃣ Cookie (Primary)
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("token".equals(cookie.getName())) {
                    logger.debug("🍪 Token found in cookie");
                    return cookie.getValue();
                }
            }
        }

        // 2️⃣ Header (Fallback)
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            logger.debug("📩 Token found in Authorization header");
            return authHeader.substring(7);
        }

        return null;
    }
}