package com.lumbre.auth.dto

import java.util.UUID

data class AuthRes(val token: String, val userId: UUID)
