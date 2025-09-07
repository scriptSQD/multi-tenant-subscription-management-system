package dev.boma.mtsms.core.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableMethodSecurity
class SecurityConfiguration {

	@Bean
	fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
		http
			.authorizeHttpRequests {
				it
					.requestMatchers(HttpMethod.GET, "/health", "/actuator/**", "/swagger-ui*/**", "/v3/api-docs/**")
					.permitAll()
					.requestMatchers(HttpMethod.GET, "/subscription-plans/**")
					.permitAll()
					.requestMatchers(HttpMethod.GET, "/tenants/**")
					.hasAuthority("SCOPE_read:tenants")
					// All non-GET tenant endpoints require write scope
					.requestMatchers("/tenants/**")
					.hasAuthority("SCOPE_write:tenants")
					.anyRequest()
					.authenticated()
			}
			.anonymous { it.disable() }
			.csrf { it.disable() }
			.sessionManagement { it.disable() }
			.oauth2ResourceServer { it.jwt(withDefaults()) }

		return http.build()
	}
}