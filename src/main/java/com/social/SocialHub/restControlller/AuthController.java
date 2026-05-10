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
    private static  final Logger logger= LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequestDTO dto,
            HttpServletResponse response,
            jakarta.servlet.http.HttpServletRequest request
    ) {

        try {

            // =====================================
            // AUTHENTICATE
            // =====================================
            Authentication auth =
                    authenticationManager.authenticate(

                            new UsernamePasswordAuthenticationToken(
                                    dto.getEmail(),
                                    dto.getPassword()
                            )
                    );

            CustomUserDetail user =
                    (CustomUserDetail) auth.getPrincipal();

            // =====================================
            // GENERATE JWT
            // =====================================
            String token = jwtUtil.generateToken(
                    user.getId(),
                    user.getUsername(),
                    user.getRole().name()
            );

            logger.info("ROLE = {}", user.getRole());

            // =====================================
            // COOKIE
            // =====================================
            Cookie cookie = new Cookie("token", token);

            cookie.setHttpOnly(true);

            cookie.setPath("/");

            cookie.setMaxAge(60 * 60);

            response.addCookie(cookie);

            // =====================================
            // SAVED URL
            // =====================================
            org.springframework.security.web.savedrequest
                    .SavedRequest savedRequest =

                    new org.springframework.security.web
                            .savedrequest
                            .HttpSessionRequestCache()

                            .getRequest(request, response);

            String redirectUrl = "/user/dashboard";

            if (savedRequest != null) {

                redirectUrl =
                        savedRequest.getRedirectUrl();

                logger.info(
                        "REDIRECT URL = {}",
                        redirectUrl
                );
            }

            // =====================================
            // RESPONSE
            // =====================================
            return ResponseEntity.ok(

                    Map.of(

                            "success", true,

                            "message", "Login Successful",

                            "redirectUrl", redirectUrl
                    )
            );

        } catch (Exception e) {

            logger.error("LOGIN ERROR", e);

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
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        SecurityContextHolder.clearContext();

        HttpSession session = request.getSession(false);

        if(session != null){
            session.invalidate();
        }

        Cookie cookie = new Cookie("token", "");

        cookie.setHttpOnly(true);

        cookie.setSecure(false);

        cookie.setPath("/");

        cookie.setMaxAge(0);

        response.addCookie(cookie);

        response.setHeader(
                "Cache-Control",
                "no-cache, no-store, must-revalidate"
        );

        response.setHeader("Pragma", "no-cache");

        response.setHeader("Expires", "0");

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "redirectUrl", "/api/login"
                )
        );
    }
}