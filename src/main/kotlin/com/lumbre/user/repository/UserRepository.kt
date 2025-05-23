package com.lumbre.user.repository

import com.lumbre.auth.dto.SimpleUserProjection
import com.lumbre.user.dto.UserInfoRes
import com.lumbre.user.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional
import java.util.UUID

@Repository
interface UserRepository: JpaRepository<User, UUID> {
    fun findByUsername(username: String): Optional<User>
    fun findByEmail(email: String): Optional<User>
    fun existsByUsername(username: String): Boolean
    fun existsByEmail(email: String): Boolean

    fun findByUsernameContainingIgnoreCase(username: String, pageable: Pageable): Page<UserInfoRes>

    fun findUserProjectionById(id: UUID) : Optional<SimpleUserProjection>
    fun findUserProjectionByUsername(username: String) : Optional<SimpleUserProjection>
    fun findUserProjectionByEmail(email: String) : Optional<SimpleUserProjection>
}