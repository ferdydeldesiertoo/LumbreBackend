package com.lumbre.messages.repository

import com.lumbre.messages.entity.Message
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface MessageRepository: JpaRepository<Message, UUID> {
}