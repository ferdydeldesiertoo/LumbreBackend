package com.lumbre.friendship.repository

import com.lumbre.friendship.dto.FriendshipWithUserProjection
import com.lumbre.friendship.entity.Friendship
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface FriendshipRepository : JpaRepository<Friendship, UUID> {
    @Query(
        """
        SELECT 
            f.id as id,
            CASE 
                WHEN f.sender.id = :userId THEN f.receiver.id
                ELSE f.sender.id
            END as userId,
            CASE 
                WHEN f.sender.id = :userId THEN f.receiver.username
                ELSE f.sender.username
            END as username,
            CASE 
                WHEN f.sender.id = :userId THEN f.receiver.bio
                ELSE f.sender.bio
            END as bio,
            CASE 
                WHEN f.sender.id = :userId THEN f.receiver.profileImageUrl
                ELSE f.sender.profileImageUrl
            END as profileImageUrl
        FROM Friendship f
        JOIN f.sender s
        JOIN f.receiver r
        WHERE (f.sender.id = :userId OR f.receiver.id = :userId)
        AND f.status = 'ACCEPTED'
        """
    )
    fun findAcceptedFriendships(userId: UUID): List<FriendshipWithUserProjection>

    @Query(
        """
        SELECT 
            f.id as id,
            f.sender.id as userId,
            f.sender.username as username,
            f.sender.bio as bio,
            f.sender.profileImageUrl as profileImageUrl
        FROM Friendship f
        WHERE (f.receiver.id = :userId)
        AND f.status = 'PENDING'
        ORDER BY f.requestedAt DESC
        """
    )
    fun findPendingFriendships(userId: UUID): List<FriendshipWithUserProjection>

    @Query(
        """
        SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END 
        FROM Friendship f 
        WHERE (f.sender.id = :senderId AND f.receiver.id = :receiverId)
        OR (f.sender.id = :receiverId AND f.receiver.id = :senderId)
        """
    )
    fun existsBetweenUsers(
        senderId: UUID,
        receiverId: UUID
    ): Boolean

    @Query(
        """
            SELECT f FROM Friendship f
            WHERE (f.sender.id = :userId AND f.receiver.id IN :userIds)
            OR (f.sender.id IN :userIds AND f.receiver.id = : userId)
        """
    )
    fun findFriendshipsBetweenUserAndList(userId: UUID, userIds: List<UUID>): List<Friendship>

    @Query("SELECT f FROM Friendship f WHERE (f.sender.id = :userId AND f.receiver.id = :friendId) OR (f.sender.id = :friendId AND f.receiver.id = :userId)")
    fun findBetweenUsers(userId: UUID, friendId: UUID): Optional<Friendship>

    @Query("SELECT f FROM Friendship f WHERE f.sender.id = :senderId AND f.receiver.id = :receiverId")
    fun findBySenderIdAndReceiverId(senderId: UUID, receiverId: UUID): Optional<Friendship>
}