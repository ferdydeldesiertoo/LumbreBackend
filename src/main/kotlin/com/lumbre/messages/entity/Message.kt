package com.lumbre.messages.entity

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

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    val sender: User,

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    val receiver: User,

    @Column(nullable = false, columnDefinition = "TEXT")
    var content: String,

    @Column(insertable = false, updatable = false)
    val sentAt: LocalDateTime? = null
)