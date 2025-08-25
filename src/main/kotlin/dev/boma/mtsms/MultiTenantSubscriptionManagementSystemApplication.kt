package dev.boma.mtsms

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MultiTenantSubscriptionManagementSystemApplication

fun main(args: Array<String>) {
    runApplication<MultiTenantSubscriptionManagementSystemApplication>(*args)
}
