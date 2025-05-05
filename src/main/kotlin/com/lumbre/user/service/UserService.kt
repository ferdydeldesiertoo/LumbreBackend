package com.lumbre.user.service

import com.lumbre.user.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {
}