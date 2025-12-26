package com.example.glowpass.outbox.domain

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = false)
class JsonNodeConverter(
    private val objectMapper: ObjectMapper = ObjectMapper()
) : AttributeConverter<JsonNode, String> {

    override fun convertToDatabaseColumn(attribute: JsonNode?): String {
        return objectMapper.writeValueAsString(attribute)
    }

    override fun convertToEntityAttribute(dbData: String?): JsonNode {
        return objectMapper.readTree(dbData)
    }
}