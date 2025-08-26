package dev.boma.mtsms.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import dev.boma.mtsms.DatabaseEnabledTest
import dev.boma.mtsms.tenants.persistence.entities.Tenant
import net.datafaker.Faker
import org.hamcrest.Matchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
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

    @Nested
    @DisplayName("Tenant creation")
    inner class TenantCreation {

        @Test
        @DisplayName("should allow any authenticated user to create a tenant")
        fun createSuccessfully() {
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
        @DisplayName("should return 400 for invalid request")
        fun invalidPayload() {
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
        @DisplayName("should return 401 for non-authenticated user")
        fun unauthenticated() {
            mockMvc.post("/tenants").andExpect {
                status { isUnauthorized() }
            }
        }
    }

    @Nested
    @DisplayName("Get tenant by id")
    inner class GetTenantById {

        private lateinit var tenant: Tenant
        private val tenantOwnerSub = "auth0|user-sub"

        @BeforeEach
        fun prepareTestTenant() {
            mockMvc.post("/tenants") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(Tenant().apply {
                    name = faker.company().name()
                })

                with(jwt().jwt {
                    it.claim("sub", tenantOwnerSub)
                })
            }.andReturn().also {
                tenant = objectMapper.readValue(it.response.contentAsString, Tenant::class.java)
            }
        }

        @Test
        @DisplayName("should return tenant if user is related")
        fun relatedUser() {
            mockMvc.get("/tenants/${tenant.id}") {
                with(jwt().jwt {
                    it.claim("sub", tenantOwnerSub)
                })
            }.andExpect {
                status { isOk() }

                jsonPath("$.tenantId") { value(tenant.id.toString()) }
                jsonPath("$.name") { value(tenant.name) }
            }
        }

        @Test
        @DisplayName("should return 404 if user is not related")
        fun notRelatedUser() {
            mockMvc.get("/tenants/${tenant.id}") {
                with(jwt().jwt {
                    it.claim("sub", "$tenantOwnerSub-broken")
                })
            }.andExpect {
                status { isNotFound() }
            }
        }

        @Test
        @DisplayName("should return 404 if the tenant doesn't exist")
        fun nonExistentTenant() {
            mockMvc.get("/tenants/00000000-0000-0000-0000-000000000000") {
                with(jwt().jwt {
                    it.claim("sub", tenantOwnerSub)
                })
            }.andExpect {
                status { isNotFound() }
            }
        }
    }
}