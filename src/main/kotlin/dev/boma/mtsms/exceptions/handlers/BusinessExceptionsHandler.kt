package dev.boma.mtsms.exceptions.handlers

import dev.boma.mtsms.exceptions.business.EntityNotFound
import dev.boma.mtsms.serialization.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class BusinessExceptionsHandler {

    @ExceptionHandler(EntityNotFound::class)
    fun handleEntityNotFoundException(e: EntityNotFound): ResponseEntity<ApiResponse> = ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .contentType(MediaType.APPLICATION_JSON)
        .body(
            ApiResponse(
                status = HttpStatus.NOT_FOUND.value(),
                message = e.message,
            )
        )
}