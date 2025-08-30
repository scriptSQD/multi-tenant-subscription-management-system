package dev.boma.mtsms.exceptions.handlers

import dev.boma.mtsms.serialization.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@RestControllerAdvice
class ValidationExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException::class)
	fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ResponseEntity<ApiResponse> {
		val errors = e.bindingResult.fieldErrors.associate {
			it.field to (it.defaultMessage
			             ?: "Invalid value")
		}

		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(
				ApiResponse(
					status = HttpStatus.BAD_REQUEST.value(),
					message = "Request validation failed",
					data = mapOf("errors" to errors),
				),
			)
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException::class)
	fun handleMethodArgumentTypeMismatchException(e: MethodArgumentTypeMismatchException): ResponseEntity<ApiResponse> {
		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(
				ApiResponse(
					status = HttpStatus.BAD_REQUEST.value(),
					message = "Request validation failed",
					data = mapOf(
						"errors" to mapOf(
							e.name to "Expected valid value of type ${e.requiredType?.simpleName}",
						),
					),
				),
			)
	}
}