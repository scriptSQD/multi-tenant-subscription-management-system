package dev.boma.mtsms.shared

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.io.Serializable

class HttpStatusException(
    val status: HttpStatus,
    val errorMessage: String? = null,
    override val cause: Throwable? = null,
    val data: Serializable? = null,
) : ResponseStatusException(status, errorMessage, cause)
