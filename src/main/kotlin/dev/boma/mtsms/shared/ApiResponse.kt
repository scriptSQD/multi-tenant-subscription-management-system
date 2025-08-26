package dev.boma.mtsms.shared

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

data class ApiResponse(
    @field:JsonProperty val status: Number,
    @field:JsonProperty val message: String,
    @field:JsonProperty val data: Any? = null,
) : Serializable
