package org.example.com.example.glowpass.event.application

import com.fasterxml.jackson.databind.ObjectMapper
import org.example.com.example.glowpass.event.api.OrderCompletedEventRequest
import org.example.com.example.glowpass.outbox.domain.OutboxEvent
import org.example.com.example.glowpass.outbox.domain.ProcessedEvent
import org.example.com.example.glowpass.outbox.infra.OutboxEventRepository
import org.example.com.example.glowpass.outbox.infra.ProcessedEventRepository
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

        val payloadJson = objectMapper.writeValueAsString(req)

        outboxRepository.save(
            OutboxEvent(
                eventId = req.eventId,
                eventType = "ORDER_COMPLETED",
                payload = payloadJson,
            )
        )
    }
}