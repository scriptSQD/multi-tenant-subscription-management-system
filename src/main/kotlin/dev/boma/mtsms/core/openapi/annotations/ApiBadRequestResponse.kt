package dev.boma.mtsms.core.openapi.annotations

import dev.boma.mtsms.core.serialization.ErrorResponse
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.core.annotation.AliasFor

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ApiResponse(
	responseCode = "400",
	content = [
		Content(
			mediaType = "application/json",
			schema = Schema(implementation = ErrorResponse::class),
		),
	],
)
annotation class ApiBadRequestResponse(
	@get:AliasFor(annotation = ApiResponse::class, attribute = "description")
	val description: String = "",
)
