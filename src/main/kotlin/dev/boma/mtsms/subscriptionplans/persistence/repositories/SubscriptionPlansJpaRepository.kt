package dev.boma.mtsms.subscriptionplans.persistence.repositories

import dev.boma.mtsms.subscriptionplans.persistence.entities.SubscriptionPlan
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository

internal interface SubscriptionPlansJpaRepository : JpaRepository<SubscriptionPlan, UUID>
