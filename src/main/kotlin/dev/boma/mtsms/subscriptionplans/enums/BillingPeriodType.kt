package dev.boma.mtsms.subscriptionplans.enums

import com.fasterxml.jackson.annotation.JsonProperty

enum class BillingPeriodType {

	@JsonProperty("monthly")
	Monthly,
	@JsonProperty("yearly")
	Yearly,
}