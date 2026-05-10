package com.social.SocialHub.repository;

import com.social.SocialHub.entity.ChatMessage;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatMessageRepository
        extends JpaRepository<ChatMessage, UUID> {

    @Query("""

    SELECT m

    FROM ChatMessage m

    WHERE

    (
        m.senderId = :user1
        AND
        m.receiverId = :user2
    )

    OR

    (
        m.senderId = :user2
        AND
        m.receiverId = :user1
    )

    ORDER BY m.createdAt ASC

    """)
    List<ChatMessage> getChatMessages(
            UUID user1,
            UUID user2
    );

    @Modifying
    @Transactional
    @Query("""

    UPDATE ChatMessage m

    SET m.seen = true

    WHERE

    m.senderId = :senderId

    AND

    m.receiverId = :receiverId

    AND

    m.seen = false

    """)
    int markMessagesSeen(
            UUID senderId,
            UUID receiverId
    );

    int countBySenderIdAndReceiverIdAndSeenFalse(UUID id, UUID id1);

    Optional<ChatMessage> findTopBySenderIdAndReceiverIdOrSenderIdAndReceiverIdOrderByCreatedAtDesc(UUID id, UUID id1, UUID id2, UUID id3);
}