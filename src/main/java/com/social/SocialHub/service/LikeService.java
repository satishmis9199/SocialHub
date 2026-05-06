package com.social.SocialHub.service;

import com.social.SocialHub.dto.LikeResponse;
import com.social.SocialHub.entity.Post;
import com.social.SocialHub.entity.PostLike;
import com.social.SocialHub.entity.UserEntity;
import com.social.SocialHub.repository.PostLikeRepository;
import com.social.SocialHub.repository.PostRepository;
import com.social.SocialHub.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class LikeService {

    private final PostLikeRepository likeRepo;
    private final PostRepository postRepo;
    private final UserRepository userRepo;

    public LikeService(PostLikeRepository likeRepo,
                       PostRepository postRepo,
                       UserRepository userRepo) {
        this.likeRepo = likeRepo;
        this.postRepo = postRepo;
        this.userRepo = userRepo;
    }

    @Transactional
    public LikeResponse toggle(UUID userId, UUID postId) {

        Optional<PostLike> existing =
                likeRepo.findByUserIdAndPostId(userId, postId);

        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (existing.isPresent()) {
            // 🔥 UNLIKE
            likeRepo.delete(existing.get());

            post.setLikeCount(post.getLikeCount() - 1);

            return new LikeResponse(false, post.getLikeCount());
        } else {
            // 🔥 LIKE
            UserEntity user = userRepo.getReferenceById(userId);

            PostLike like = new PostLike();
            like.setUser(user);
            like.setPost(post);

            likeRepo.save(like);

            post.setLikeCount(post.getLikeCount() + 1);

            return new LikeResponse(true, post.getLikeCount());
        }
    }
}