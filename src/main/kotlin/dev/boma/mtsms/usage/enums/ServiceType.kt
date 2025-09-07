package dev.boma.mtsms.usage.enums

import com.fasterxml.jackson.annotation.JsonProperty

enum class ServiceType(val unit: String) {

	@JsonProperty("apiCalls")
	ApiCalls(unit = "request"),
	@JsonProperty("tenantStorage")
	TenantStorage(unit = "GB"),
	@JsonProperty("userStorage")
	UserStorage(unit = "MB"),
}