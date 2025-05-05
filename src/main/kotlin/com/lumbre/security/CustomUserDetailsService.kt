package com.lumbre.security

import com.lumbre.user.dto.SimpleUserProjection
import com.lumbre.user.entity.User
import com.lumbre.user.exception.UserNotFoundException
import com.lumbre.user.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.util.*

@Service
class CustomUserDetailsService(private val userRepository: UserRepository) : UserDetailsService {
    private val logger = LoggerFactory.getLogger(CustomUserDetailsService::class.java)

    override fun loadUserByUsername(usernameOrEmail: String): UserDetails {
        return if (usernameOrEmail.contains("@")) {
            userRepository.findUserProjectionByEmail(usernameOrEmail).map(::mapToCustomUserDetails).orElseThrow {
                logger.warn("Email not registered: $usernameOrEmail")
                UsernameNotFoundException("Email not registered: $usernameOrEmail")
            }
        } else {
            userRepository.findUserProjectionByUsername(usernameOrEmail).map(::mapToCustomUserDetails).orElseThrow {
                logger.warn("Username not registered: $usernameOrEmail")
                UsernameNotFoundException("Username not registered: $usernameOrEmail")
            }
        }
    }

    fun loadUserByUserId(id: UUID): UserDetails {
        return userRepository.findUserProjectionById(id).map(::mapToCustomUserDetails)
            .orElseThrow {
                logger.warn("User not found with id: $id ")
                UserNotFoundException("User not found with id: $id")
            }
    }

    private fun mapToCustomUserDetails(userProjection: SimpleUserProjection): CustomUserDetails {
        // Maps the user object to CustomUserDetails, which is used for Spring Security authentication.
        return CustomUserDetails(userProjection.id, userProjection.username, userProjection.email, userProjection.password)
    }
}