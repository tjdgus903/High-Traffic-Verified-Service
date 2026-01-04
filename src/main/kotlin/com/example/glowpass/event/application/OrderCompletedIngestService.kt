package com.example.glowpass.event.application

import com.example.glowpass.event.api.OrderCompletedEventRequest
import com.fasterxml.jackson.databind.ObjectMapper
import com.example.glowpass.outbox.domain.OutboxStatus
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

        val inserted = outboxRepository.insertIfAbsent(
            eventId = req.eventId,
            eventType = "ORDER_COMPLETED",
            payload = objectMapper.writeValueAsString(req),
            status = OutboxStatus.PENDING.name
        )

        if(inserted == 0){
            // 중복 이벤트 -> 정상종료(202)
            return
        }
    }
}