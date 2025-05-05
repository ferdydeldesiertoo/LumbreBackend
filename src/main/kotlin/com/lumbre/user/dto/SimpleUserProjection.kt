package com.lumbre.user.dto

import java.util.UUID

interface SimpleUserProjection {
    val id: UUID
    val username: String
    val email: String
    val password: String
}