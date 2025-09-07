package dev.boma.mtsms.core.exceptions.handlers

import dev.boma.mtsms.core.serialization.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@RestControllerAdvice
class ValidationExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException::class)
	fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
		val errors = e.bindingResult.fieldErrors.associate {
			it.field to (it.defaultMessage
			             ?: "Invalid value")
		}

		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(
				ErrorResponse(
					status = HttpStatus.BAD_REQUEST.value(),
					message = "Request validation failed",
					data = mapOf("errors" to errors),
				),
			)
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException::class)
	fun handleMethodArgumentTypeMismatchException(e: MethodArgumentTypeMismatchException): ResponseEntity<ErrorResponse> {
		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(
				ErrorResponse(
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