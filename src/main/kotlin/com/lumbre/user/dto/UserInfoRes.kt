package com.lumbre.user.dto

import java.util.*

data class UserInfoRes(
    val id: UUID,
    val username: String,
    val bio: String?,
    val profileImageUrl: String?
)
