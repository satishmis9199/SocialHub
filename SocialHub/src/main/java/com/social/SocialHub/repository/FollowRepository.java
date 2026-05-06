package com.social.SocialHub.repository;

import com.social.SocialHub.entity.Follow;
import com.social.SocialHub.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FollowRepository  extends JpaRepository< Follow,UUID> {
    Optional<Follow> findByFollowerIdAndFollowingId(UUID id, UUID targetId);
    long countByFollowingIdAndStatus(UUID userId, String status);

    long countByFollowerIdAndStatus(UUID userId, String status);

//    List<Follow> findByFollowingIdAndStatus(UserEntity logged, String accepted);
//
//    boolean existsByFollowerIdAndFollowingId(UserEntity logged, UserEntity user);

    List<Follow> findByFollowingIdAndStatus(UUID id, String accepted);

    boolean existsByFollowerIdAndFollowingId(UUID id, UUID id1);
}
