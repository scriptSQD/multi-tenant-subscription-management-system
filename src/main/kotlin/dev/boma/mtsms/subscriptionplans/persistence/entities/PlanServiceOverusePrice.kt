package dev.boma.mtsms.subscriptionplans.persistence.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonView
import dev.boma.mtsms.core.serialization.views.Views
import dev.boma.mtsms.usage.enums.ServiceType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import jakarta.persistence.Table
import java.io.Serializable
import java.math.BigDecimal
import java.util.Objects
import java.util.UUID
import org.hibernate.annotations.Comment
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.proxy.HibernateProxy
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "plan_service_overuse_prices")
@IdClass(PlanServiceOverusePrice.PrimaryKey::class)
class PlanServiceOverusePrice {

	@Id
	@Column(name = "subscription_plan_id", nullable = false)
	@JsonIgnore
	var subscriptionPlanId: UUID? = null

	@Id
	@Enumerated(EnumType.STRING)
	@Column(name = "service_type", nullable = false, length = 255)
	@JsonIgnore
	var serviceType: ServiceType? = null

	@JdbcTypeCode(SqlTypes.DECIMAL)
	@Column(name = "billed_usage", nullable = false, precision = 1000, scale = 3)
	@JsonProperty
	@JsonView(Views.Thin::class)
	lateinit var billedUsage: BigDecimal

	@JdbcTypeCode(SqlTypes.DECIMAL)
	@Column(name = "amount", nullable = false, precision = 1000, scale = 2)
	@JsonProperty
	@JsonView(Views.Thin::class)
	lateinit var amount: BigDecimal

	@JdbcTypeCode(SqlTypes.VARCHAR)
	@Column(name = "currency", nullable = false, length = 3)
	@Comment("ISO 4217 currency code")
	@JsonProperty
	@JsonView(Views.Thin::class)
	lateinit var currency: String

	@MapsId("subscriptionPlanId")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "subscription_plan_id", nullable = false/*, insertable = false, updatable = false*/)
	@JsonIgnore
	lateinit var subscriptionPlan: SubscriptionPlan

	@JsonProperty("unit")
	@JsonView(Views.Thin::class)
	fun serviceTypeUnit(): String {
		return serviceType?.unit
		       ?: ""
	}

	data class PrimaryKey(
		var subscriptionPlanId: UUID? = null,
		var serviceType: ServiceType? = null,
	) : Serializable

	final override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other == null) return false
		val oEffectiveClass = if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
		val thisEffectiveClass = if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
		if (thisEffectiveClass != oEffectiveClass) return false
		other as PlanServiceOverusePrice

		return subscriptionPlanId != null
		       && subscriptionPlanId == other.subscriptionPlanId
		       && serviceType != null
		       && serviceType == other.serviceType
	}

	final override fun hashCode(): Int = Objects.hash(subscriptionPlanId, serviceType)
}
