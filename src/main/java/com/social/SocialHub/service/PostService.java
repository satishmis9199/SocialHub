package com.social.SocialHub.service;

import com.social.SocialHub.controller.OnlineUsers;
import com.social.SocialHub.dto.*;
import com.social.SocialHub.entity.*;
import com.social.SocialHub.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PostService {

    public static final Logger logger = LoggerFactory.getLogger(PostService.class);

    private final FollowRepository followRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final PostMediaRepository postMediaRepository;
    private final MediaService mediaService;
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;
    private final ChatMessageRepository chatMessageRepository;

    public PostService(FollowRepository followRepository, CommentRepository commentRepository,
                       PostRepository postRepository,
                       PostMediaRepository postMediaRepository,
                       MediaService mediaService,
                       UserRepository userRepository,
                       PostLikeRepository postLikeRepository, ChatMessageRepository chatMessageRepository) {
        this.followRepository = followRepository;
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.postMediaRepository = postMediaRepository;
        this.mediaService = mediaService;
        this.userRepository = userRepository;
        this.postLikeRepository = postLikeRepository;
        this.chatMessageRepository = chatMessageRepository;
    }

    public String getBaseUrl(HttpServletRequest request) {
        String url = request.getScheme() + "://" + request.getServerName();
        if (request.getServerPort() != 80 && request.getServerPort() != 443) {
            url += ":" + request.getServerPort();
        }
        logger.error("Url becomes :  {}",url);
        return url;

    }


    @Transactional
    public void createPost(CustomUserDetail user,
                           String caption,
                           List<MultipartFile> files) {

        UserEntity userEntity = userRepository.findByUsername(user.getUsername());
        if (userEntity == null) throw new RuntimeException("User Not Found");

        Post post = new Post();
        post.setUser(userEntity);
        post.setCaption(caption);
        post = postRepository.save(post);

        List<MediaUploadResponse> uploaded = mediaService.upload(files);
        List<PostMedia> mediaList = new ArrayList<>();
        int index = 0;

        for (MediaUploadResponse m : uploaded) {
            logger.info("File Type {}", m.getType());
            PostMedia media = new PostMedia();
            media.setPost(post);
            media.setFileName(m.getFileName());
            media.setUrl(m.getUrl());
            media.setType(com.social.SocialHub.entity.MediaType.valueOf(m.getType()));
            media.setOrderIndex(index++);
            mediaList.add(media);
        }

        postMediaRepository.saveAll(mediaList);
    }


    @Transactional
    public FeedResponse getFeed(UUID userId, String cursor, int limit) {

        List<Post> posts;
        PageRequest pageable = PageRequest.of(0, limit);

        if (cursor == null) {
            posts = postRepository.findAll(
                    PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"))
            ).getContent();
        } else {
            LocalDateTime cursorTime = LocalDateTime.parse(cursor);
            posts = postRepository.findByCreatedAtBeforeOrderByCreatedAtDesc(cursorTime, pageable);
        }

        if (posts.isEmpty()) {
            return new FeedResponse(new ArrayList<>(), null);
        }

        List<UUID> postIds = posts.stream().map(Post::getId).toList();

        // Media bulk fetch
        List<PostMedia> allMedia = postMediaRepository.findByPostIdIn(postIds);
        Map<UUID, List<MediaDTO>> mediaMap = new HashMap<>();
        for (PostMedia m : allMedia) {
            MediaDTO dto = new MediaDTO(m.getUrl(), m.getType().name());
            mediaMap.computeIfAbsent(m.getPost().getId(), k -> new ArrayList<>()).add(dto);
        }

        // Liked posts
        List<PostLike> likes = postLikeRepository.findByUserIdAndPostIdIn(userId, postIds);
        Set<UUID> likedPostIds = new HashSet<>();
        for (PostLike l : likes) likedPostIds.add(l.getPost().getId());

        // Build response
        List<PostResponse> response = new ArrayList<>();
        for (Post p : posts) {
            List<MediaDTO> media = mediaMap.getOrDefault(p.getId(), new ArrayList<>());
            boolean liked = likedPostIds.contains(p.getId());

            response.add(new PostResponse(
                    p.getId(),
                    p.getUser().getUsername(),
                    p.getUser().getId(),
                    p.getUser().getProfilePic(),
                    p.getCaption(),
                    p.getLikeCount(),
                    liked,
                    media,
                    p.getCommentCount()
            ));
        }

        String nextCursor = posts.get(posts.size() - 1).getCreatedAt().toString();
        return new FeedResponse(response, nextCursor);
    }

    /* ═══════════════════════════════════════
       GET PROFILE
       FIX 1: @Transactional added
       FIX 2: p.getUser() hata ke profileUser use kiya
       FIX 3: "NOT_FOLLOWING" → "NONE" (frontend match)
       FIX 4: catch mein error log kiya
    ═══════════════════════════════════════ */
    @Transactional
    public UserProfileResponse getProfile(UUID profileUserId, UUID currentUserId) {
        try {
            // 1. User fetch
            UserEntity profileUser = userRepository.findById(profileUserId)
                    .orElseThrow(() -> new RuntimeException("User not found: " + profileUserId));

            logger.info("Profile fetch: {} requested by {}", profileUserId, currentUserId);

            // 2. Follow status
            Optional<Follow> relation = followRepository
                    .findByFollowerIdAndFollowingId(currentUserId, profileUserId);

            String followStatus = relation
                    .map(f -> "ACCEPTED".equals(f.getStatus()) ? "FOLLOWING" : "REQUESTED")
                    .orElse("NONE"); // FIX: "NOT_FOLLOWING" → "NONE"

            // 3. Visibility
            boolean canViewPosts = !profileUser.isPrivate()
                    || "FOLLOWING".equals(followStatus);

            // 4. Counts
            long followersCount = followRepository
                    .countByFollowingIdAndStatus(profileUserId, "ACCEPTED");
            long followingCount = followRepository
                    .countByFollowerIdAndStatus(profileUserId, "ACCEPTED");
            long postCount = postRepository.countByUserId(profileUserId);

            // 5. Posts
            List<PostResponse> finalPosts = new ArrayList<>();

            if (canViewPosts) {
                List<Post> posts = postRepository
                        .findByUserIdOrderByCreatedAtDesc(profileUserId);

                if (!posts.isEmpty()) {
                    List<UUID> postIds = posts.stream().map(Post::getId).toList();

                    List<PostMedia> allMedia = postMediaRepository.findByPostIdIn(postIds);
                    Map<UUID, List<MediaDTO>> mediaMap = new HashMap<>();
                    for (PostMedia m : allMedia) {
                        MediaDTO dto = new MediaDTO(m.getUrl(), m.getType().name());
                        mediaMap.computeIfAbsent(m.getPost().getId(),
                                k -> new ArrayList<>()).add(dto);
                    }


                    for (Post p : posts) {
                        List<MediaDTO> media = mediaMap.getOrDefault(p.getId(), new ArrayList<>());
                        finalPosts.add(new PostResponse(
                                p.getId(),
                                profileUser.getUsername(),   // ← FIX
                                profileUser.getId(),          // ← FIX
                                profileUser.getProfilePic(), // ← FIX
                                p.getCaption(),
                                p.getLikeCount(),
                                false,
                                media,
                                p.getCommentCount()
                        ));
                    }
                }
            }

            logger.info("Profile built: posts={}, followStatus={}", finalPosts.size(), followStatus);

            // 6. Return
            return new UserProfileResponse(
                    true,
                    profileUser.getUsername(),
                    profileUser.getProfilePic(),
                    profileUser.getBio(),
                    (int) followersCount,
                    (int) followingCount,
                    (int) postCount,
                    followStatus,
                    profileUser.isPrivate(),
                    canViewPosts,
                    finalPosts
            );

        } catch (Exception e) {
            // FIX: error print karo — pehle silently fail ho raha tha
            logger.error("getProfile FAILED: {}", e.getMessage(), e);

            return new UserProfileResponse(
                    false, "Unknown", null, null,
                    0, 0, 0, "NONE",
                    false, false, new ArrayList<>()
            );
        }
    }

    public CommentResponseDto addComment(CommentRequestDto commentRequestDto) {
        try{
            Post post = postRepository.findById(commentRequestDto.getPostId())
                    .orElseThrow(() -> new RuntimeException("Post not found"));
            UserEntity user1=getLoggedInUser();

            Comment comment=new Comment();
            comment.setPost(post);
            comment.setUser(user1);
            comment.setText(commentRequestDto.getText());
            Comment savedComment= commentRepository.save(comment);
            post.setCommentCount(post.getCommentCount()+1);
            postRepository.save(post);


            return CommentResponseDto.builder()
                    .id(savedComment.getId())
                    .postId(post.getId())
                    .userId(user1.getId())
                    .username(user1.getUsername())
                    .profilePic(user1.getProfilePic())
                    .text(savedComment.getText())
                    .createdAt(savedComment.getCreatedAt())
                    .build();

        } catch (RuntimeException e) {
            e.printStackTrace();
            logger.error("Error :: {} ",e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
    public UserEntity getLoggedInUser(){

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();
        CustomUserDetail user=(CustomUserDetail) authentication.getPrincipal();
        return user.getUser();
    }

    public List<CommentResponseDto> getCommentsByPostId(UUID postId) {

        List<Comment> comments = commentRepository.findByPostId(postId);

        if (comments.isEmpty()) {
            throw new RuntimeException("No comments found");
        }

        List<CommentResponseDto> responseList = new ArrayList<>();

        for (Comment c : comments) {
            CommentResponseDto dto = CommentResponseDto.builder()
                    .id(c.getId())
                    .postId(c.getPost().getId())
                    .userId(c.getUser().getId())
                    .username(c.getUser().getUsername())
                    .profilePic(c.getUser().getProfilePic())
                    .text(c.getText())
                    .createdAt(c.getCreatedAt())
                    .build();

            responseList.add(dto);
        }

        return responseList;
    }


    public ProfileResponseDto getMyProfile() {

        UserEntity user = getLoggedInUser();

        Long followersCount = followRepository
                .countByFollowingIdAndStatus(user.getId(), "ACCEPTED");

        Long followingCount = followRepository
                .countByFollowerIdAndStatus(user.getId(), "ACCEPTED");
        Long likes=postLikeRepository.countByUserId(user.getId());
        Long comments=commentRepository.countByUserId(user.getId());


        Long postCount = postRepository.countByUserId(user.getId());

        return ProfileResponseDto.builder()
                .id(user.getId())

                .username(user.getUsername() != null ? user.getUsername() : "N/A")
                .website(user.getWebsite()!=null ?user.getWebsite() :"N/A")
                .location(user.getLocation()!=null ? user.getLocation() : "N/A")
                .createdAt(user.getCreatedAt() !=null ? user.getCreatedAt() :LocalDateTime.now())
                .email(user.getEmail() != null ? user.getEmail() : "N/A")
                .displayName(user.getFullName() != null ? user.getFullName() : "N/A")
                .bio(user.getBio() != null ? user.getBio() : "N/A")
                .profilePic(user.getProfilePic() != null ? user.getProfilePic() : "N/A")
                .coverImage("N/A")
                .totalLikes(likes)
                .totalComments(comments)
                .profileViews(0L)

                .followersCount(followersCount != null ? followersCount : 0L)
                .followingCount(followingCount != null ? followingCount : 0L)
                .postCount(postCount != null ? postCount : 0L)

                .isPrivate(user.isPrivate())
                .isVerified(user.isVerified())
                .isCurrentUser(true)

                .build();
    }

    public String profilesUpdate(ProfileResponseDto profileResponseDto) {
//        UserEntity usr=userRepository.findByUsername(profileResponseDto.getUsername());
        UserEntity user = getLoggedInUser();
        UserEntity existing = userRepository.findByUsername(profileResponseDto.getUsername());

        if (existing != null && !existing.getId().equals(user.getId())) {
            return "Username Already Exists";
        }

        user.setUsername(profileResponseDto.getUsername());
        user.setBio(profileResponseDto.getBio());
        user.setLocation(profileResponseDto.getLocation());
        user.setWebsite(profileResponseDto.getWebsite());
        userRepository.save(user);
        return "User Saved SuccessFully";
    }

    public String uploadProfileVatar(
            MultipartFile file,
            HttpServletRequest request
    ) throws IOException {

        List<MultipartFile> files =
                List.of(file);

        List<MediaUploadResponse> uploaded =
                mediaService.upload(files);

        String imageUrl =
                uploaded.get(0).getUrl();

        UserEntity user =
                getLoggedInUser();

        user.setProfilePic(imageUrl);

        userRepository.save(user);

        return imageUrl;
    }

    public List<ProfilePostDTOResponse> getLoggedUserPost(HttpServletRequest request) {
        String url = getBaseUrl(request);

        UserEntity user = getLoggedInUser();

        List<Post> posts = postRepository.findByUserId(user.getId());


        List<UUID> postIds = posts.stream()
                .map(Post::getId)
                .toList();


        List<PostMedia> allMedia = postMediaRepository.findByPostIdIn(postIds);


        Map<UUID, String> mediaMap = new HashMap<>();

        for (PostMedia media : allMedia) {

            mediaMap.putIfAbsent(media.getPost().getId(), media.getUrl());
        }


        List<ProfilePostDTOResponse> responseList = new ArrayList<>();

        for (Post post : posts) {

            String thumbnail = mediaMap.get(post.getId());
            logger.error("Url in loggedin UserPost {}",thumbnail);// null ho sakta hai

            ProfilePostDTOResponse dto = ProfilePostDTOResponse.builder()
                    .id(post.getId())
                    .thumbnail(thumbnail)
                    .likesCount((long) post.getLikeCount())
                    .commentsCount((long) post.getCommentCount())
                    .build();

            responseList.add(dto);
        }

        return responseList;
    }

    public List<UserMessageResponse> getALlUser() {

        try {

            UserEntity currentUser=getLoggedInUser();

            List<UserEntity> users =
                    userRepository.findAll();

            List<UserMessageResponse>
                    userMessageResponses =
                    new ArrayList<>();

            for(UserEntity u1 : users){

                if(
                        u1.getId()
                                .equals(
                                        currentUser.getId()
                                )
                ){
                    continue;
                }

                boolean online =

                        OnlineUsers
                                .ONLINE_USERS
                                .contains(
                                        u1.getUsername()
                                );

                int count =

                        chatMessageRepository
                                .countBySenderIdAndReceiverIdAndSeenFalse(

                                        u1.getId(),

                                        currentUser.getId()
                                );

                Optional<ChatMessage> lastMessageOptional =

                        chatMessageRepository

                                .findTopBySenderIdAndReceiverIdOrSenderIdAndReceiverIdOrderByCreatedAtDesc(

                                        currentUser.getId(),

                                        u1.getId(),

                                        u1.getId(),

                                        currentUser.getId()
                                );

                String lastMessage = "";

                if(lastMessageOptional.isPresent()){

                    lastMessage =

                            lastMessageOptional
                                    .get()
                                    .getMessage();
                }

                UserMessageResponse u =

                        UserMessageResponse
                                .builder()

                                .id(
                                        u1.getId()
                                )

                                .username(
                                        u1.getUsername()
                                )

                                .profilePic(
                                        u1.getProfilePic()
                                )

                                .online(
                                        online
                                )

                                .unreadCount(
                                        count
                                )

                                .lastSeen(
                                        u1.getLastSeen()
                                )

                                .lastMessage(
                                        lastMessage
                                )

                                .build();

                userMessageResponses
                        .add(u);
            }

            return userMessageResponses;

        } catch(Exception e){

            e.printStackTrace();

            logger.error(
                    "Error while geeting user :: {}",
                    e.getMessage()
            );

            return Collections.emptyList();
        }
    }
}