package com.social.SocialHub.restControlller;

import com.social.SocialHub.dto.LoginRequestDTO;
import com.social.SocialHub.security.JwtUtil;
import com.social.SocialHub.service.CustomUserDetail;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO dto,
                                   HttpServletResponse response) {
        try {

            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            dto.getEmail(),
                            dto.getPassword()
                    )
            );

            CustomUserDetail user = (CustomUserDetail) auth.getPrincipal();

            String token = jwtUtil.generateToken(
                    user.getId(),
                    user.getUsername(),
                    user.getRole().name()
            );
            logger.error("Role is ___{} ",user.getRole());

            // 🔐 Cookie set
            Cookie cookie = new Cookie("token", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(100 * 100); // 1 hour

            response.addCookie(cookie);

            return ResponseEntity.ok(Map.of(
                    "message","Login SuccessFull"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(   // ✅ FIX
                    Map.of("message", e.getMessage())
            );
        }
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        logger.error("Insside LogOut");

        Cookie cookie = new Cookie("token", null);
        cookie.setMaxAge(0);     // 🔥 delete
        cookie.setPath("/");

        response.addCookie(cookie);

        return ResponseEntity.ok("Logged out");
    }
}