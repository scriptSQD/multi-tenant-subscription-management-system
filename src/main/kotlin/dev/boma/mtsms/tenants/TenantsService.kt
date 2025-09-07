package dev.boma.mtsms.tenants

import dev.boma.mtsms.core.exceptions.business.EntityNotFound
import dev.boma.mtsms.core.utils.getJwtFromContext
import dev.boma.mtsms.tenants.persistence.entities.Tenant
import dev.boma.mtsms.tenants.persistence.repositories.TenantsRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class TenantsService(private val tenantsRepository: TenantsRepository) {

	fun getAll(): Set<Tenant> {
		val jwt = getJwtFromContext()
		return tenantsRepository.getAllWithAccessControl(jwt.subject)
	}

	fun getById(id: UUID): Tenant {
		val jwt = getJwtFromContext()
		return tenantsRepository.getByIdWithAccessControl(id, jwt.subject).orElseThrow {
			EntityNotFound(message = "Tenant with id $id doesn't exist or you lack permissions to access it")
		}
	}

	fun create(tenant: Tenant): Tenant {
		val jwt = getJwtFromContext()

		tenant.addUserBySub(jwt.subject)
		return tenantsRepository.save(tenant)
	}

	fun update(id: UUID, update: Tenant): Tenant {
		val tenant = getById(id)
		return tenantsRepository.update(tenant, update)
	}
}