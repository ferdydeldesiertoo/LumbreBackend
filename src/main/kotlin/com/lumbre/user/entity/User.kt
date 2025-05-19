package com.lumbre.user.entity

import com.lumbre.friendship.entity.Friendship
import com.lumbre.message.entity.Message
import com.lumbre.user.dto.UserInfoRes
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    var username: String = "",

    var email: String = "",

    var password: String = "",

    var bio: String? = null,

    var profileImageUrl: String? = null,

    @Column(insertable = false, updatable = false)
    val createdAt: LocalDateTime? = null,

    @OneToMany(mappedBy = "sender", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    val sentFriendRequests: List<Friendship> = emptyList(),

    @OneToMany(mappedBy = "receiver", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    val receivedFriendRequests: List<Friendship> = emptyList(),

    @OneToMany(mappedBy = "sender", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    val sentMessages: List<Message> = emptyList(),

    @OneToMany(mappedBy = "receiver", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    val receivedMessages: List<Message> = emptyList(),
)

fun User.toUserInfo(): UserInfoRes {
    return UserInfoRes(id!!, username, bio, profileImageUrl, createdAt!!)
}