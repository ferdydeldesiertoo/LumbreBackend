package com.lumbre.exception

import com.lumbre.friendship.exception.FriendshipAlreadyRequestedException
import com.lumbre.friendship.exception.FriendshipNotFoundException
import com.lumbre.friendship.exception.InvalidFriendshipStatusException
import com.lumbre.friendship.exception.SelfFriendshipException
import com.lumbre.auth.exception.UserAlreadyExistsException
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.NoHandlerFoundException


@RestControllerAdvice
class GlobalExceptionHandler {
    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(
        e: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<Map<String, Any>> {
        val errors = e.bindingResult.fieldErrors.associate {
            it.field to (it.defaultMessage ?: "Invalid value")
        }

        val data = ExceptionBuilder.build(
            HttpStatus.BAD_REQUEST,
            "VALIDATION.FAILED",
            "Validation error",
            errors,
            request.requestURI,
            request.method
        )

        logger.warn("Validation failed for request: ${e.parameter} - Errors: $errors")

        return ResponseEntity(data, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(UserAlreadyExistsException::class)
    fun handleUserAlreadyExistsException(
        e: UserAlreadyExistsException,
        request: HttpServletRequest
    ): ResponseEntity<Map<String, Any>> {
        val data = ExceptionBuilder.build(
            HttpStatus.CONFLICT,
            "USER.ALREADY_EXISTS",
            "User already exists",
            e.message ?: "User already exists",
            request.requestURI,
            request.method
        )

        return ResponseEntity(data, HttpStatus.CONFLICT)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(
        exception: HttpMessageNotReadableException?,
        request: HttpServletRequest
    ): ResponseEntity<Map<String, Any>> {
        val data: Map<String, Any> =
            ExceptionBuilder.build(
                HttpStatus.BAD_REQUEST,
                "REQUEST.MALFORMED",
                "Invalid request",
                "The JSON request body is invalid or incorrectly formatted",
                request.requestURI,
                request.method
            )

        logger.warn("Malformed JSON request at ${request.requestURI} - Method: ${request.method}")

        return ResponseEntity(data, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(
        NoHandlerFoundException::class,
        HttpRequestMethodNotSupportedException::class
    )
    fun handleNoHandlerFoundException(exception: Exception, request: HttpServletRequest): ResponseEntity<*> {
        val data: Map<String, Any> =
            ExceptionBuilder.build(
                HttpStatus.NOT_FOUND,
                "REQUEST.NOT_FOUND",
                "Route not found",
                exception.message ?: "Route ${request.requestURI} - ${request.method} not found",
                request.requestURI,
                request.method
            )

        logger.warn("Route not found for request: ${request.requestURI} - Method: ${request.method}")

        return ResponseEntity(data, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException::class)
    fun handleHttpMediaTypeNotAcceptableException(
        exception: HttpMediaTypeNotSupportedException,
        request: HttpServletRequest
    ): ResponseEntity<Map<String, Any>> {
        val data: Map<String, Any> =
            ExceptionBuilder.build(
                HttpStatus.BAD_REQUEST,
                "REQUEST.CONTENT_TYPE_NOT_SUPPORTED",
                "Content-Type not supported",
                exception.message ?: "Content type ${request.contentType} not supported",
                request.requestURI,
                request.method
            )

        logger.warn("Content-Type ${request.contentType} not supported for request: ${request.requestURI} - Method: ${request.method}")

        return ResponseEntity(data, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(
        exception: IllegalArgumentException,
        request: HttpServletRequest
    ): ResponseEntity<Map<String, Any>> {
        val data: Map<String, Any> =
            ExceptionBuilder.build(
                HttpStatus.BAD_REQUEST,
                "REQUEST.ILLEGAL_ARGUMENT",
                "Illegal argument",
                exception.message ?: "Illegal argument",
                request.requestURI,
                request.method
            )

        logger.warn("Illegal argument for request: ${request.requestURI} - Method: ${request.method}. Message: ${exception.message}")

        return ResponseEntity(data, HttpStatus.BAD_REQUEST)
    }

    //**--**--FRIENDSHIP EXCEPTIONS HANDLERS--**--**
    @ExceptionHandler(FriendshipNotFoundException::class)
    fun handleFriendshipNotFoundException(
        exception: FriendshipNotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<Map<String, Any>> {
        val data: Map<String, Any> =
            ExceptionBuilder.build(
                HttpStatus.NOT_FOUND,
                "FRIENDSHIP.NOT_FOUND",
                "Friendship not found",
                exception.message ?: "Friendship not found",
                request.requestURI,
                request.method
            )
        return ResponseEntity(data, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(FriendshipAlreadyRequestedException::class)
    fun handleFriendshipAlreadyRequestedException(
        exception: FriendshipAlreadyRequestedException,
        request: HttpServletRequest
    ): ResponseEntity<Map<String, Any>> {
        val data: Map<String, Any> =
            ExceptionBuilder.build(
                HttpStatus.CONFLICT,
                "FRIENDSHIP.ALREADY_REQUESTED",
                "Friendship already requested",
                exception.message ?: "Friendship request",
                request.requestURI,
                request.method
            )
        return ResponseEntity(data, HttpStatus.CONFLICT)
    }

    @ExceptionHandler(InvalidFriendshipStatusException::class)
    fun handleInvalidFriendshipStatusException(exception: InvalidFriendshipStatusException, request: HttpServletRequest): ResponseEntity<Map<String, Any>> {
        val data: Map<String, Any> =
            ExceptionBuilder.build(
                HttpStatus.BAD_REQUEST,
                "FRIENDSHIP.INVALID_STATUS",
                "Invalid friendship status",
                exception.message ?: "Invalid friendship status",
                request.requestURI,
                request.method
            )
        return ResponseEntity(data, HttpStatus.BAD_REQUEST)
    }
    @ExceptionHandler(SelfFriendshipException::class)
    fun handleSelfFriendshipException(exception: SelfFriendshipException, request: HttpServletRequest): ResponseEntity<Map<String, Any>> {
        val data: Map<String, Any> =
            ExceptionBuilder.build(
                HttpStatus.CONFLICT,
                "FRIENDSHIP.SELF_ACTION",
                "Invalid action",
                exception.message ?: "You cannot modify a friendship with yourself",
                request.requestURI,
                request.method
            )
        return ResponseEntity(data, HttpStatus.CONFLICT)
    }
}