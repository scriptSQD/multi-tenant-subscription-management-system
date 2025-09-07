package dev.boma.mtsms.subscriptionplans

import com.fasterxml.jackson.annotation.JsonView
import dev.boma.mtsms.core.serialization.views.Views
import dev.boma.mtsms.core.openapi.annotations.ApiNotFoundResponse
import dev.boma.mtsms.core.openapi.annotations.ApiOkResponse
import dev.boma.mtsms.subscriptionplans.persistence.entities.SubscriptionPlan
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import java.util.UUID
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/subscription-plans")
@RestController
@Tag(name = "Subscription Plans", description = "API for fetching Subscription Plans")
class SubscriptionPlansController(private val subscriptionPlansService: SubscriptionPlansService) {

	@GetMapping
	@JsonView(Views.Thin::class)
	@ApiOkResponse(description = "All available Subscription Plans")
	fun getAll(): Set<SubscriptionPlan> {
		return subscriptionPlansService.getAll()
	}

	@GetMapping("/{id}")
	@JsonView(Views.Extended::class)
	@ApiOkResponse(description = "Subscription Plan found")
	@ApiNotFoundResponse(description = "Subscription Plan with the provided id does not exist")
	fun getById(@Valid @PathVariable id: UUID): SubscriptionPlan {
		return subscriptionPlansService.getById(id)
	}
}