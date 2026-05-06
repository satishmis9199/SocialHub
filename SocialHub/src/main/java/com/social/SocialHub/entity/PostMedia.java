package com.social.SocialHub.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "post_media",
        indexes = {
                @Index(name = "idx_post_media_post", columnList = "post_id")
        }
)
@Getter @Setter
public class PostMedia extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false)
    private String fileName;
    private String url;// 🔥 only file name

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MediaType type;    // IMAGE / VIDEO

    private int orderIndex = 0;


}