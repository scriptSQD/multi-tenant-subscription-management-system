package dev.boma.mtsms.core.openapi.annotations

import dev.boma.mtsms.core.serialization.ErrorResponse
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.core.annotation.AliasFor

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ApiResponse(
	responseCode = "404",
	content = [
		Content(
			mediaType = "application/json",
			schema = Schema(implementation = ErrorResponse::class),
			examples = [
				ExampleObject(
					name = "ErrorResponseNotFoundExample",
					ref = "#/components/examples/ErrorResponseNotFoundExample",
				),
			],
		),
	],
)
annotation class ApiNotFoundResponse(
	@get:AliasFor(annotation = ApiResponse::class, attribute = "description")
	val description: String = "",
)