package com.lumbre.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.lumbre.exception.ExceptionBuilder
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class CustomAuthenticationEntryPoint : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        response.contentType = "application/json"
        response.status = HttpServletResponse.SC_UNAUTHORIZED

        val error = ExceptionBuilder.build(
            HttpStatus.UNAUTHORIZED, "AUTH.UNAUTHORIZED", "Unauthorized",
            authException.message ?: "Unknown error", request.requestURI, request.method
        )

        ObjectMapper().writeValue(response.outputStream, error)

    }

}