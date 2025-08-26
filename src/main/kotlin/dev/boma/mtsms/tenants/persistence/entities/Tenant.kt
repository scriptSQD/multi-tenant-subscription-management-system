package dev.boma.mtsms.tenants.persistence.entities

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import dev.boma.mtsms.persistence.base.UUIDAsPkEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import org.hibernate.validator.constraints.Length
import java.util.UUID

@Entity
@Table(name = "tenants")
@JsonIgnoreProperties("tenantUsers", allowGetters = true)
class Tenant : UUIDAsPkEntity() {
    override var id: UUID? = null
        @JsonProperty("tenantId")
        get

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "name", length = 1000, nullable = false)
    @JsonProperty
    @Length(message = "Tenant name shouldn't exceed 1000 characters", max = 1000)
    @NotBlank(message = "Tenant name is required and shouldn't be blank")
    var name: String? = null

    @OneToMany(mappedBy = "tenant", cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonProperty
    var tenantUsers: MutableSet<TenantUser> = mutableSetOf()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(  id = $id   ,   name = $name )"
    }
}