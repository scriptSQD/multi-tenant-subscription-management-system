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
import java.util.UUID
import org.hibernate.annotations.Check
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "trial_offerings")
class TrialOffering {

	@Id
	@Column(name = "subscription_plan_id", nullable = false)
	@JsonIgnore
	lateinit var subscriptionPlanId: UUID

	@JdbcTypeCode(SqlTypes.BOOLEAN)
	@Column(name = "available", nullable = false)
	@JsonProperty
	@JsonView(Views.Thin::class)
	var available: Boolean = false

	@JdbcTypeCode(SqlTypes.INTEGER)
	@Column(name = "trial_period", nullable = true)
	@Check(name = "ck_trial_period_specified_if_available", constraints = "available = true AND trial_period IS NOT NULL")
	@JsonProperty
	@JsonView(Views.Thin::class)
	var trialPeriod: Int? = null

	@MapsId
	@OneToOne(fetch = FetchType.LAZY, optional = false, orphanRemoval = true)
	@JoinColumn(name = "subscription_plan_id", nullable = false, unique = true)
	@JsonIgnore
	lateinit var subscriptionPlan: SubscriptionPlan
}