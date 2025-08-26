package dev.boma.mtsms.persistence.generators

import com.github.f4b6a3.uuid.UuidCreator
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.id.IdentifierGenerator
import java.util.UUID


class UUIDv7Generator : IdentifierGenerator {
    override fun generate(session: SharedSessionContractImplementor, obj: Any): UUID {
        return UuidCreator.getTimeOrderedEpoch()
    }
}