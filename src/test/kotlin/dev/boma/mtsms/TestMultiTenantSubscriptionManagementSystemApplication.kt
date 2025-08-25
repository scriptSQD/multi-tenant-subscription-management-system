package dev.boma.mtsms

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
    fromApplication<MultiTenantSubscriptionManagementSystemApplication>().with(TestcontainersConfiguration::class)
        .run(*args)
}
