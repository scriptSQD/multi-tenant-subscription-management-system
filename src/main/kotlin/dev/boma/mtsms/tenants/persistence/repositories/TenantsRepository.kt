package dev.boma.mtsms.tenants.persistence.repositories

import dev.boma.mtsms.tenants.persistence.entities.Tenant
import dev.boma.mtsms.tenants.persistence.entities.TenantUser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.Optional
import java.util.UUID

@Repository
internal class TenantsRepository @Autowired constructor(
    private val repository: TenantsJpaRepository
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
        val auth = SecurityContextHolder.getContext().authentication
            ?: throw IllegalStateException("Context is not authenticated")

        val jwt = when (auth.principal) {
            is Jwt -> auth.principal as Jwt
            else -> throw IllegalStateException("Context is authenticated with wrong principal type")
        }

        repository.save(tenant)

        tenant.tenantUsers.add(
            TenantUser.create(
                tenantId = tenant.id!!,
                userSub = jwt.subject
            )
        )

        return repository.saveAndFlush(tenant)
    }

    @Transactional
    fun update(tenant: Tenant, update: Tenant): Tenant {
        update.name?.let { tenant.name = it }

        return repository.saveAndFlush(tenant)
    }
}