package dev.boma.mtsms.subscriptionplans.persistence.repositories

import dev.boma.mtsms.subscriptionplans.persistence.entities.SubscriptionPlan
import java.util.Optional
import java.util.UUID
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class SubscriptionPlansRepository internal constructor(
	private val repository: SubscriptionPlansJpaRepository,
) {

	@Transactional(readOnly = true)
	fun getAll(): Set<SubscriptionPlan> {
		return repository.findAll().toSet()
	}

	@Transactional(readOnly = true)
	fun getById(id: UUID): Optional<SubscriptionPlan> {
		return repository.findById(id)
	}

	@Transactional
	fun create(subscriptionPlan: SubscriptionPlan): SubscriptionPlan {
		return repository.save(subscriptionPlan)
	}

	@Transactional
	fun deleteByIds(ids: List<UUID>) {
		repository.deleteAllById(ids)
	}
}