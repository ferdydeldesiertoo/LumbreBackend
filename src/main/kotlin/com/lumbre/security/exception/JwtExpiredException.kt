package com.lumbre.security.exception

import org.springframework.security.core.AuthenticationException

class JwtExpiredException(message:String, cause: Throwable): AuthenticationException(message, cause) {
}