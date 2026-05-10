package com.social.SocialHub.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;
@Builder
public class UserMessageResponse{
    private UUID id;
    private String username;
    private String profilePic;
    private boolean online;
    private LocalDateTime lastSeen;
    private String lastMessage;
    private int unreadCount;

    public UserMessageResponse(UUID id, String username, String profilePic, boolean online, LocalDateTime lastSeen, String lastMessage, int unreadCount) {
        this.id = id;
        this.username = username;
        this.profilePic = profilePic;
        this.online = online;
        this.lastSeen = lastSeen;
        this.lastMessage = lastMessage;
        this.unreadCount = unreadCount;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public LocalDateTime getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(LocalDateTime lastSeen) {
        this.lastSeen = lastSeen;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
