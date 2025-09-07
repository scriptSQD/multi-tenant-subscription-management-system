package dev.boma.mtsms.subscriptionplans

import dev.boma.mtsms.core.exceptions.business.EntityNotFound
import dev.boma.mtsms.subscriptionplans.persistence.entities.SubscriptionPlan
import dev.boma.mtsms.subscriptionplans.persistence.repositories.SubscriptionPlansRepository
import java.util.UUID
import org.springframework.stereotype.Service

@Service
class SubscriptionPlansService(private val repository: SubscriptionPlansRepository) {

	fun getAll(): Set<SubscriptionPlan> {
		return repository.getAll()
	}

	fun getById(id: UUID): SubscriptionPlan {
		return repository.getById(id).orElseThrow {
			EntityNotFound(message = "Subscription Plan with id $id doesn't exist")
		}
	}
}