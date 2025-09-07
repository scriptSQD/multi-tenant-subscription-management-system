package dev.boma.mtsms.core.configuration

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@EnableWebMvc
class CorsConfiguration : WebMvcConfigurer {

	private val allowedOrigins: Array<String> = arrayOf("http://localhost:3000", "https://dhcode.github.io")

	override fun addCorsMappings(registry: CorsRegistry) {
		registry.addMapping("/swagger-ui*/**").allowedOrigins(*allowedOrigins)
		registry.addMapping("/v3/api-docs/**").allowedOrigins(*allowedOrigins)
	}
}