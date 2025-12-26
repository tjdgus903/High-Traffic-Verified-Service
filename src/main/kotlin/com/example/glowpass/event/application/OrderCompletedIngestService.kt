package com.example.glowpass.event.application

import com.example.glowpass.event.api.OrderCompletedEventRequest
import com.fasterxml.jackson.databind.ObjectMapper
import com.example.glowpass.outbox.domain.OutboxEvent
import com.example.glowpass.outbox.domain.ProcessedEvent
import com.example.glowpass.outbox.infra.OutboxEventRepository
import com.example.glowpass.outbox.infra.ProcessedEventRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderCompletedIngestService(
    private val processedEventRepository: ProcessedEventRepository,
    private val outboxRepository: OutboxEventRepository,
    private val objectMapper: ObjectMapper,
) {

    @Transactional
    fun ingest(req: OrderCompletedEventRequest){
        try {
            processedEventRepository.save(ProcessedEvent(req.eventId))
        }catch (e: DataIntegrityViolationException){
            return
        }

        val payloadJson: String = objectMapper.writeValueAsString(req)

        outboxRepository.save(
            OutboxEvent(
                eventId = req.eventId,
                eventType = "ORDER_COMPLETED",
                payload = payloadJson,
            )
        )
    }
}