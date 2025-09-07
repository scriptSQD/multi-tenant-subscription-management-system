package dev.boma.mtsms.core.exceptions.business

class EntityNotFound(
	val entityName: String? = null,
	val criteria: String? = null,
	override val message: String = buildString {
		append(
			entityName
			?: "Entity",
		)

		criteria?.let {
			append(" with $criteria")
		}

		append(" not found")
	},
) : Exception(message)