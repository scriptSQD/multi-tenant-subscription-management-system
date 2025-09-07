package dev.boma.mtsms.e2e

import dev.boma.mtsms.subscriptionplans.enums.BillingPeriodType
import dev.boma.mtsms.subscriptionplans.persistence.entities.SubscriptionPlan
import dev.boma.mtsms.subscriptionplans.persistence.repositories.SubscriptionPlansRepository
import dev.boma.mtsms.usage.enums.ServiceType
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SubscriptionPlansTests @Autowired constructor(private val mockMvc: MockMvc) : Base() {

	@Nested
	@DisplayName("Subscription plan retrieval")
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	inner class SubscriptionPlanRetrieval @Autowired constructor(
		private val subscriptionPlansRepository: SubscriptionPlansRepository,
	) {

		private lateinit var subscriptionPlans: MutableSet<SubscriptionPlan>

		@BeforeAll
		fun prepareTestSubscriptionPlans() {
			val plans = listOf(
				SubscriptionPlan().apply {
					name = "Free"
					billingPeriodType = BillingPeriodType.Monthly

					addPlanPrice {
						amount = 0.00.toBigDecimal()
						currency = "usd"
					}
					trialOffering = null

					addUsageLimit {
						serviceType = ServiceType.ApiCalls
						limit = 1000.toBigDecimal()
					}
					addUsageLimit {
						serviceType = ServiceType.TenantStorage
						limit = 1.toBigDecimal() /* GB */
					}

					addOverusePrice {
						serviceType = ServiceType.ApiCalls

						billedUsage = 1000.toBigDecimal() /* requests */

						amount = 0.25.toBigDecimal()
						currency = "usd"
					}
				},
				SubscriptionPlan().apply {
					name = "Starter"
					billingPeriodType = BillingPeriodType.Monthly

					addPlanPrice {
						amount = 10.00.toBigDecimal()
						currency = "usd"
					}
					addTrialOffering {
						available = true
						trialPeriod = 7 /* days */
					}

					addUsageLimit {
						serviceType = ServiceType.ApiCalls
						limit = 50_000.toBigDecimal()
					}
					addUsageLimit {
						serviceType = ServiceType.TenantStorage
						limit = 5.toBigDecimal() /* GB */
					}

					addOverusePrice {
						serviceType = ServiceType.ApiCalls

						billedUsage = 1000.toBigDecimal() /* requests */

						amount = 0.15.toBigDecimal()
						currency = "usd"
					}
				},
				SubscriptionPlan().apply {
					name = "Premium+"
					billingPeriodType = BillingPeriodType.Monthly

					addPlanPrice {
						amount = 50.00.toBigDecimal()
						currency = "usd"
					}
					addTrialOffering {
						available = true
						trialPeriod = 30 /* days */
					}

					// no limits implied
				},
			)

			subscriptionPlans = plans
				.map { subscriptionPlansRepository.create(it) }
				.toMutableSet()
		}

		@AfterAll
		fun cleanupSubscriptionPlans() {
			subscriptionPlansRepository.deleteByIds(subscriptionPlans.map { s -> s.id }.requireNoNulls())
		}

		@Nested
		@DisplayName("Retrieve all subscription plans")
		inner class RetrieveAllSubscriptionPlans {

			@Test
			@DisplayName("should return subscriptions without auth")
			fun withoutAuth() {
				mockMvc.get("/subscription-plans").andExpect {
					status { isOk() }
				}
			}

			@Test
			@DisplayName("should return subscriptions with price, trial offering and usage limits")
			fun getAllSubscriptions() {
				mockMvc.get("/subscription-plans").andExpect {
					status { isOk() }

					jsonPath("@") {
						exists()
						isArray()
					}

					jsonPath("$[*].name") { exists() }

					jsonPath("$[*].price") { exists() }
					jsonPath("$[*].trialOffering") { exists() }
					jsonPath("$[*].usageLimits") { exists() }

					jsonPath("$[*].overusePrices") { doesNotExist() }
				}
			}
		}

		@Nested
		@DisplayName("Subscription plan retrieval by id")
		inner class SubscriptionPlanRetrievalById {

			@Test
			@DisplayName("should return 404 for non-existing subscription plan")
			fun nonExistingSubscriptionPlan() {
				mockMvc.get("/subscription-plans/00000000-0000-0000-0000-000000000000").andExpect {
					status { isNotFound() }
				}
			}

			@Test
			@DisplayName("should return a subscription plan without auth")
			fun withoutAuth() {
				val plan = subscriptionPlans.random()
				mockMvc.get("/subscription-plans/${plan.id}").andExpect {
					status { isOk() }
				}
			}

			@Test
			@DisplayName("should return a subscription plan with price info, trial offering, usage limits and overuse price info")
			fun getSubscriptionWithExtendedInfo() {
				// Explicitly select the second plan here because it has a price, trial offering, usage limits and overuse prices
				val plan = subscriptionPlans.elementAt(1)

				mockMvc.get("/subscription-plans/${plan.id}").andExpect {
					status { isOk() }

					jsonPath("$.name") { value(plan.name) }

					jsonPath("$.price.amount") { value(plan.planPrice?.amount) }
					jsonPath("$.price.currency") { value(plan.planPrice?.currency) }

					jsonPath("$.trialOffering.available") { value(plan.trialOffering?.available) }
					jsonPath("$.trialOffering.trialPeriod") { value(plan.trialOffering?.trialPeriod) }

					jsonPath("$.usageLimits") { isMap() }

					jsonPath("$.overusePrices") { isMap() }
				}
			}
		}
	}
}