package com.lumbre.friendship.dto

import com.lumbre.friendship.entity.FriendshipStatus
import java.util.*

data class FriendshipRes(val id: UUID, val senderId: UUID, val receiverId: UUID, val status: FriendshipStatus)
