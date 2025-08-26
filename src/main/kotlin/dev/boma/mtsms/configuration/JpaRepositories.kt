package dev.boma.mtsms.configuration

import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableJpaRepositories(basePackages = ["dev.boma.mtsms"])
class JpaRepositories