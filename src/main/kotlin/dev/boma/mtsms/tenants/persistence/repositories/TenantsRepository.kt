package dev.boma.mtsms.tenants.persistence.repositories

import dev.boma.mtsms.tenants.persistence.entities.Tenant
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.Optional
import java.util.UUID

@Repository
class TenantsRepository internal constructor(
	private val repository: TenantsJpaRepository,
) {

	@Transactional(readOnly = true)
	fun getAllWithAccessControl(userSub: String): Set<Tenant> {
		return repository.findAllByRelatedUser(userSub)
	}

	@Transactional(readOnly = true)
	fun getByIdWithAccessControl(id: UUID, userSub: String): Optional<Tenant> {
		return repository.findByIdAndRelatedUser(id, userSub)
	}

	@Transactional
	fun save(tenant: Tenant): Tenant {
		return repository.saveAndFlush(tenant)
	}

	@Transactional
	fun update(tenant: Tenant, update: Tenant): Tenant {
		update.name?.let { tenant.name = it }

		return repository.saveAndFlush(tenant)
	}

	@Transactional
	fun deleteByIds(ids: List<UUID>) {
		repository.deleteAllById(ids)
	}
}