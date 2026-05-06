package com.social.SocialHub.repository;

import com.social.SocialHub.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostLikeRepository extends JpaRepository<PostLike, UUID> {
    List<PostLike> findByUserIdAndPostIdIn(UUID userId, List<UUID> postIds);

    Optional<PostLike> findByUserIdAndPostId(UUID userId, UUID postId);

    int countByPostId(UUID id);

    Long countByUserId(UUID id);
}
