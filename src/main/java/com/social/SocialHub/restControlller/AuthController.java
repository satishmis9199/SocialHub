package com.social.SocialHub.restControlller;

import com.social.SocialHub.dto.LoginRequestDTO;
import com.social.SocialHub.security.JwtUtil;
import com.social.SocialHub.service.CustomUserDetail;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    private static final Logger logger =
            LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil
    ) {

        this.authenticationManager =
                authenticationManager;

        this.jwtUtil = jwtUtil;
    }

    // =====================================================
    // LOGIN
    // =====================================================
    @PostMapping("/login")
    public ResponseEntity<?> login(

            @RequestBody LoginRequestDTO dto,

            HttpServletRequest request,

            HttpServletResponse response
    ) {

        try {

            // =============================================
            // AUTHENTICATE USER
            // =============================================
            Authentication auth =

                    authenticationManager.authenticate(

                            new UsernamePasswordAuthenticationToken(

                                    dto.getEmail(),

                                    dto.getPassword()
                            )
                    );

            // =============================================
            // USER DETAILS
            // =============================================
            CustomUserDetail user =

                    (CustomUserDetail) auth.getPrincipal();

            logger.info(
                    "✅ LOGIN SUCCESS : {}",
                    user.getUsername()
            );

            // =============================================
            // GENERATE JWT
            // =============================================
            String token =

                    jwtUtil.generateToken(

                            user.getId(),

                            user.getUsername(),

                            user.getRole().name()
                    );

            // =============================================
            // JWT COOKIE
            // =============================================
            Cookie cookie =
                    new Cookie("token", token);

            cookie.setHttpOnly(true);

            cookie.setSecure(false);

            cookie.setPath("/");

            cookie.setMaxAge(60 * 60);

            response.addCookie(cookie);

            // =============================================
            // REDIRECT URL
            // =============================================
            String redirectUrl =
                    request.getParameter("continue");

            if (
                    redirectUrl == null
                            || redirectUrl.isBlank()
            ) {

                redirectUrl =
                        "/user/dashboard";
            }

            logger.info(
                    "➡️ REDIRECT URL : {}",
                    redirectUrl
            );

            // =============================================
            // RESPONSE
            // =============================================
            return ResponseEntity.ok(

                    Map.of(

                            "success", true,

                            "message",
                            "Login Successful",

                            "redirectUrl",
                            redirectUrl
                    )
            );

        } catch (Exception e) {

            logger.error(
                    "❌ LOGIN ERROR",
                    e
            );

            return ResponseEntity
                    .status(401)
                    .body(

                            Map.of(

                                    "success", false,

                                    "message",
                                    "Invalid Email or Password"
                            )
                    );
        }
    }

    // =====================================================
    // LOGOUT
    // =====================================================
    @PostMapping("/logout")
    public ResponseEntity<?> logout(

            HttpServletRequest request,

            HttpServletResponse response
    ) {

        logger.info("🚪 LOGOUT");

        // =============================================
        // CLEAR SECURITY CONTEXT
        // =============================================
        SecurityContextHolder.clearContext();

        // =============================================
        // INVALIDATE SESSION
        // =============================================
        HttpSession session =
                request.getSession(false);

        if (session != null) {

            session.invalidate();
        }

        // =============================================
        // DELETE JWT COOKIE
        // =============================================
        Cookie jwtCookie =
                new Cookie("token", "");

        jwtCookie.setHttpOnly(true);

        jwtCookie.setSecure(false);

        jwtCookie.setPath("/");

        jwtCookie.setMaxAge(0);

        response.addCookie(jwtCookie);

        // =============================================
        // DELETE JSESSIONID
        // =============================================
        Cookie sessionCookie =
                new Cookie("JSESSIONID", "");

        sessionCookie.setHttpOnly(true);

        sessionCookie.setSecure(false);

        sessionCookie.setPath("/");

        sessionCookie.setMaxAge(0);

        response.addCookie(sessionCookie);

        // =============================================
        // CACHE CLEAR
        // =============================================
        response.setHeader(
                "Cache-Control",
                "no-cache, no-store, must-revalidate"
        );

        response.setHeader(
                "Pragma",
                "no-cache"
        );

        response.setHeader(
                "Expires",
                "0"
        );

        // =============================================
        // RESPONSE
        // =============================================
        return ResponseEntity.ok(

                Map.of(

                        "success", true,

                        "message",
                        "Logout Successful",

                        "redirectUrl",
                        "/api/login"
                )
        );
    }
}