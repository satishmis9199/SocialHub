package com.social.SocialHub.restControlller;

import com.social.SocialHub.dto.RegistserDto;
import com.social.SocialHub.entity.UserEntity;
import com.social.SocialHub.repository.UserRepository;
import com.social.SocialHub.service.RegisterService;

import io.micrometer.observation.annotation.Observed;   // ✅ IMPORTANT

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class RegisterRestController {

    private static final Logger log = LoggerFactory.getLogger(RegisterRestController.class);

    private final RegisterService registerService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterRestController(RegisterService registerService,
                                  UserRepository userRepository,
                                  PasswordEncoder passwordEncoder) {
        this.registerService = registerService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }



    @PostMapping("/register")
    public ResponseEntity<?> registerUserFromLocal(@RequestBody RegistserDto registserDto) {

        log.info("Inside registerUserFromLocal");

        String message = registerService.registerUser(registserDto);

        return ResponseEntity.ok(
                Map.of("message", message)
        );
    }



    @Observed(name = "get-all-users")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public List<UserEntity> getAll() {
        return userRepository.findAll();
    }
}