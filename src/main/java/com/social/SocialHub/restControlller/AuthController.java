package com.social.SocialHub.restControlller;

import com.social.SocialHub.dto.LoginRequestDTO;
import com.social.SocialHub.dto.ResetPasswordDTO;
import com.social.SocialHub.entity.UserEntity;
import com.social.SocialHub.repository.UserRepository;
import com.social.SocialHub.security.JwtUtil;
import com.social.SocialHub.service.CustomUserDetail;

import com.social.SocialHub.service.PostService;
import com.social.SocialHub.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    private static final Logger logger =
            LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;
    @Autowired
    PostService postService;

    private final JwtUtil jwtUtil;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil
    ) {

        this.authenticationManager =
                authenticationManager;

        this.jwtUtil = jwtUtil;
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(

            @RequestBody LoginRequestDTO dto,

            HttpServletRequest request,

            HttpServletResponse response
    ) {

        try {


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


            String token =

                    jwtUtil.generateToken(

                            user.getId(),

                            user.getUsername(),

                            user.getRole().name()
                    );


            Cookie cookie =
                    new Cookie("token", token);

            cookie.setHttpOnly(true);

            cookie.setSecure(false);

            cookie.setPath("/");

            cookie.setMaxAge(60 * 60);

            response.addCookie(cookie);


            String redirectUrl =
                    request.getParameter("continue");

            if (
                    redirectUrl == null
                            || redirectUrl.isBlank()
            ) {

                redirectUrl =
                        "/user/dashboard";
            }




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


    @PostMapping("/logout")
    public ResponseEntity<?> logout(

            HttpServletRequest request,

            HttpServletResponse response
    ) {

        UserEntity user=postService.getLoggedInUser();
        user.setFcmToken(null);
        userRepository.save(user);
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
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestBody ResetPasswordDTO dto) {

        Map<String, Object> response = new HashMap<>();

        try {

            System.out.println("API HIT");
            System.out.println(dto.getEmail());
            System.out.println(dto.getNewPassword());

            boolean updated =
                    userService.resetPassword(
                            dto.getEmail(),
                            dto.getNewPassword()
                    );

            if (!updated) {
                response.put("success", false);
                response.put("message", "Email not found");

                return ResponseEntity.badRequest().body(response);
            }

            response.put("success", true);
            response.put("message", "Password reset successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            e.printStackTrace();

            response.put("success", false);
            response.put("message", "Something went wrong");

            return ResponseEntity.internalServerError().body(response);
        }
    }

}