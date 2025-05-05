package com.lumbre.friendship.entity

import com.lumbre.friendship.dto.FriendshipRes
import com.lumbre.user.entity.User
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "friendships")
class Friendship(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    val sender: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    val receiver: User,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: FriendshipStatus = FriendshipStatus.PENDING,

    @Column(insertable = false, updatable = false)
    val requestedAt: LocalDateTime? = null
)

fun Friendship.toFriendshipRes(): FriendshipRes {
    return FriendshipRes(id!!, sender.id!!, receiver.id!!, status)
}


enum class FriendshipStatus {
    PENDING,
    ACCEPTED
}