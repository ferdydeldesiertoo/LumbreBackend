package com.lumbre.message.dto

import java.time.LocalDateTime
import java.util.UUID

data class MessageRes(val id: UUID, val content: String, val senderId: UUID, val sentAt: LocalDateTime, var isFromCurrentUser: Boolean)
