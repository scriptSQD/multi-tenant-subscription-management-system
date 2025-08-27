package dev.boma.mtsms.tenants

import com.fasterxml.jackson.annotation.JsonView
import dev.boma.mtsms.serialization.views.Views
import dev.boma.mtsms.tenants.persistence.entities.Tenant
import dev.boma.mtsms.validation.OnCreate
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
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
class TenantsController @Autowired constructor(val tenantsService: TenantsService) {


    @GetMapping
    @JsonView(Views.Thin::class)
    fun getAllTenants(): Set<Tenant> {
        return tenantsService.getAll()
    }

    @GetMapping("/{id}")
    @JsonView(Views.Extended::class)
    fun getTenantById(@Validated @PathVariable id: UUID): Tenant {
        return tenantsService.getById(id)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @JsonView(Views.Thin::class)
    fun createTenant(@Validated(OnCreate::class) @RequestBody body: Tenant): Tenant {
        return tenantsService.create(body)
    }

    @PatchMapping("/{id}")
    @JsonView(Views.Thin::class)
    fun updateTenant(@Validated @PathVariable id: UUID, @Validated @RequestBody body: Tenant): Tenant {
        return tenantsService.update(id, body)
    }
}