package dev.boma.mtsms.tenants.persistence.repositories

import dev.boma.mtsms.tenants.persistence.entities.Tenant
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

internal interface TenantsJpaRepository : JpaRepository<Tenant, UUID> {

    @Query(
        """
        SELECT t, tu
        FROM Tenant t
        INNER JOIN TenantUser tu ON tu.tenantId = t.id
        WHERE t.id = ?1 AND tu.userSub = ?2
        """
    )
    fun findByIdAndRelatedUser(id: UUID, relatedUserSub: String): Optional<Tenant>

    @Query(
        """
        SELECT t
        FROM Tenant t
        INNER JOIN TenantUser tu ON tu.tenantId = t.id
        WHERE tu.userSub = ?1
        """
    )
    fun findAllByRelatedUser(relatedUserSub: String): Set<Tenant>
}