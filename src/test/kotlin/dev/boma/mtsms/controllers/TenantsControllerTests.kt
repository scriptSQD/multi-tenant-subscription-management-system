package dev.boma.mtsms.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import dev.boma.mtsms.DatabaseEnabledTest
import net.datafaker.Faker
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TenantsControllerTests @Autowired constructor(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
) : DatabaseEnabledTest() {

    private val faker = Faker()

    @Test
    fun `should allow any authenticated user to create a tenant`() {
        val tenantName = faker.company().name()
        val payload = mapOf("name" to tenantName)

        mockMvc.post("/tenants") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(payload)

            with(jwt().jwt {
                it.claim("sub", "auth0|user-id")
            })
        }.andExpect {
            status { isCreated() }

            jsonPath("$.tenantId") {
                exists()
                // id should be UUID
                value(Matchers.matchesPattern("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"))
            }
            jsonPath("$.name") {
                value(tenantName)
            }
            jsonPath("$.tenantUsers[*].userSub") {
                value("auth0|user-id")
            }
        }
    }

    @Test
    fun `should return 400 for invalid request`() {
        val payload = mapOf("name" to "")

        mockMvc.post("/tenants") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(payload)

            with(jwt())
        }.andExpect {
            status { isBadRequest() }

            jsonPath("$.data.errors.name") {
                exists()
                value(Matchers.containsString("required"))
            }
        }
    }

    @Test
    fun `should return 401 for non-authenticated user`() {
        mockMvc.post("/tenants").andExpect {
            status { isUnauthorized() }
        }
    }
}