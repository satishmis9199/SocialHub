package com.social.SocialHub.security;

import com.social.SocialHub.entity.AuthProvider;
import com.social.SocialHub.entity.Roles;
import com.social.SocialHub.entity.UserEntity;
import com.social.SocialHub.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private static final Logger logger= LoggerFactory.getLogger(CustomOAuth2SuccessHandler.class);

    public CustomOAuth2SuccessHandler(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException {

        try {
            logger.error("Inside CustomOAuth2SuccessHandler");

            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

            String email = oAuth2User.getAttribute("email");
            String name  = oAuth2User.getAttribute("name");
            logger.error("email From  :: "+oAuth2User.getAttribute("email"));

            logger.error("Inside CustomOAuth2SuccessHandler");


            // 🔥 find or create user (FIXED)
            UserEntity user = userRepository.findByEmail(email);

            if (user == null) {
                user = new UserEntity();  // 🔥 IMPORTANT
                user.setEmail(email);
                user.setUsername(name);
                user.setProvider(AuthProvider.GOOGLE);
                user.setProviderId(oAuth2User.getName());
                user.setProfilePic(oAuth2User.getAttribute("picture"));
                user.setRole(Roles.USER);
                user.setVerified(true);

                user = userRepository.save(user); // 🔥 assign back
            }

            // 🔥 existing user role fix
            if (user.getRole() == null) {
                user.setRole(Roles.USER);
                user = userRepository.save(user);
            }

            // 🔐 JWT
            String token = jwtUtil.generateToken(
                    user.getId(),
                    user.getEmail(),
                    user.getRole().name()
            );

            Cookie cookie =
                    new Cookie("token", token);

            cookie.setHttpOnly(true);

            cookie.setSecure(false);

            cookie.setPath("/");

            cookie.setMaxAge(60 * 60);

            response.addCookie(cookie);


            response.sendRedirect("/user/dashboard");

        } catch (Exception e) {
            e.printStackTrace(); // 🔥 debug
            response.sendRedirect("/api/login");
        }
    }
}