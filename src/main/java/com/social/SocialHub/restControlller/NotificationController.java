package com.social.SocialHub.restControlller;

import com.social.SocialHub.dto.NotificationResponseDto;
import com.social.SocialHub.entity.UserEntity;
import com.social.SocialHub.service.NotificationService;
import com.social.SocialHub.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final PostService postService;

    @GetMapping
    public ResponseEntity<List<NotificationResponseDto>>
    getNotifications() {
        UserEntity logged = postService.getLoggedInUser();

        return ResponseEntity.ok(
                notificationService
                        .getNotifications(logged.getId())
        );
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(
            @PathVariable UUID id) {

        notificationService.markAsRead(id);

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Notification marked as read"
                )
        );
    }
}
