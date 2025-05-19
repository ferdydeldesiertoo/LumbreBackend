package com.lumbre.message.service

import com.lumbre.friendship.exception.FriendshipNotFoundException
import com.lumbre.friendship.exception.SelfFriendshipException
import com.lumbre.friendship.repository.FriendshipRepository
import com.lumbre.message.dto.ChatPreviewRes
import com.lumbre.message.dto.MessageRes
import com.lumbre.message.dto.SendMessageReq
import com.lumbre.message.entity.Message
import com.lumbre.message.entity.toMessageRes
import com.lumbre.message.repository.MessageRepository
import com.lumbre.user.entity.User
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class MessageService(
    private val messageRepository: MessageRepository,
    private val friendshipRepository: FriendshipRepository,
    private val entityManager: EntityManager
) {
    private val logger = LoggerFactory.getLogger(MessageService::class.java)

    @Transactional
    fun sendMessage(currentUser: UUID, messageReq: SendMessageReq) : MessageRes {
        if (currentUser == messageReq.receiverId) {
            logger.info("Attempted to send a message to oneself: $currentUser")
            throw SelfFriendshipException("You cannot send a message to yourself")
        }

        if (!friendshipRepository.existsBetweenUsers(currentUser, messageReq.receiverId)) {
            logger.info("Attempted to send a message to a non-existent friendship: user=$currentUser, receiver=${messageReq.receiverId}")
            throw FriendshipNotFoundException("Friendship not found")
        }

        val currentUserRef = entityManager.getReference(User::class.java, currentUser)
        val receiverUserRef = entityManager.getReference(User::class.java, messageReq.receiverId)

        val message = Message(null, currentUserRef, receiverUserRef, messageReq.content)
        entityManager.persist(message)

        entityManager.flush()
        entityManager.refresh(message)

        return message.toMessageRes(currentUser)
    }

    @Transactional
    fun getMessagesBetweenUsers(currentUser: UUID, friendId: UUID, page: Int): Page<MessageRes> {
        if (currentUser == friendId) {
            logger.info("Attempted to get messages to oneself: $currentUser")
            throw SelfFriendshipException("You cannot get messages to yourself")
        }

        if (!friendshipRepository.existsBetweenUsers(currentUser, friendId)) {
            logger.info("Attempted to get messages to a non-existent friendship: user=$currentUser, receiver=${friendId}")
            throw FriendshipNotFoundException("Friendship not found")
        }

        val pageable = PageRequest.of(page, 20)
        val messages = messageRepository.findBetweenUsers(currentUser, friendId, pageable).map { it.toMessageRes(currentUser) }

        return messages
    }

    @Transactional
    fun getChatsPreview(currentUser: UUID) : List<ChatPreviewRes> {
        val chats = messageRepository.findChatsPreview(currentUser)

        return chats
    }
}