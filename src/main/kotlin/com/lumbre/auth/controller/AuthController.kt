package com.lumbre.auth.controller

import com.lumbre.auth.dto.LoginUserReq
import com.lumbre.auth.dto.RegisterUserReq
import com.lumbre.auth.dto.AuthRes
import com.lumbre.auth.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/auth")
class AuthController(private val service: AuthService) {

    @PostMapping("/register")
    fun register(@Valid @RequestBody registerRequest: RegisterUserReq): ResponseEntity<AuthRes> {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createUser(registerRequest))
    }
    @PostMapping("/login")
    fun login(@Valid @RequestBody loginRequest: LoginUserReq): ResponseEntity<AuthRes> {
        return ResponseEntity.status(HttpStatus.OK).body(service.loginUser(loginRequest))
    }
}