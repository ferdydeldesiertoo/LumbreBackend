    package com.lumbre.user.service

    import com.lumbre.auth.exception.UserNotFoundException
    import com.lumbre.user.dto.UserInfoRes
    import com.lumbre.user.entity.toUserInfo
    import com.lumbre.user.repository.UserRepository
    import jakarta.transaction.Transactional
    import org.slf4j.LoggerFactory
    import org.springframework.data.domain.Page
    import org.springframework.data.domain.PageRequest
    import org.springframework.data.domain.Sort
    import org.springframework.stereotype.Service
    import java.util.UUID

    @Service
    class UserService(private val userRepository: UserRepository) {
        private val logger = LoggerFactory.getLogger(UserService::class.java)

        @Transactional
        fun getUserInfoById(id: UUID) : UserInfoRes {
            val user = userRepository.findById(id).orElseThrow{
                logger.warn("Cannot find user with id: $id")
                UserNotFoundException("User with id $id not found")
            }

            return user.toUserInfo()
        }

        @Transactional
        fun getUserInfoByUsername(username: String) : UserInfoRes {
            val user = userRepository.findByUsername(username).orElseThrow {
                logger.warn("Cannot find user with username: $username")
                UserNotFoundException("User with username $username not found")
            }

            return user.toUserInfo()
        }

        @Transactional
        fun searchUsersByUsername(username: String, page: Int): Page<UserInfoRes> {
            val pageable = PageRequest.of(page, 10, Sort.by("username").ascending())
            val users = userRepository.findByUsernameContainingIgnoreCase(username, pageable)
            return users
        }
    }