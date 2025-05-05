package com.lumbre.exception

import org.springframework.http.HttpStatus
import java.time.Instant


class ExceptionBuilder {
    companion object {
        fun build(
            status: HttpStatus,
            errorCode: String,
            errorTitle: String,
            errorMessage: Any,
            path: String,
            method: String,
        ): Map<String, Any> {
            val data: MutableMap<String, Any> = HashMap()

            data["status"] = status.value() // Status code for the error
            data["error_code"] = errorCode
            data["error"] = errorTitle // Title describing the error
            data["message"] = errorMessage // Detailed message about the error
            data["path"] = path // Path of the request that caused the error
            data["method"] = method // HTTP method used in the request
            data["timestamp"] = Instant.now().toString() // Timestamp of the error

            return data
        }
    }
}