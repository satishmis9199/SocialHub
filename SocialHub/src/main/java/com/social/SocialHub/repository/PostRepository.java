package com.social.SocialHub.repository;

import com.social.SocialHub.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {
    List<Post> findByCreatedAtBeforeOrderByCreatedAtDesc(LocalDateTime cursorTime, Pageable pageable);
    List<Post> findByUserIdOrderByCreatedAtDesc(UUID userId);

    long countByUserId(UUID userId);

    List<Post> findByUserId(UUID id);
}