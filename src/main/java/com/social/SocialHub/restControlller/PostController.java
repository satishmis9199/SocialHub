package com.social.SocialHub.restControlller;

import com.social.SocialHub.dto.*;
import com.social.SocialHub.entity.UserEntity;
import com.social.SocialHub.repository.UserRepository;
import com.social.SocialHub.service.CustomUserDetail;
import com.social.SocialHub.service.LikeService;
import com.social.SocialHub.service.PostService;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/user/post")

@CrossOrigin(
        origins = "https://socialhub-jjy2.onrender.com",
        allowCredentials = "true"
)

public class PostController {

    private static final Logger logger =
            LoggerFactory.getLogger(PostController.class);

    private final PostService postService;
    private final LikeService likeService;
    private final UserRepository userRepository;

    public PostController(
            PostService postService,
            LikeService likeService,
            UserRepository userRepository
    ) {
        this.postService = postService;
        this.likeService = likeService;
        this.userRepository = userRepository;
    }

    // CREATE POST
    @PostMapping
    public ResponseEntity<?> createPost(
            @RequestParam("caption") String caption,
            @RequestParam("files") List<MultipartFile> files,
            Authentication auth
    ) {

        logger.error("CREATE POST API HIT");

        try {

            logger.error("AUTH OBJECT : {}", auth);

            if (auth == null ||
                    !(auth.getPrincipal() instanceof CustomUserDetail)) {

                logger.error("AUTH FAILED");

                return ResponseEntity
                        .status(401)
                        .body("Unauthorized");
            }

            CustomUserDetail user =
                    (CustomUserDetail) auth.getPrincipal();

            logger.error(
                    "USER : {} ROLE : {}",
                    user.getId(),
                    user.getRole()
            );

            postService.createPost(user, caption, files);

            logger.error("POST CREATED SUCCESS");

            return ResponseEntity.ok(
                    "Post created successfully 🚀"
            );

        } catch (Exception e) {

            logger.error("CREATE POST ERROR", e);

            return ResponseEntity
                    .internalServerError()
                    .body(e.getMessage());
        }
    }

    // FEED
    @GetMapping("/feed")
    public ResponseEntity<?> feed(
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "5") int limit,
            Authentication auth
    ) {

        logger.error("FEED API HIT");

        try {

            logger.error("AUTH : {}", auth);

            if (auth == null ||
                    !(auth.getPrincipal() instanceof CustomUserDetail)) {

                logger.error("FEED AUTH FAILED");

                return ResponseEntity.status(401).build();
            }

            CustomUserDetail user =
                    (CustomUserDetail) auth.getPrincipal();

            logger.error("FEED USER : {}", user.getUsername());

            FeedResponse response =
                    postService.getFeed(
                            user.getId(),
                            cursor,
                            limit
                    );

            logger.error("FEED SUCCESS");

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            logger.error("FEED ERROR", e);

            return ResponseEntity
                    .internalServerError()
                    .body(e.getMessage());
        }
    }

    // ADD COMMENT
    @PostMapping("/comments")
    public ResponseEntity<?> addComment(
            @RequestBody CommentRequestDto commentRequestDto,
            Authentication auth
    ) {

        logger.error("ADD COMMENT API HIT");

        try {

            logger.error("AUTH : {}", auth);

            logger.error(
                    "POST ID : {}",
                    commentRequestDto.getPostId()
            );

            logger.error(
                    "TEXT : {}",
                    commentRequestDto.getText()
            );

            CommentResponseDto commentResponseDto =
                    postService.addComment(commentRequestDto);

            logger.error("COMMENT ADDED SUCCESS");

            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message", "Comment Added",
                            "data", commentResponseDto
                    )
            );

        } catch (Exception e) {

            logger.error("ADD COMMENT ERROR", e);

            return ResponseEntity
                    .internalServerError()
                    .body(
                            Map.of(
                                    "success", false,
                                    "message", e.getMessage()
                            )
                    );
        }
    }

    // GET COMMENTS
    @GetMapping("/comments/{id}")
    public ResponseEntity<?> getAllComment(
            @PathVariable UUID id,
            Authentication auth
    ) {

        logger.error("GET COMMENTS API HIT");

        try {

            logger.error("COMMENT ID : {}", id);

            logger.error("AUTH : {}", auth);

            if (auth == null) {

                logger.error("AUTH NULL");

            } else {

                logger.error(
                        "AUTH PRINCIPAL : {}",
                        auth.getPrincipal()
                );

                logger.error(
                        "AUTH NAME : {}",
                        auth.getName()
                );

                logger.error(
                        "IS AUTHENTICATED : {}",
                        auth.isAuthenticated()
                );
            }

            List<CommentResponseDto> comments =
                    postService.getCommentsByPostId(id);

            logger.error(
                    "TOTAL COMMENTS : {}",
                    comments.size()
            );

            return ResponseEntity.ok(comments);

        } catch (Exception e) {

            logger.error("GET COMMENTS ERROR", e);

            return ResponseEntity
                    .internalServerError()
                    .body(
                            Map.of(
                                    "success", false,
                                    "message", e.getMessage()
                            )
                    );
        }
    }
}