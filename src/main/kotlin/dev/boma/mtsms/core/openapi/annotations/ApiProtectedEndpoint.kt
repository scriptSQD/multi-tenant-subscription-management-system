package dev.boma.mtsms.core.openapi.annotations

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.core.annotation.AliasFor

@SecurityRequirement(name = "OIDCAuthScheme")
annotation class ApiProtectedEndpoint(
	@get:AliasFor(annotation = SecurityRequirement::class, attribute = "scopes")
	val scopes: Array<String> = []
)
