package dev.boma.mtsms.core.exceptions.handlers

import dev.boma.mtsms.core.exceptions.business.EntityNotFound
import dev.boma.mtsms.core.serialization.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class BusinessExceptionsHandler {

	@ExceptionHandler(EntityNotFound::class)
	fun handleEntityNotFoundException(e: EntityNotFound): ResponseEntity<ErrorResponse> = ResponseEntity
		.status(HttpStatus.NOT_FOUND)
		.contentType(MediaType.APPLICATION_JSON)
		.body(
			ErrorResponse(
				status = HttpStatus.NOT_FOUND.value(),
				message = e.message,
			),
		)
}