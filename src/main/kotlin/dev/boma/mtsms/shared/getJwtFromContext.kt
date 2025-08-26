package dev.boma.mtsms.shared

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt

fun getJwtFromContext(): Jwt {
    val authentication = SecurityContextHolder.getContext().authentication
        ?: throw IllegalStateException("Execution context is missing authentication")

    val jwt = when (authentication.principal) {
        is Jwt -> authentication.principal as Jwt
        else -> throw IllegalStateException("Authentication principal is not a JWT")
    }

    return jwt
}