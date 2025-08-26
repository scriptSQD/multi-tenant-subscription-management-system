package dev.boma.mtsms

import org.springframework.boot.fromApplication


fun main(args: Array<String>) {
    fromApplication<MultiTenantSubscriptionManagementSystemApplication>()
        .run(*args)
}
