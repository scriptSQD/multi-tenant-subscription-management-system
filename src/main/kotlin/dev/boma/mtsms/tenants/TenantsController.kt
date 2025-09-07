package dev.boma.mtsms.tenants

import com.fasterxml.jackson.annotation.JsonView
import dev.boma.mtsms.core.openapi.annotations.ApiCreatedResponse
import dev.boma.mtsms.core.openapi.annotations.ApiNotFoundResponse
import dev.boma.mtsms.core.openapi.annotations.ApiOkResponse
import dev.boma.mtsms.core.openapi.annotations.ApiProtectedEndpoint
import dev.boma.mtsms.core.openapi.annotations.meta.ApiBadRequestValidationResponse
import dev.boma.mtsms.core.serialization.views.Views
import dev.boma.mtsms.tenants.persistence.entities.Tenant
import dev.boma.mtsms.core.validation.OnCreate
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RequestMapping("/tenants")
@RestController
@Tag(name = "Tenants", description = "API for Tenant Management")
@ApiProtectedEndpoint(scopes = ["read:tenants", "write:tenants"])
class TenantsController(private val tenantsService: TenantsService) {

	@GetMapping
	@JsonView(Views.Thin::class)
	@ApiOkResponse(description = "All Tenants available to the authenticated user")
	fun getAllTenants(): Set<Tenant> {
		return tenantsService.getAll()
	}

	@GetMapping("/{id}")
	@JsonView(Views.Extended::class)
	@ApiOkResponse(description = "Tenant found")
	@ApiNotFoundResponse(description = "Tenant with the provided id does not exist")
	fun getTenantById(@Validated @PathVariable id: UUID): Tenant {
		return tenantsService.getById(id)
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@JsonView(Views.Thin::class)
	@ApiCreatedResponse(description = "New Tenant created")
	@ApiBadRequestValidationResponse(description = "Creation request data is malformed")
	fun createTenant(@Validated(OnCreate::class) @RequestBody body: Tenant): Tenant {
		return tenantsService.create(body)
	}

	@PatchMapping("/{id}")
	@JsonView(Views.Thin::class)
	fun updateTenant(@Validated @PathVariable id: UUID, @Validated @RequestBody body: Tenant): Tenant {
		return tenantsService.update(id, body)
	}
}