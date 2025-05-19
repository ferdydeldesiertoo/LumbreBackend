package com.lumbre.message.dto

import java.util.UUID

data class SendMessageReq(val receiverId: UUID, val content: String)
