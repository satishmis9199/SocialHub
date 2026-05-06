package com.social.SocialHub.config;

import com.social.SocialHub.entity.Post;
import com.social.SocialHub.repository.CommentRepository;
import com.social.SocialHub.repository.PostLikeRepository;
import com.social.SocialHub.repository.PostRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Component
public class Scheduler {

    private static final Logger logger = LoggerFactory.getLogger(Scheduler.class);

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostLikeRepository postLikeRepository;


    @Scheduled(cron = "0 32 17 * * ?")
    public void syncPostCounts() {

        logger.info("🚀 Scheduler started: Syncing post like & comment counts");

        List<Post> postList = postRepository.findAll();

        if (postList.isEmpty()) {
            logger.warn("⚠️ No posts found in database");
            return;
        }

        for (Post post : postList) {

            try {
                int actualCommentCount = commentRepository.countByPostId(post.getId());
                int actualLikeCount = postLikeRepository.countByPostId(post.getId());

                int storedCommentCount =  post.getCommentCount();
                int storedLikeCount =  post.getLikeCount();

                if (actualCommentCount != storedCommentCount ||
                        actualLikeCount != storedLikeCount) {

                    logger.info("🔧 Fixing Post ID: {}", post.getId());
                    logger.info("Old CommentCount: {}, New: {}", storedCommentCount, actualCommentCount);
                    logger.info("Old LikeCount: {}, New: {}", storedLikeCount, actualLikeCount);

                    post.setCommentCount(actualCommentCount);
                    post.setLikeCount(actualLikeCount);

                    postRepository.save(post);

                    logger.info("✅ Post updated successfully: {}", post.getId());

                } else {
                    logger.debug("✔ Post already in sync: {}", post.getId());
                }

            } catch (Exception e) {
                logger.error("❌ Error processing post ID: {}", post.getId(), e);
            }
        }

        logger.info("🏁 Scheduler completed successfully");
    }
}