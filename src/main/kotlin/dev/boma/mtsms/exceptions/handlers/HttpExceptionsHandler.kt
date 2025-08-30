package dev.boma.mtsms.exceptions.handlers

import dev.boma.mtsms.serialization.ApiResponse
import dev.boma.mtsms.exceptions.http.HttpStatusException
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class HttpExceptionsHandler {

	@ExceptionHandler(HttpStatusException::class)
	fun handleHttpStatusException(e: HttpStatusException): ResponseEntity<ApiResponse> {
		return ResponseEntity
			.status(e.status)
			.contentType(MediaType.APPLICATION_JSON)
			.body(
				ApiResponse(
					status = e.status.value(),
					message = e.errorMessage
					          ?: e.reason
					          ?: e.cause?.message
					          ?: e.message.let {
						          @Suppress("UselessCallOnNotNull")
						          if (it.isNullOrBlank()) "Unknown error" else it
					          },
					data = e.data,
				),
			)
	}
}