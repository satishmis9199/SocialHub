package com.social.SocialHub.restControlller;

import com.social.SocialHub.dto.*;
import com.social.SocialHub.entity.Comment;
import com.social.SocialHub.entity.Post;
import com.social.SocialHub.entity.UserEntity;
import com.social.SocialHub.repository.UserRepository;
import com.social.SocialHub.service.LikeService;
import com.social.SocialHub.service.PostService;
import com.social.SocialHub.service.CustomUserDetail;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/user/post")
public class PostController {
    private final SimpMessagingTemplate messagingTemplate;
    private static final Logger logger= LoggerFactory.getLogger(PostController.class);

    private final PostService postService;
    private final LikeService likeService;
    private final UserRepository userRepository;


    public PostController(SimpMessagingTemplate messagingTemplate, PostService postService, LikeService likeService, UserRepository userRepository) {
        this.messagingTemplate = messagingTemplate;
        this.postService = postService;
        this.likeService = likeService;
        this.userRepository = userRepository;
    }

    // 🔥 CREATE POST
    @PostMapping
    public ResponseEntity<?> createPost(
            @RequestParam("caption") String caption,
            @RequestParam("files") List<MultipartFile> files,
            Authentication auth) {

        // 🔒 SAFETY CHECK
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetail)) {
            logger.error("Auth is Null");
            return ResponseEntity.status(401).body("Unauthorized");
        }

        CustomUserDetail user = (CustomUserDetail) auth.getPrincipal();
        logger.error("User Detaail {} {}" ,user.getId(),user.getRole());

        postService.createPost(user, caption, files);

        return ResponseEntity.ok("Post created successfully 🚀");
    }

    @GetMapping("/feed")
    public ResponseEntity<FeedResponse> feed(
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "5") int limit,
            Authentication auth) {

        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetail)) {
            return ResponseEntity.status(401).build();
        }

        CustomUserDetail user = (CustomUserDetail) auth.getPrincipal();


        return ResponseEntity.ok(
                postService.getFeed(user.getId(), cursor, limit)
        );
    }

    @PostMapping("/toggle")
    public ResponseEntity<LikeResponse> toggleLike(
            @RequestBody LikeRequest request,
            Authentication auth) {
        logger.error("Inside LikeToggle");

        CustomUserDetail user = (CustomUserDetail) auth.getPrincipal();

        LikeResponse response =
                likeService.toggle(user.getId(), request.getPostId());

        return ResponseEntity.ok(response);
    }
    @GetMapping("/getUserProfile/{profileUserId}")
    public ResponseEntity<?> getUserProfiles(@PathVariable UUID profileUserId,Authentication authentication){
       CustomUserDetail user=(CustomUserDetail) authentication.getPrincipal();
        UserProfileResponse userProfileResponse=postService.getProfile(profileUserId,user.getId());
        return ResponseEntity.ok(userProfileResponse);

    }

    @PostMapping("/comments")
    public ResponseEntity<?> addComment(@RequestBody CommentRequestDto commentRequestDto){
        logger.error("Inside addComment");
       CommentResponseDto commentResponseDto= postService.addComment(commentRequestDto);
        return ResponseEntity.ok(
                Map.of(
                        "success",true,
                        "message","Comment Added",
                        "data",commentResponseDto
                )
        );
    }
    @GetMapping("/comments/{id}")
    public ResponseEntity<List<CommentResponseDto>> getAllComment(@PathVariable UUID id){

        List<CommentResponseDto> comments = postService.getCommentsByPostId(id);

        return ResponseEntity.ok(comments);
    }


    @GetMapping("/profile/me")
    public ResponseEntity<ProfileResponseDto> getMyProfile(){
        ProfileResponseDto profileResponseDto=postService.getMyProfile();
        return ResponseEntity.ok(profileResponseDto);
    }
    @PutMapping("/profile/update")
    public ResponseEntity<?> profileUpdate(@RequestBody ProfileResponseDto profileResponseDto){
        logger.error("Inside profile update");
        String message=postService.profilesUpdate(profileResponseDto);
        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message",message
                )
        );
    }
    @PostMapping("/upload/avatar")
    public Map<String, String> uploadAvatar(@RequestParam("avatar") MultipartFile file, HttpServletRequest request) throws IOException {

        String url=postService.uploadProfileVatar(file,request);

        return Map.of("profilePic", url);
    }
    @GetMapping("/all")
    public ResponseEntity<?> getLoggedInUserPost(HttpServletRequest request){
        List<ProfilePostDTOResponse> profilePostDTOResponses=postService.getLoggedUserPost
                (request);
        return ResponseEntity.ok(
                Map.of(
                        "posts",profilePostDTOResponses
                )
        );
    }



    /// ////Testing
    @GetMapping("/getallUser")
    public List<UserEntity> getall(){
        return userRepository.findAll();
    }
    @GetMapping("/socket-test")
    public String socketTest() {

        System.out.println(
                "TEST SOCKET API HIT"
        );

        messagingTemplate.convertAndSend(
                "/topic/notifications",
                "NEW_NOTIFICATION"
        );

        System.out.println(
                "TEST SOCKET SENT"
        );

        return "DONE";
    }
}
