package dev.boma.mtsms.tenants.persistence.entities

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonView
import dev.boma.mtsms.core.persistence.generators.GeneratedUUIDv7
import dev.boma.mtsms.core.serialization.views.Views
import dev.boma.mtsms.core.validation.OnCreate
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.proxy.HibernateProxy
import org.hibernate.type.SqlTypes
import org.hibernate.validator.constraints.Length
import java.util.UUID

@Entity
@Table(name = "tenants")
@JsonIgnoreProperties("tenantUsers", allowGetters = true)
class Tenant {

	@Id
	@GeneratedUUIDv7
	@Column(name = "id", nullable = false, updatable = false)
	@JsonView(Views.Thin::class)
	@JsonProperty("tenantId")
	var id: UUID? = null

	@JdbcTypeCode(SqlTypes.VARCHAR)
	@Column(name = "name", length = 1000, nullable = false)
	@JsonProperty
	@Length(message = "Tenant name shouldn't exceed 1000 characters", max = 1000)
	@NotBlank(message = "Tenant name is required and shouldn't be blank", groups = [OnCreate::class])
	@JsonView(Views.Thin::class)
	var name: String? = null

	@OneToMany(mappedBy = "tenant", cascade = [CascadeType.ALL], orphanRemoval = true)
	@JsonProperty
	@JsonView(Views.Extended::class)
	var tenantUsers: MutableSet<TenantUser> = mutableSetOf()

	fun addUserBySub(sub: String) {
		val user = TenantUser()
		user.userSub = sub
		user.tenant = this

		tenantUsers.add(user)
	}

	@Override
	override fun toString(): String {
		return this::class.simpleName + "( id = $id , name = $name )"
	}

	final override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other == null) return false
		val oEffectiveClass =
			if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
		val thisEffectiveClass =
			if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
		if (thisEffectiveClass != oEffectiveClass) return false
		other as Tenant

		return id != null && id?.equals(other.id) == true
	}

	final override fun hashCode(): Int =
		if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()
}