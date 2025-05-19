package com.lumbre.message.entity

import com.lumbre.message.dto.MessageRes
import com.lumbre.user.entity.User
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "messages")
class Message (
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    val sender: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    val receiver: User,

    @Column(nullable = false, columnDefinition = "TEXT")
    var content: String,

    @Column(insertable = false, updatable = false)
    val sentAt: LocalDateTime? = null
)

fun Message.toMessageRes(currentUserId: UUID): MessageRes {
    return MessageRes(id!!, content, sender.id!!, sentAt!!, sender.id == currentUserId)
}