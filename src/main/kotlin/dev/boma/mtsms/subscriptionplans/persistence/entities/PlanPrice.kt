package dev.boma.mtsms.subscriptionplans.persistence.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonView
import dev.boma.mtsms.core.serialization.views.Views
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.MapsId
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.math.BigDecimal
import java.util.UUID
import org.hibernate.annotations.Comment
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "plan_prices")
class PlanPrice {

	@Id
	@Column(name = "subscription_plan_id", nullable = false)
	@JsonIgnore
	lateinit var subscriptionPlanId: UUID

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

	@MapsId
	@OneToOne(fetch = FetchType.LAZY, optional = false, orphanRemoval = true)
	@JoinColumn(name = "subscription_plan_id", nullable = false, unique = true)
	@JsonIgnore
	lateinit var subscriptionPlan: SubscriptionPlan
}