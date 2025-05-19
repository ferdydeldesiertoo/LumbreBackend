package com.lumbre.websocket.controller

import java.util.UUID
import com.lumbre.message.dto.SendMessageReq
import com.lumbre.security.CustomUserDetails
import com.lumbre.websocket.service.ChatSocketService
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Controller

@Controller
class ChatSocketController(
    private val chatSocketService: ChatSocketService
) {
    @MessageMapping("/chat.send")
    fun handleSendMessage(@Payload message: SendMessageReq, accessor: SimpMessageHeaderAccessor) {
        // Try to get authenticated user from session attributes
        val authFromSession =
            accessor.sessionAttributes?.get("SPRING.SESSION.PRINCIPAL") as? UsernamePasswordAuthenticationToken
        val senderId: UUID
        if (authFromSession != null) {
            val userDetails = authFromSession.principal as CustomUserDetails
            senderId = userDetails.getId()
            println("user id desde controller: $senderId")
            // User successfully retrieved from session
        } else {
            // Fallback: get user from accessor.user (in case session attributes are not available)
            val user = accessor.user as? UsernamePasswordAuthenticationToken
            if (user != null) {
                val userDetails = user.principal as CustomUserDetails
                senderId = userDetails.getId()
            } else {
                // No authenticated user found
                throw Exception("User no authenticated")
            }
        }
        // Send message using service and resolved sender ID
        chatSocketService.sendMessage(senderId, message)
    }
}
