package com.lumbre.security.exception

import org.springframework.security.core.AuthenticationException

class JwtInvalidException: AuthenticationException {
    constructor(message: String): super(message)
    constructor(message: String, cause: Throwable): super(message, cause)
}