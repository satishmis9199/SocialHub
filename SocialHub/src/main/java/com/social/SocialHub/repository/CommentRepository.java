package com.social.SocialHub.repository;

import com.social.SocialHub.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    Optional<Object> findByUserId(UUID id);


    List<Comment> findByPostId(UUID id);

    int countByPostId(UUID id);

    Long countByUserId(UUID id);
}
