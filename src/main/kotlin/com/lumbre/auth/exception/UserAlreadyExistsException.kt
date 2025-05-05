package com.lumbre.auth.exception

class UserAlreadyExistsException(username: String) : RuntimeException(username) {
}