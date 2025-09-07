package dev.boma.mtsms.core.openapi.annotations

import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.core.annotation.AliasFor

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ApiResponse(responseCode = "201")
annotation class ApiCreatedResponse(
	@get:AliasFor(annotation = ApiResponse::class, attribute = "description")
	val description: String = "",
)
