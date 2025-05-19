package com.lumbre.message.controller

import com.lumbre.message.dto.ChatPreviewRes
import com.lumbre.message.dto.MessageRes
import com.lumbre.message.dto.SendMessageReq
import com.lumbre.message.entity.Message
import com.lumbre.message.service.MessageService
import com.lumbre.security.CustomUserDetails
import com.lumbre.security.annotation.CurrentUser
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/messages")
class MessageController(private val messageService: MessageService) {
    @PostMapping
    fun sendMessage(
        @CurrentUser currentUser: CustomUserDetails,
        @RequestBody messageReq: SendMessageReq
    ): ResponseEntity<MessageRes> {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(messageService.sendMessage(currentUser.getId(), messageReq))
    }

    @GetMapping("/{friendId}")
    fun getMessages(
        @CurrentUser currentUser: CustomUserDetails,
        @PathVariable friendId: UUID,
        @RequestParam(defaultValue = "0") page: Int
    ): ResponseEntity<Page<MessageRes>> {
        return ResponseEntity.status(HttpStatus.OK).body(messageService.getMessagesBetweenUsers(currentUser.getId(), friendId, page))
    }

    @GetMapping("/preview")
    fun getPreviewChats(@CurrentUser currentUser: CustomUserDetails): ResponseEntity<List<ChatPreviewRes>> {
        return ResponseEntity.status(HttpStatus.OK).body(messageService.getChatsPreview(currentUser.getId()))
    }
}