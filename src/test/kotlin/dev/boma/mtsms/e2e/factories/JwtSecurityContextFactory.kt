package dev.boma.mtsms.e2e.factories

import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken

class JwtSecurityContextFactory {

	companion object {
		fun create(subject: String, additionalClaims: Map<String, Any>? = null): SecurityContext {
			return SecurityContextHolder
				.createEmptyContext()
				.apply {
					val jwt = Jwt
						.withTokenValue("token")
						.header("alg", "none")
						.subject(subject)
						.claims { claims -> additionalClaims?.let { claims.putAll(it) } }
						.build()

					authentication = JwtAuthenticationToken(jwt)
				}
		}
	}
}