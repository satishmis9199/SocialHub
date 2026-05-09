package com.social.SocialHub.service;

import com.social.SocialHub.dto.EntityType;
import com.social.SocialHub.dto.LikeResponse;
import com.social.SocialHub.dto.PostLikedEvent;
import com.social.SocialHub.entity.NotificationType;
import com.social.SocialHub.entity.Post;
import com.social.SocialHub.entity.PostLike;
import com.social.SocialHub.entity.UserEntity;
import com.social.SocialHub.repository.NotificationRepository;
import com.social.SocialHub.repository.PostLikeRepository;
import com.social.SocialHub.repository.PostRepository;
import com.social.SocialHub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeService {

    private final PostLikeRepository likeRepo;
    private final PostRepository postRepo;
    private final UserRepository userRepo;
    private final ApplicationEventPublisher eventPublisher;
    private final NotificationRepository notificationRepository;

    @Transactional
    public LikeResponse toggle(
            UUID userId,
            UUID postId
    ) {

        log.error(
                "LIKE TOGGLE STARTED"
        );

        Optional<PostLike> existing =
                likeRepo.findByUserIdAndPostId(
                        userId,
                        postId
                );

        Post post =
                postRepo.findById(postId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Post not found"
                                )
                        );

        if (existing.isPresent()) {

            log.error(
                    "UNLIKE FLOW STARTED"
            );

            likeRepo.delete(existing.get());

            post.setLikeCount(
                    post.getLikeCount() - 1
            );

            int deleted =
                    notificationRepository
                            .deleteBySenderIdAndReceiverIdAndEntityIdAndEntityTypeAndType(

                                    userId,

                                    post.getUser().getId(),

                                    postId,

                                    EntityType.POST,

                                    NotificationType.LIKE
                            );

            log.error(
                    "TOTAL NOTIFICATION DELETED = {}",
                    deleted
            );

            return new LikeResponse(
                    false,
                    post.getLikeCount()
            );
        }

        log.error(
                "LIKE FLOW STARTED"
        );

        UserEntity user =
                userRepo.getReferenceById(
                        userId
                );

        PostLike like = new PostLike();

        like.setUser(user);

        like.setPost(post);

        likeRepo.save(like);

        log.error(
                "LIKE SAVED"
        );

        post.setLikeCount(
                post.getLikeCount() + 1
        );

        eventPublisher.publishEvent(

                new PostLikedEvent(

                        userId,

                        post.getUser().getId(),

                        postId
                )
        );

        log.error(
                "POST LIKE EVENT PUBLISHED"
        );

        return new LikeResponse(

                true,

                post.getLikeCount()
        );
    }
}