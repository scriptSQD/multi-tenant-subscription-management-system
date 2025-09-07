package dev.boma.mtsms.core.openapi.exampleclasses

import dev.boma.mtsms.core.serialization.ErrorResponse
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.examples.Example
import org.springdoc.core.customizers.OpenApiCustomizer
import org.springframework.stereotype.Component

@Component
class ErrorResponseNotFoundExample : OpenApiCustomizer {
    override fun customise(openApi: OpenAPI) {
        val example = Example().value(
            ErrorResponse(
                status = 404,
                message = "Not found",
				data = null,
            )
        )
        openApi.components.addExamples("ErrorResponseNotFoundExample", example)
    }
}