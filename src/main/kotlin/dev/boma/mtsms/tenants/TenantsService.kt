package dev.boma.mtsms.tenants

import dev.boma.mtsms.shared.getJwtFromContext
import dev.boma.mtsms.tenants.persistence.entities.Tenant
import dev.boma.mtsms.tenants.persistence.repositories.TenantsRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.Optional
import java.util.UUID

@Service
class TenantsService @Autowired internal constructor(
    private val tenantsRepository: TenantsRepository
) {

    fun getById(id: UUID): Optional<Tenant> {
        val jwt = getJwtFromContext()
        return tenantsRepository.getByIdWithAccessControl(id, jwt.subject)
    }

    fun create(tenant: Tenant): Tenant {
        return tenantsRepository.save(tenant)
    }
}