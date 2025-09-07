package dev.boma.mtsms.core.openapi.exampleclasses

import dev.boma.mtsms.core.serialization.ErrorResponse
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.examples.Example
import org.springdoc.core.customizers.OpenApiCustomizer
import org.springframework.stereotype.Component

@Component
class ErrorResponseBadRequestValidationExample : OpenApiCustomizer {

	override fun customise(openApi: OpenAPI) {
		val example = Example().value(
			ErrorResponse(
				status = 400,
				message = "Request validation failed",
				data = mapOf(
					"errors" to mapOf(
						"name" to "must not be empty",
						"subscriptionPlan" to "should be selected",
					),
				),
			),
		)
		openApi.components.addExamples("ErrorResponseBadRequestValidationExample", example)
	}
}