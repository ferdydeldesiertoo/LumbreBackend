package com.lumbre.websocket.service

import com.lumbre.message.dto.MessageRes
import com.lumbre.message.dto.SendMessageReq
import com.lumbre.message.service.MessageService
import org.slf4j.LoggerFactory
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ChatSocketService(private val messageService: MessageService, private val messagingTemplate: SimpMessagingTemplate) {
    private val logger = LoggerFactory.getLogger(ChatSocketService::class.java)

    fun sendMessage(senderId: UUID, messageReq: SendMessageReq) {
        logger.info("Attempted to send message user: $senderId. Content: $messageReq")

        val message = messageService.sendMessage(senderId, messageReq)

        // Prepare message copy for the receiver (mark as not from current user)
        val messageForReceiver = message.copy(isFromCurrentUser = false)

        // Send message back to sender's queue (for real-time update)
        messagingTemplate.convertAndSend("/queue/$senderId", message)

        // Send message to receiver's queue
        messagingTemplate.convertAndSend("/queue/${messageReq.receiverId}", messageForReceiver)
    }
}
