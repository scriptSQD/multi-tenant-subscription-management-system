package dev.boma.mtsms.tenants.persistence.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonView
import dev.boma.mtsms.serialization.views.Views
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.io.Serializable
import java.util.*

@Entity
@Table(name = "tenant_users")
@IdClass(TenantUser.PrimaryKey::class)
class TenantUser {

    @Id
    @Column(name = "tenant_id", nullable = false)
    @JsonIgnore
    var tenantId: UUID? = null

    @Id
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "user_sub", nullable = false)
    @JsonProperty
    @JsonView(Views.Thin::class)
    var userSub: String? = null

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(
        name = "tenant_id",
        insertable = false,
        updatable = false,
        foreignKey = ForeignKey(name = "fk_tenant_users__tenant_id")
    )
    @JsonIgnore
    var tenant: Tenant? = null

    @Override
    override fun toString(): String {
        return this::class.simpleName + "( tenantId = $tenantId, userSub = $userSub )"
    }

    data class PrimaryKey(
        var tenantId: UUID? = null,
        var userSub: String? = null,
    ) : Serializable

    companion object {
        fun create(tenantId: UUID, userSub: String): TenantUser = TenantUser().apply {
            this.tenantId = tenantId
            this.userSub = userSub
        }
    }
}