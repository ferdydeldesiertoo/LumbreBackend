package com.lumbre.user.dto

import com.lumbre.friendship.entity.FriendshipStatus
import java.util.*

data class SearchUserRes(val id: UUID,
                         val username: String,
                         val bio: String?,
                         val profileImageUrl: String?,
                         val friendshipStatus: FriendshipStatusRes
)

enum class FriendshipStatusRes {
    NONE,
    ACCEPTED,
    REQUEST_SENT,
    REQUEST_RECEIVED
}
