package com.lumbre.friendship.repository

import com.lumbre.friendship.entity.Friendship
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface FriendshipRepository : JpaRepository<Friendship, UUID> {
    @Query("SELECT f FROM Friendship f WHERE (f.sender.id = :userId OR f.receiver.id = :userId) AND f.status = 'ACCEPTED'")
    fun findAcceptedFriendships(userId: UUID): List<Friendship>

    @Query("SELECT f FROM Friendship f WHERE (f.sender.id = :userId OR f.receiver.id = :userId) AND f.status = 'PENDING'")
    fun findPendingFriendships(userId: UUID): List<Friendship>

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

    @Query("SELECT f FROM Friendship f WHERE (f.sender.id = :userId AND f.receiver.id = :friendId) OR (f.sender.id = :friendId AND f.receiver.id = :userId)")
    fun findBetweenUsers(userId: UUID, friendId: UUID): Optional<Friendship>

    @Query("SELECT f FROM Friendship f WHERE f.sender.id = :senderId AND f.receiver.id = :receiverId")
    fun findBySenderIdAndReceiverId(senderId: UUID, receiverId: UUID): Optional<Friendship>
}