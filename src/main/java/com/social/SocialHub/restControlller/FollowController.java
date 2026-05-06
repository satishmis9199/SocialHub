package com.social.SocialHub.restControlller;

import com.social.SocialHub.dto.FollowRequestDto;
import com.social.SocialHub.dto.FollowResponseDto;
import com.social.SocialHub.dto.FollowersFinalDto;
import com.social.SocialHub.dto.FollowersResponseDto;
import com.social.SocialHub.entity.UserEntity;
import com.social.SocialHub.repository.UserRepository;
import com.social.SocialHub.service.CustomUserDetail;
import com.social.SocialHub.service.FollowService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class FollowController {
    private static  final Logger logger= LoggerFactory.getLogger(FollowController.class);
    @Autowired
    FollowService followService;
    @Autowired
    UserRepository userRepository;
    @PostMapping(value = "/follow/toggle", produces = "application/json")

    public ResponseEntity<?> followUser(@RequestBody FollowRequestDto dto, Authentication authentication) {

        try {

            CustomUserDetail user=(CustomUserDetail) authentication.getPrincipal();
            logger.error("user details{}",user.getId());
            logger.error("user details {}",user.getUser());

            logger.error("user details {}",user.getUsername());

            logger.error("Response from Follow {}",dto.getTargetUserId());

            FollowResponseDto response =
                    followService.followUsers(dto, user.getUser());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("error in follow {} ",e.getMessage());

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "success", false,
                            "message", e.getMessage()
                    )
            );
        }
    }
    @GetMapping(value = "/followers", produces = "application/json")
    public ResponseEntity<FollowersFinalDto> getAllFollowers(HttpServletRequest request) {
        logger.info("Inside FollowController");

        FollowersFinalDto response = followService.getAllFollowers(request);

        return ResponseEntity.ok(response);
    }
}
