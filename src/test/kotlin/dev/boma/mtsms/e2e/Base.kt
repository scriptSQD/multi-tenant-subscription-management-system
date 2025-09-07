package dev.boma.mtsms.e2e

import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@Transactional
@Testcontainers
abstract class Base {

	companion object {

		private val logger = LoggerFactory.getLogger("E2EBaseTest")

		@JvmStatic
		val postgresqlContainer by lazy {
			PostgreSQLContainer("postgres:17-alpine").apply {
				withReuse(true)
				start()
			}
		}

		@JvmStatic
		@DynamicPropertySource
		fun testcontainersDatabase(registry: DynamicPropertyRegistry) {
			val settings = getDatabaseSettings()
			registry.add("spring.datasource.url") { settings.jdbcUrl }
			registry.add("spring.datasource.username") { settings.username }
			registry.add("spring.datasource.password") { settings.password }
		}

		private fun getDatabaseSettings(): TestDbSettings {
			return getExplicitTestDbSettings()
			       ?: TestDbSettings(
				       postgresqlContainer.jdbcUrl,
				       postgresqlContainer.username,
				       postgresqlContainer.password,
			       ).also { logger.info("Using Testcontainers to execute E2E tests") }
		}

		private fun getExplicitTestDbSettings(): TestDbSettings? {
			val url = System.getenv("TEST_DATABASE_URL")
			if (url.isNullOrBlank()) {
				return null
			}

			val username = System.getenv("TEST_DATABASE_USERNAME")
			if (username.isNullOrBlank()) {
				return null
			}

			val password = System.getenv("TEST_DATABASE_PASSWORD")
			if (password.isNullOrBlank()) {
				return null
			}

			logger.info("Using explicit database credentials (user=$username) to execute E2E tests")
			return TestDbSettings("jdbc:$url", username, password)
		}

		private data class TestDbSettings(
			val jdbcUrl: String,
			val username: String,
			val password: String,
		)
	}
}