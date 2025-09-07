package dev.boma.mtsms.subscriptionplans.persistence.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonView
import dev.boma.mtsms.core.persistence.generators.GeneratedUUIDv7
import dev.boma.mtsms.core.serialization.views.Views
import dev.boma.mtsms.subscriptionplans.enums.BillingPeriodType
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.util.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.proxy.HibernateProxy
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "subscription_plans")
class SubscriptionPlan {

	@Id
	@GeneratedUUIDv7
	@Column(name = "id", nullable = false)
	@JsonProperty("planId")
	@JsonView(Views.Thin::class)
	var id: UUID? = null

	@JdbcTypeCode(SqlTypes.VARCHAR)
	@Column(name = "name", nullable = false, length = 1000)
	@JsonProperty
	@JsonView(Views.Thin::class)
	lateinit var name: String

	@Enumerated(EnumType.STRING)
	@Column(name = "billing_period_type", nullable = false, length = 255)
	@JsonProperty
	@JsonView(Views.Thin::class)
	lateinit var billingPeriodType: BillingPeriodType

	@OneToOne(mappedBy = "subscriptionPlan", cascade = [CascadeType.ALL], orphanRemoval = true)
	@JsonProperty("price")
	@JsonView(Views.Thin::class)
	var planPrice: PlanPrice? = null

	@OneToOne(mappedBy = "subscriptionPlan", cascade = [CascadeType.ALL], orphanRemoval = true)
	@JsonProperty
	@JsonView(Views.Thin::class)
	var trialOffering: TrialOffering? = null

	@OneToMany(mappedBy = "subscriptionPlan", cascade = [CascadeType.ALL], orphanRemoval = true)
	@JsonIgnore
	var planServiceUsageLimits: MutableSet<PlanServiceUsageLimit> = mutableSetOf()

	@OneToMany(mappedBy = "subscriptionPlan", cascade = [CascadeType.ALL], orphanRemoval = true)
	@JsonIgnore
	var planServiceOverusePrices: MutableSet<PlanServiceOverusePrice> = mutableSetOf()

	@JsonProperty("usageLimits")
	@JsonView(Views.Thin::class)
	fun getSerializedUsageLimits() = planServiceUsageLimits.associateBy { it.serviceType }

	@JsonProperty("overusePrices")
	@JsonView(Views.Extended::class)
	fun getSerializedOverusePrices() = planServiceOverusePrices.associateBy { it.serviceType }

	fun addPlanPrice(builder: PlanPrice.() -> Unit) {
		val price = PlanPrice().apply(builder)

		this.planPrice = price
		price.subscriptionPlan = this
	}

	fun addTrialOffering(builder: TrialOffering.() -> Unit) {
		val trial = TrialOffering().apply(builder)

		this.trialOffering = trial
		trial.subscriptionPlan = this
	}

	fun addUsageLimit(builder: PlanServiceUsageLimit.() -> Unit) {
		val limit = PlanServiceUsageLimit().apply(builder)

		this.planServiceUsageLimits.add(limit)
		limit.subscriptionPlan = this
	}

	fun addOverusePrice(builder: PlanServiceOverusePrice.() -> Unit) {
		val price = PlanServiceOverusePrice().apply(builder)

		this.planServiceOverusePrices.add(price)
		price.subscriptionPlan = this
	}

	final override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other == null) return false
		val oEffectiveClass = if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
		val thisEffectiveClass = if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
		if (thisEffectiveClass != oEffectiveClass) return false
		other as SubscriptionPlan

		return id != null && id == other.id
	}

	final override fun hashCode(): Int = if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()
}