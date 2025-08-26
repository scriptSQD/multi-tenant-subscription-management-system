package dev.boma.mtsms.configuration

import dev.boma.mtsms.shared.ApiResponse
import dev.boma.mtsms.shared.HttpStatusException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class HttpStatusExceptionHandler {

    @ExceptionHandler(HttpStatusException::class)
    fun handleHttpStatusException(e: HttpStatusException): ApiResponse {
        return ApiResponse(
            status = e.status.value(),
            message = e.errorMessage
                ?: e.reason
                ?: e.cause?.message
                ?: e.message.let {
                    @Suppress("UselessCallOnNotNull")
                    if (it.isNullOrBlank()) "Unknown error" else it
                },
            data = e.data
        )
    }
}