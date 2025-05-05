package com.lumbre.user.dto

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.lumbre.common.TrimStringDeserializer
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class LoginUserReq(
    @field:NotBlank(message = "Identifier cannot be empty")
    @JsonDeserialize(using = TrimStringDeserializer::class)
    val identifier: String,

    @field:NotBlank(message = "Password cannot be empty")
    @field:Size(min = 8, max = 20, message = "Password must contain between 8 to 20 characters.")
    @JsonDeserialize(using = TrimStringDeserializer::class)
    val password: String
)