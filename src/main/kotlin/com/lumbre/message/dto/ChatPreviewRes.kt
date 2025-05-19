package com.lumbre.message.dto

import java.time.LocalDateTime
import java.util.UUID

interface ChatPreviewRes {
    fun getFriendId(): UUID
    fun getUsername(): String
    fun getProfileImageUrl(): String
    fun getLastMessage(): String
    fun getLastMessageTime(): LocalDateTime
}
