package com.lumbre.message.repository

import com.lumbre.message.dto.ChatPreviewRes
import com.lumbre.message.dto.MessageRes
import com.lumbre.message.entity.Message
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface MessageRepository : JpaRepository<Message, UUID> {
    @Query(
        """
            SELECT m FROM Message m 
            WHERE (m.sender.id = :senderId AND m.receiver.id = :receiverId)
            OR (m.sender.id = :receiverId AND m.receiver.id = :senderId)
            ORDER BY m.sentAt DESC
        """
    )
    fun findBetweenUsers(senderId: UUID, receiverId: UUID, pageable: Pageable): Page<Message>

    @Query(nativeQuery = true, value = """
   SELECT 
    friend_id,
    username,
    profile_image_url,
    last_message,
    last_message_time
FROM (
    SELECT 
        CASE 
            WHEN m.sender_id = :userId THEN m.receiver_id
            ELSE m.sender_id
        END AS friend_id,
        u.username,
        u.profile_image_url,
        m.content AS last_message,
        m.sent_at AS last_message_time,
        ROW_NUMBER() OVER (
            PARTITION BY 
                CASE 
                    WHEN m.sender_id = :userId THEN m.receiver_id
                    ELSE m.sender_id
                END
            ORDER BY m.sent_at DESC
        ) AS rn
    FROM messages m
    JOIN users u ON u.id = 
        CASE 
            WHEN m.sender_id = :userId THEN m.receiver_id
            ELSE m.sender_id
        END
    WHERE (m.sender_id = :userId OR m.receiver_id = :userId)
) sub
WHERE rn = 1
ORDER BY last_message_time DESC;

""")
    fun findChatsPreview(userId: UUID): List<ChatPreviewRes>
}