package com.lumbre.auth.dto

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.lumbre.common.TrimStringDeserializer
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegisterUserReq(
    @field:NotBlank(message = "Username cannot be empty")
    @field:Size(min = 3, max = 10, message = "The username must contain between 3 to 10 characters")
    @JsonDeserialize(using = TrimStringDeserializer::class)
    val username : String,

    @field:NotBlank(message = "Email cannot be empty")
    @field:Email(message = "Email must be valid")
    @JsonDeserialize(using = TrimStringDeserializer::class)
    val email : String,

    @field:NotBlank(message = "Password cannot be empty")
    @field:Size(min = 8, max = 20, message = "Password must contain between 8 to 20 characters.")
    @JsonDeserialize(using = TrimStringDeserializer::class)
    val password : String
)
