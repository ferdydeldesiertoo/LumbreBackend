package com.lumbre.friendship.service

import com.lumbre.friendship.dto.FriendshipRes
import com.lumbre.friendship.entity.Friendship
import com.lumbre.friendship.entity.FriendshipStatus
import com.lumbre.friendship.entity.toFriendshipRes
import com.lumbre.friendship.exception.FriendshipAlreadyRequestedException
import com.lumbre.friendship.exception.FriendshipNotFoundException
import com.lumbre.friendship.exception.InvalidFriendshipStatusException
import com.lumbre.friendship.exception.SelfFriendshipException
import com.lumbre.friendship.repository.FriendshipRepository
import com.lumbre.user.entity.User
import com.lumbre.user.exception.UserNotFoundException
import com.lumbre.user.repository.UserRepository
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class FriendshipService(
    private val friendshipRepository: FriendshipRepository,
    private val userRepository: UserRepository,
    private val entityManager: EntityManager
) {

    private val logger = LoggerFactory.getLogger(FriendshipService::class.java)

    @Transactional
    fun sendFriendshipRequest(currentUser: UUID, receiverId: UUID) : FriendshipRes {
        if (currentUser == receiverId) {
            logger.warn("Attempted to send a friend request to oneself: $currentUser")
            throw SelfFriendshipException("You cannot send a friend request to yourself")
        }

        if (!userRepository.existsById(receiverId)) {
            logger.warn("Attempted to send a friend request to an unregistered user")
            throw UserNotFoundException("The user you are trying to send a friend request does not exist")
        }

        if (friendshipRepository.existsBetweenUsers(currentUser, receiverId)) {
            logger.warn("Friend request already exists between: sender=$currentUser, receiver=$receiverId")
            throw FriendshipAlreadyRequestedException("Friend request already exists or was already requested")
        }

        val senderReference = entityManager.getReference(User::class.java, currentUser)
        val receiverReference = entityManager.getReference(User::class.java, receiverId)

        val friendship = Friendship(null, senderReference, receiverReference)

        friendshipRepository.save(friendship)

        return friendship.toFriendshipRes()
    }

    @Transactional
    fun getFriendsList(currentUser: UUID): List<FriendshipRes> {
        val friendships = friendshipRepository.findAcceptedFriendships(currentUser)

        return friendships.map{it.toFriendshipRes()}
    }

    @Transactional
    fun getPendingFriendshipRequests(currentUser: UUID): List<FriendshipRes> {
        val friendships = friendshipRepository.findPendingFriendships(currentUser)

        return friendships.map{it.toFriendshipRes()}
    }

    @Transactional
    fun acceptFriendshipRequest(currentUser: UUID, senderId: UUID): FriendshipRes {
        if (senderId == currentUser) {
            logger.warn("Attempted to accept a friend request to oneself: $currentUser")
            throw SelfFriendshipException("You cannot accept your own request")
        }

        val friendship = friendshipRepository.findBySenderIdAndReceiverId(senderId, currentUser).orElseThrow {
            logger.warn("Attempted to accept a non-existent friendship request: user=$currentUser, sender=$senderId")
            FriendshipNotFoundException("Friend request not found")
        }

        if (friendship.status != FriendshipStatus.PENDING) {
            logger.warn("Attempted to accept a friend request with invalid status: ${friendship.status}")
            throw InvalidFriendshipStatusException("Friend request with invalid status")
        }

        friendship.status = FriendshipStatus.ACCEPTED

        return friendship.toFriendshipRes()
    }

    @Transactional
    fun rejectFriendshipRequest(currentUser: UUID, senderId: UUID) {
        if (senderId == currentUser) {
            logger.warn("Attempted to reject a friend request to oneself: $currentUser")
            throw SelfFriendshipException("You cannot reject your own request")
        }

        val friendship = friendshipRepository.findBySenderIdAndReceiverId(senderId, currentUser).orElseThrow {
            logger.warn("Attempted to reject a non-existent friendship request: user=$currentUser, sender=$senderId")
            FriendshipNotFoundException("Friend request not found")
        }

        if (friendship.status != FriendshipStatus.PENDING) {
            logger.warn("Attempted to reject a friend request with invalid status: ${friendship.status}")
            throw InvalidFriendshipStatusException("Friend request with invalid status")
        }

        friendshipRepository.delete(friendship)
        logger.info("Friend request rejected: sender=$senderId, receiver=$currentUser")
    }

    @Transactional
    fun cancelFriendshipRequest(currentUser: UUID, receiverId: UUID) {
        if (currentUser == receiverId) {
            logger.warn("Attempted to cancel a friend request to oneself: $currentUser")
            throw SelfFriendshipException("You cannot cancel your own request")
        }

        val friendship = friendshipRepository.findBySenderIdAndReceiverId(currentUser, receiverId).orElseThrow {
            logger.warn("Attempted to cancel a non-existent friendship request: user=$currentUser, friend=$receiverId")
            FriendshipNotFoundException("Friend request not found")
        }

        friendshipRepository.delete(friendship)
        logger.info("Friend request canceled: sender=$currentUser, receiver=$receiverId")
    }

    @Transactional
    fun removeFriendship(currentUser: UUID, friendId: UUID) {
        if (currentUser == friendId) {
            logger.warn("Attempted to remove a friendship to oneself: $currentUser")
            throw SelfFriendshipException("You cannot remove your own friendship")
        }

        val friendship = friendshipRepository.findBetweenUsers(currentUser, friendId).orElseThrow {
            logger.warn("Attempted to remove a non-existent friendship: user=$currentUser, friend=$friendId")
            FriendshipNotFoundException("Friend request not found")
        }

        friendshipRepository.delete(friendship)
        logger.info("Friendship removed: user=$currentUser, friend=$friendId")
    }
}