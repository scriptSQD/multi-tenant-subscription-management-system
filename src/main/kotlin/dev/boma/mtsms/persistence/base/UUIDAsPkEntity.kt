package dev.boma.mtsms.persistence.base

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Column
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import org.hibernate.annotations.GenericGenerator
import java.util.UUID

@MappedSuperclass
abstract class UUIDAsPkEntity {

    @Id
    @GeneratedValue(generator = "uuid-v7-generator")
    @GenericGenerator(name = "uuid-v7-generator", strategy = "dev.boma.mtsms.persistence.generators.UUIDv7Generator")
    @Column(name = "id", nullable = false, updatable = false)
    @JsonIgnore
    var id: UUID? = null
}