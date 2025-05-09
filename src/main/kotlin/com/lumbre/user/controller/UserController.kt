package com.lumbre.user.controller

import com.lumbre.security.CustomUserDetails
import com.lumbre.security.annotation.CurrentUser
import com.lumbre.user.dto.UserInfoRes
import com.lumbre.user.service.UserService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {

    @GetMapping("/me")
    fun getCurrentUserInfo(@CurrentUser currentUser: CustomUserDetails): ResponseEntity<UserInfoRes> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userService.getUserInfoById(currentUser.getId()))
    }

    @GetMapping("/{username}")
    fun getUserInfo(@PathVariable username: String): ResponseEntity<UserInfoRes> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userService.getUserInfoByUsername(username))
    }

    @GetMapping("/search")
    fun searchUserInfo(
        @RequestParam username: String,
        @RequestParam(defaultValue = "0") page: Int
    ): ResponseEntity<Page<UserInfoRes>> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(userService.searchUsersByUsername(username, page))
    }
}
