package com.lumbre.user.controller

import com.lumbre.security.CustomUserDetails
import com.lumbre.security.annotation.CurrentUser
import com.lumbre.user.dto.SearchUserRes
import com.lumbre.user.dto.UpdateUserReq
import com.lumbre.user.dto.UserInfoRes
import com.lumbre.user.service.UserService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {

    @GetMapping("/me")
    fun getCurrentUserInfo(@CurrentUser currentUser: CustomUserDetails): ResponseEntity<UserInfoRes> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userService.getUserInfoById(currentUser.getId()))
    }

    @PatchMapping("/me")
    fun updateCurrentUserInfo(@CurrentUser currentUser: CustomUserDetails, @RequestBody updateUserReq: UpdateUserReq): ResponseEntity<UserInfoRes> {
        println(updateUserReq)
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userService.updateUser(currentUser.getId(), updateUserReq))
    }

    @GetMapping("/{username}")
    fun getUserInfo(@PathVariable username: String): ResponseEntity<UserInfoRes> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userService.getUserInfoByUsername(username))
    }

    @GetMapping("/search")
    fun searchUserInfo(
        @CurrentUser currentUser: CustomUserDetails,
        @RequestParam username: String,
        @RequestParam(defaultValue = "0") page: Int
    ): ResponseEntity<Page<SearchUserRes>> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userService.searchUsersByUsername(currentUser.getId(), username, page))
    }
}
