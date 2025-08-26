package dev.boma.mtsms.tenants

import dev.boma.mtsms.shared.HttpStatusException
import dev.boma.mtsms.tenants.persistence.entities.Tenant
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RequestMapping("/tenants")
@RestController
class TenantsController @Autowired constructor(val tenantsService: TenantsService) {


    @GetMapping("/{id}")
    fun getTenantById(@PathVariable id: UUID): Tenant {
        return tenantsService.getById(id).orElseThrow {
            HttpStatusException(
                HttpStatus.NOT_FOUND,
                "Tenant with id $id doesn't exist or you lack permissions to access it"
            )
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createTenant(@Valid @RequestBody body: Tenant): Tenant {
        return tenantsService.create(body)
    }
}