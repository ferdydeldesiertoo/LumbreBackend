package com.lumbre.friendship.dto

import com.lumbre.friendship.entity.FriendshipStatus
import java.util.*

interface FriendshipWithUserProjection {
    fun getId(): UUID
    fun getUserId(): UUID
    fun getUsername(): String
    fun getBio(): String
    fun getProfileImageUrl(): String
    fun getStatus(): FriendshipStatus
}