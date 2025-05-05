package com.lumbre.user.controller

import com.lumbre.user.dto.LoginUserReq
import com.lumbre.user.dto.RegisterUserReq
import com.lumbre.user.dto.TokenRes
import com.lumbre.user.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/auth")
class UserController(private val service: UserService) {

    @PostMapping("/register")
    fun register(@Valid @RequestBody registerRequest: RegisterUserReq): ResponseEntity<TokenRes> {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createUser(registerRequest))
    }
    @PostMapping("/login")
    fun login(@Valid @RequestBody loginRequest: LoginUserReq): ResponseEntity<TokenRes> {
        return ResponseEntity.status(HttpStatus.OK).body(service.loginUser(loginRequest))
    }
}