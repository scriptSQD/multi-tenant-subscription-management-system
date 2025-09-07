package dev.boma.mtsms.core.persistence.generators

import com.github.f4b6a3.uuid.UuidCreator
import org.hibernate.annotations.IdGeneratorType
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.generator.BeforeExecutionGenerator
import org.hibernate.generator.EventType
import org.hibernate.generator.EventTypeSets
import java.util.EnumSet
import java.util.UUID

private class UUIDv7Generator : BeforeExecutionGenerator {

	override fun generate(
		session: SharedSessionContractImplementor?,
		owner: Any?,
		currentValue: Any?,
		eventType: EventType?,
	): UUID {
		return UuidCreator.getTimeOrderedEpoch()
	}

	override fun getEventTypes(): EnumSet<EventType?> = EventTypeSets.INSERT_ONLY
}

@IdGeneratorType(UUIDv7Generator::class)
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class GeneratedUUIDv7
