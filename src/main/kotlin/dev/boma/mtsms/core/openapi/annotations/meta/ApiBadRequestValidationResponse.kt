package dev.boma.mtsms.core.openapi.annotations.meta

import dev.boma.mtsms.core.openapi.annotations.ApiBadRequestResponse
import dev.boma.mtsms.core.serialization.ErrorResponse
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.core.annotation.AliasFor

@ApiBadRequestResponse
annotation class ApiBadRequestValidationResponse(
	@get:AliasFor(annotation = ApiResponse::class, attribute = "description")
	val description: String = "",
	@get:AliasFor(annotation = ApiResponse::class, attribute = "content")
	val content: Array<Content> = [
		Content(
			mediaType = "application/json",
			schema = Schema(implementation = ErrorResponse::class),
			examples = [
				ExampleObject(
					name = "ErrorResponseBadRequestValidationExample",
					ref = "#/components/examples/ErrorResponseBadRequestValidationExample",
				)
			]
		)
	]
)
