package dev.boma.mtsms.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
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
                    .requestMatchers("/health", "/actuator/**", "/swagger-ui*/**", "/v3/api-docs/**")
                    .permitAll()

                    .anyRequest()
                    .authenticated()
            }
            .oauth2ResourceServer { it.jwt {} }
            .anonymous { it.disable() }
            .csrf { it.disable() }

        return http.build()
    }
}