package com.lumbre.auth.service

import com.lumbre.security.CustomUserDetails
import com.lumbre.security.jwt.JwtService
import com.lumbre.auth.dto.LoginUserReq
import com.lumbre.auth.dto.RegisterUserReq
import com.lumbre.auth.dto.AuthRes
import com.lumbre.user.entity.User
import com.lumbre.auth.exception.UserAlreadyExistsException
import com.lumbre.user.repository.UserRepository
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager,
    private val jwtService: JwtService
) {

    private val logger = LoggerFactory.getLogger(AuthService::class.java)

    @Transactional
    fun createUser(registerUserReq: RegisterUserReq): AuthRes {
        if(userRepository.existsByEmail(registerUserReq.email)) {
            logger.warn("Attempted registration with an already existing email: ${registerUserReq.email}")
            throw UserAlreadyExistsException("Email is already registered")
        }

        if(userRepository.existsByUsername(registerUserReq.username)) {
            logger.warn("Attempted registration with an already existing username: ${registerUserReq.username}")
            throw UserAlreadyExistsException("Username is already taken")
        }


        val hashedPassword = passwordEncoder.encode(registerUserReq.password)

        val user = userRepository.save(User(null, registerUserReq.username, registerUserReq.email, hashedPassword))

        val token = jwtService.generateToken(user.id, user.username, user.email)

        return AuthRes(token, user.id!!)
    }

    @Transactional
    fun loginUser(loginUserReq: LoginUserReq): AuthRes {
        val authToken = UsernamePasswordAuthenticationToken(loginUserReq.identifier, loginUserReq.password)

        val authUser = authenticationManager.authenticate(authToken).principal as CustomUserDetails

        val token = jwtService.generateToken(authUser.getId(), authUser.username, authUser.getEmail())

        return AuthRes(token, authUser.getId())
    }
}
