package com.social.SocialHub.repository;

import com.social.SocialHub.entity.PostMedia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PostMediaRepository extends JpaRepository<PostMedia, UUID> {
    List<PostMedia> findByPostIdIn(List<UUID> postIds);
}