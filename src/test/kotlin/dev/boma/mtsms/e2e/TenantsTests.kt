package dev.boma.mtsms.e2e

import com.fasterxml.jackson.databind.ObjectMapper
import dev.boma.mtsms.e2e.factories.JwtSecurityContextFactory
import dev.boma.mtsms.tenants.persistence.entities.Tenant
import dev.boma.mtsms.tenants.persistence.repositories.TenantsRepository
import java.util.UUID
import net.datafaker.Faker
import org.hamcrest.Matchers
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TenantsTests @Autowired constructor(
	private val mockMvc: MockMvc,
	private val objectMapper: ObjectMapper,
	private val tenantsRepository: TenantsRepository,
) : Base() {

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

				with(
					jwt().jwt {
						it.claim("sub", "auth0|user-id")
						it.claim("scope", "write:tenants")
					},
				)
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
			}
		}

		@Test
		@DisplayName("should return 400 for invalid request")
		fun invalidPayload() {
			val payload = mapOf("name" to "")

			mockMvc.post("/tenants") {
				contentType = MediaType.APPLICATION_JSON
				content = objectMapper.writeValueAsString(payload)

				with(jwt().jwt { it.claim("scope", "write:tenants") })
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
	@DisplayName("Tenant retrieval")
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	inner class TenantRetrieval {

		private lateinit var tenants: MutableList<Pair<String, Tenant>>

		@BeforeAll
		fun prepareTestTenants() {
			val tenantOwners = listOf(
				"auth0|user-sub-1",
				"auth0|user-sub-2",
				"auth0|user-sub-3",
				"auth0|user-sub-4",
			)

			val tenantOwnerToTenantNameMap = tenantOwners.shuffled().associateWith { faker.company().name() }
			tenants = tenantOwnerToTenantNameMap.map { (tenantOwner, tenantName) ->
				val tenant = Tenant().apply {
					name = tenantName
					addUserBySub(tenantOwner)
				}

				SecurityContextHolder.setContext(JwtSecurityContextFactory.create(tenantOwner))

				tenantOwner to tenantsRepository.save(tenant)
			}.toMutableList()
		}

		@AfterAll
		fun cleanupTestTenants() {
			tenantsRepository.deleteByIds(tenants.map { t -> t.second.id }.requireNoNulls())
		}

		@Nested
		@DisplayName("Retrieve all")
		inner class RetrieveAll {

			private lateinit var userSub: String

			@BeforeEach
			fun getRandomTestUser() {
				this.userSub = tenants.random().first
			}

			@Test
			@DisplayName("should return all related tenants")
			fun allRelatedTenants() {
				mockMvc.get("/tenants") {
					with(
						jwt().jwt {
							it.claim("sub", userSub)
							it.claim("scope", "read:tenants")
						},
					)
				}.andExpect {
					status { isOk() }

					jsonPath("@") {
						exists()
						isArray()
					}

					jsonPath("$[*].tenantId") {
						exists()

						val userRelatedTenantIds = tenants.filter { t -> t.first == userSub }.map { t -> t.second.id.toString() }
						value(Matchers.hasItems(*userRelatedTenantIds.toTypedArray()))

						val userUnrelatedTenantIds = tenants.filter { t -> t.first != userSub }.map { t -> t.second.id.toString() }
						value(Matchers.not(Matchers.hasItem(Matchers.`is`(Matchers.`in`(userUnrelatedTenantIds)))))
					}
				}
			}
		}

		@Nested
		@DisplayName("Retrieve by id")
		inner class RetrievalById {

			private lateinit var tenant: Tenant
			private lateinit var tenantOwnerSub: String

			@BeforeEach
			fun getRandomTestTenant() {
				val (tenantOwner, tenant) = tenants.random()
				this.tenant = tenant
				this.tenantOwnerSub = tenantOwner
			}

			@Test
			@DisplayName("should return 400 bad request for invalid uuid in path")
			fun invalidUuid() {
				mockMvc.get("/tenants/invalid-uuid") {
					with(
						jwt().jwt {
							it.claim("sub", tenantOwnerSub)
							it.claim("scope", "read:tenants")
						},
					)
				}.andExpect {
					status { isBadRequest() }
				}
			}

			@Test
			@DisplayName("should return tenant if user is related")
			fun relatedUser() {
				mockMvc.get("/tenants/${tenant.id}") {
					with(
						jwt().jwt {
							it.claim("sub", tenantOwnerSub)
							it.claim("scope", "read:tenants")
						},
					)
				}.andExpect {
					status { isOk() }

					jsonPath("$.tenantId") { value(tenant.id.toString()) }
					jsonPath("$.name") { value(tenant.name) }
					jsonPath("$.tenantUsers") {
						exists()
						isArray()
					}
					jsonPath("$.tenantUsers[*].userSub") {
						value(tenantOwnerSub)
					}
				}
			}

			@Test
			@DisplayName("should return 404 if user is not related")
			fun notRelatedUser() {
				mockMvc.get("/tenants/${tenant.id}") {
					with(
						jwt().jwt {
							it.claim("sub", "$tenantOwnerSub-broken")
							it.claim("scope", "read:tenants")
						},
					)
				}.andExpect {
					status { isNotFound() }
				}
			}

			@Test
			@DisplayName("should return 404 if the tenant doesn't exist")
			fun nonExistentTenant() {
				mockMvc.get("/tenants/00000000-0000-0000-0000-000000000000") {
					with(
						jwt().jwt {
							it.claim("sub", tenantOwnerSub)
							it.claim("scope", "read:tenants")
						},
					)
				}.andExpect {
					status { isNotFound() }
				}
			}
		}
	}

	@Nested
	@DisplayName("Tenant modification")
	inner class TenantModification {

		private lateinit var tenant: Tenant
		private val tenantOwnerSub: String = "auth0|tenant-owner-sub"

		@BeforeEach
		fun prepareTestTenant() {
			tenant = tenantsRepository.save(
				Tenant().apply {
					name = "Name before modification"
					addUserBySub(tenantOwnerSub)
				},
			)
		}

		@AfterEach
		fun cleanupTestTenant() {
			tenantsRepository.deleteByIds(listOf(tenant.id).requireNoNulls())
		}

		@Test
		@DisplayName("should throw 404 if user is not related")
		fun notRelatedUser() {
			mockMvc.patch("/tenants/${tenant.id}") {
				contentType = MediaType.APPLICATION_JSON
				content =
					objectMapper.writeValueAsString(
						Tenant().apply {
							name = faker.company().name()
						},
					)

				with(
					jwt().jwt {
						it.claim("sub", "$tenantOwnerSub-broken")
						it.claim("scope", "write:tenants")
					},
				)
			}.andExpect {
				status { isNotFound() }
			}
		}

		@Test
		@DisplayName("should throw 404 if tenant not found")
		fun nonExistentTenant() {
			mockMvc.patch("/tenants/00000000-0000-0000-0000-000000000000") {
				contentType = MediaType.APPLICATION_JSON
				content =
					objectMapper.writeValueAsString(
						Tenant().apply {
							name = faker.company().name()
						},
					)

				with(
					jwt().jwt {
						it.claim("sub", tenantOwnerSub)
						it.claim("scope", "write:tenants")
					},
				)
			}.andExpect {
				status { isNotFound() }
			}
		}

		@Test
		@DisplayName("should allow empty updates and return unmodified tenant")
		fun emptyUpdate() {
			mockMvc.patch("/tenants/${tenant.id}") {
				contentType = MediaType.APPLICATION_JSON
				content = objectMapper.writeValueAsString(emptyMap<String, Any>())

				with(
					jwt().jwt {
						it.claim("sub", tenantOwnerSub)
						it.claim("scope", "write:tenants")
					},
				)
			}.andExpect {
				status { isOk() }

				jsonPath("$.name") { value(tenant.name) }
			}
		}

		@Test
		@DisplayName("should return modified tenant if update contains changes")
		fun successfulUpdate() {
			mockMvc.patch("/tenants/${tenant.id}") {
				contentType = MediaType.APPLICATION_JSON
				content =
					objectMapper.writeValueAsString(
						Tenant().apply {
							name = "Modified name"
						},
					)

				with(
					jwt().jwt {
						it.claim("sub", tenantOwnerSub)
						it.claim("scope", "write:tenants")
					},
				)
			}.andExpect {
				status { isOk() }

				jsonPath("$.name") { value("Modified name") }
			}
		}

		@Test
		@DisplayName("should not modify tenantId even if update contains it")
		fun immutableTenantId() {
			mockMvc.patch("/tenants/${tenant.id}") {
				contentType = MediaType.APPLICATION_JSON
				content = objectMapper.writeValueAsString(
					Tenant().apply {
						id = UUID.fromString("00000000-0000-0000-0000-000000000000")
					},
				)

				with(
					jwt().jwt {
						it.claim("sub", tenantOwnerSub)
						it.claim("scope", "write:tenants")
					},
				)
			}.andExpect {
				status { isOk() }

				jsonPath("$.tenantId") { value(tenant.id.toString()) }
			}
		}
	}
}