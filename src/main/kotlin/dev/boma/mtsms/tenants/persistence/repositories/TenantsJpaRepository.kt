package dev.boma.mtsms.tenants.persistence.repositories

import dev.boma.mtsms.tenants.persistence.entities.Tenant
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

internal interface TenantsJpaRepository : JpaRepository<Tenant, UUID>