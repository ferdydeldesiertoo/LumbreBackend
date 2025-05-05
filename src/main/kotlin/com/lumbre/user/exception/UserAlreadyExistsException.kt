package com.lumbre.user.exception

class UserAlreadyExistsException(username: String) : RuntimeException(username) {
}