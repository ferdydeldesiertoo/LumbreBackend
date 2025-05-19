    package com.lumbre.user.service

    import com.lumbre.auth.exception.UserNotFoundException
    import com.lumbre.friendship.entity.FriendshipStatus
    import com.lumbre.friendship.repository.FriendshipRepository
    import com.lumbre.user.dto.FriendshipStatusRes
    import com.lumbre.user.dto.SearchUserRes
    import com.lumbre.user.dto.UpdateUserReq
    import com.lumbre.user.dto.UserInfoRes
    import com.lumbre.user.entity.User
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
    class UserService(
        private val userRepository: UserRepository,
        private val friendshipRepository: FriendshipRepository
    ) {
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
        fun updateUser(id: UUID, updateUserReq: UpdateUserReq): UserInfoRes {
            val user = userRepository.findById(id)
                .orElseThrow {
                    logger.warn("Attempt to update a non existent user with id: $id")
                    UserNotFoundException("User not found with id: $id")
                }

            user.bio = updateUserReq.bio

            val updatedUser = userRepository.save(user)

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
        fun searchUsersByUsername(currentUser: UUID, username: String, page: Int): Page<SearchUserRes> {
            if(username.isBlank()) return Page.empty()

            val pageable = PageRequest.of(page, 10, Sort.by("username").ascending())
            val users = userRepository.findByUsernameContainingIgnoreCase(username, pageable)
            val usersId = users.content.map { it.id }

            val friendships = friendshipRepository.findFriendshipsBetweenUserAndList(currentUser, usersId)

            val friendshipMap = friendships.associateBy {
                if(it.sender.id == currentUser) it.receiver.id else it.sender.id
            }

            return users.map { user ->
                val status = when (val f = friendshipMap[user.id]) {
                    null -> FriendshipStatusRes.NONE
                    else -> when (f.status) {
                        FriendshipStatus.ACCEPTED -> FriendshipStatusRes.ACCEPTED
                        FriendshipStatus.PENDING -> {
                            if (f.sender.id == currentUser) FriendshipStatusRes.REQUEST_SENT
                            else FriendshipStatusRes.REQUEST_RECEIVED
                        }
                    }
                }

                SearchUserRes(user.id, user.username, user.bio, user.profileImageUrl, status)
            }
        }
    }