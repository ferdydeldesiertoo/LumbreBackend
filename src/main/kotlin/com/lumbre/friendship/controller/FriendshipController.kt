package com.lumbre.friendship.controller

import com.lumbre.friendship.dto.FriendshipRes
import com.lumbre.friendship.dto.SendFriendshipReq
import com.lumbre.friendship.service.FriendshipService
import com.lumbre.security.CustomUserDetails
import com.lumbre.security.annotation.CurrentUser
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/friendship")
class FriendshipController(val friendshipService: FriendshipService) {
    @PostMapping
    fun addFriendship(
        @CurrentUser currentUser: CustomUserDetails,
        @RequestBody friendshipReq: SendFriendshipReq
    ): ResponseEntity<FriendshipRes> {
        val friendship = friendshipService.sendFriendshipRequest(currentUser.getId(), friendshipReq.receiverId)

        return ResponseEntity.status(HttpStatus.CREATED).body(friendship)
    }

    @GetMapping("/friends")
    fun getFriendships(@CurrentUser currentUser: CustomUserDetails): ResponseEntity<List<FriendshipRes>> {
        val friendships = friendshipService.getFriendsList(currentUser.getId())

        return ResponseEntity.status(HttpStatus.OK).body(friendships)
    }

    @GetMapping("/requests")
    fun getPendingFriendshipRequests(@CurrentUser currentUser: CustomUserDetails): ResponseEntity<List<FriendshipRes>> {
        val friendships = friendshipService.getPendingFriendshipRequests(currentUser.getId())

        return ResponseEntity.status(HttpStatus.OK).body(friendships)
    }

    @PatchMapping("/{senderId}/accept")
    fun acceptFriendship(
        @CurrentUser currentUser: CustomUserDetails,
        @PathVariable("senderId") senderId: UUID
    ): ResponseEntity<FriendshipRes> {
        val friendship = friendshipService.acceptFriendshipRequest(currentUser.getId(), senderId)

        return ResponseEntity.status(HttpStatus.OK).body(friendship)
    }

    @DeleteMapping("/{senderId}/reject")
    fun rejectFriendship(
        @CurrentUser currentUser: CustomUserDetails,
        @PathVariable("senderId") senderId: UUID
    ): ResponseEntity<Any> {
        friendshipService.rejectFriendshipRequest(currentUser.getId(), senderId)

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }

    @DeleteMapping("/{receiverId}/cancel")
    fun cancelFriendshipRequest(
        @CurrentUser currentUser: CustomUserDetails,
        @PathVariable("receiverId") receiverId: UUID
    ): ResponseEntity<Any> {
        friendshipService.cancelFriendshipRequest(currentUser.getId(), receiverId)

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }


    @DeleteMapping("/{receiverId}/remove")
    fun removeFriendship(
        @CurrentUser currentUser: CustomUserDetails,
        @PathVariable("receiverId") receiverId: UUID
    ): ResponseEntity<Any> {
        friendshipService.removeFriendship(currentUser.getId(), receiverId)

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}