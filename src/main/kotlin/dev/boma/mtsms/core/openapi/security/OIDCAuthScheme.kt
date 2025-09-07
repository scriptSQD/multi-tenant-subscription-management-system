package dev.boma.mtsms.core.openapi.security

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.OAuthFlow
import io.swagger.v3.oas.annotations.security.OAuthFlows
import io.swagger.v3.oas.annotations.security.OAuthScope
import io.swagger.v3.oas.annotations.security.SecurityScheme
import org.springframework.stereotype.Component

@Component
@SecurityScheme(
	name = "OIDCAuthScheme",
	description = "Default OIDC Authorization Scheme",
	type = SecuritySchemeType.OPENIDCONNECT,
	openIdConnectUrl = "https://boma-dev-mtsms.eu.auth0.com/.well-known/openid-configuration",
	flows = OAuthFlows(
		authorizationCode = OAuthFlow(
			authorizationUrl = "https://boma-dev-mtsms.eu.auth0.com/authorize",
			tokenUrl = "https://boma-dev-mtsms.eu.auth0.com/oauth/token",
			scopes = [
				OAuthScope(name = "read:tenants", description = "Read Tenant information"),
				OAuthScope(name = "write:tenants", description = "Write Tenant information"),
			],
		),
	),
)
class OIDCAuthScheme