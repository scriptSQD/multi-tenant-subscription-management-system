package dev.boma.mtsms.core.serialization

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonView
import dev.boma.mtsms.core.serialization.views.Views
import io.swagger.v3.oas.annotations.media.Schema
import java.io.Serializable

@Schema(name = "ErrorResponse", description = "Generic API response in case of an error")
@JsonView(Views.Thin::class)
data class ErrorResponse(
	@field:JsonProperty val status: Number,
	@field:JsonProperty val message: String,
	@field:JsonProperty val data: Any? = null,
) : Serializable