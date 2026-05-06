package com.social.SocialHub.service;

import com.social.SocialHub.dto.FollowRequestDto;
import com.social.SocialHub.dto.FollowResponseDto;
import com.social.SocialHub.dto.FollowersFinalDto;
import com.social.SocialHub.dto.FollowersResponseDto;
import com.social.SocialHub.entity.Follow;
import com.social.SocialHub.entity.UserEntity;
import com.social.SocialHub.repository.FollowRepository;
import com.social.SocialHub.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Service
public class FollowService {
    private static final Logger log= LoggerFactory.getLogger(FollowService.class);
    @Autowired
    UserRepository userRepository;
    @Autowired
    FollowRepository followRepository;
    @Autowired PostService postService;
    @Transactional
    public FollowResponseDto followUsers(FollowRequestDto dto, UserEntity currentUser) {

        UUID targetId = dto.getTargetUserId();

        if (currentUser.getId().equals(targetId)) {

            throw new RuntimeException("You cannot follow Yourself");
        }

        UserEntity targetUser = userRepository.findById(targetId)
                .orElseThrow(() -> new RuntimeException("Target User not Found"));

        Optional<Follow> existing =
                followRepository.findByFollowerIdAndFollowingId(
                        currentUser.getId(),
                        targetId
                );

        if (existing.isPresent()) {
            followRepository.delete(existing.get());

            int count = currentUser.getFollowCount();
            currentUser.setFollowCount(Math.max(0, count - 1));

            return new FollowResponseDto(false, false, "UNFOLLOWED","UNFOLLOWED");
        }

        Follow follow = new Follow();
        follow.setFollower(currentUser);
        follow.setFollowing(targetUser);

        if (targetUser.isPrivate()) {
            log.error("Target User Is Private");

            follow.setStatus("REQUESTED");
            followRepository.save(follow);
            return new FollowResponseDto(false, true, "REQUESTED","REQUESTED");
        }

        follow.setStatus("ACCEPTED");

        int count = currentUser.getFollowCount();
        currentUser.setFollowCount(count + 1);

        followRepository.save(follow);

        return new FollowResponseDto(true, false, "FOLLOWING","FOLLOWING");
    }
    public FollowersFinalDto getAllFollowers(HttpServletRequest request) {

        log.info("Fetching followers list");

        String baseUrl = postService.getBaseUrl(request);
        UserEntity logged = postService.getLoggedInUser();

        log.info("Logged in user: id={}, username={}",
                logged.getId(),
                logged.getUsername());

        // ✅ only accepted followers of logged user
        List<Follow> follows = followRepository
                .findByFollowingIdAndStatus(
                        logged.getId(),
                        "ACCEPTED"
                );

        log.info("Total followers found: {}", follows.size());

        List<FollowersResponseDto> users = new ArrayList<>();

        for (Follow f : follows) {

            try {

                UserEntity user = f.getFollower();

                log.debug("Processing follower: id={}, username={}",
                        user.getId(),
                        user.getUsername());

                // ✅ relation from logged user -> this user
                Optional<Follow> relation =
                        followRepository.findByFollowerIdAndFollowingId(
                                logged.getId(),
                                user.getId()
                        );

                // ✅ actual follow status
                String followStatus = relation
                        .map(Follow::getStatus)
                        .orElse("NONE");

                // ✅ only ACCEPTED means following
                boolean isFollowing =
                        "ACCEPTED".equalsIgnoreCase(followStatus);

                // ✅ profile pic resolve
                String profilePic = user.getProfilePic();

                if (profilePic != null &&
                        !profilePic.startsWith("http")) {

                    profilePic = baseUrl + profilePic;
                }

                log.debug("Resolved followStatus={} for user={}",
                        followStatus,
                        user.getUsername());

                users.add(
                        new FollowersResponseDto(
                                user.getId(),
                                user.getUsername(),
                                user.getUsername(),
                                isFollowing,
                                followStatus
                        )
                );

            } catch (Exception e) {

                log.error("Error processing follower: {}",
                        e.getMessage(),
                        e);
            }
        }

        log.info("Followers response built successfully. Count={}",
                users.size());

        return new FollowersFinalDto(
                users.size(),
                users
        );
    }
}


