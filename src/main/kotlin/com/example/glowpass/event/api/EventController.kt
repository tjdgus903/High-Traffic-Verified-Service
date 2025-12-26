package com.example.glowpass.event.api

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import com.example.glowpass.event.application.OrderCompletedIngestService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

data class OrderCompletedEventRequest(
    @field:NotBlank val eventId: String,
    @field:NotNull val memberId: Long,
    @field:NotNull val totalAmount: Long,
    @field:NotNull val occurredAt: Instant,
)

@RestController
@RequestMapping("/events")
class EventController(
    private val ingestService: OrderCompletedIngestService
) {

    @PostMapping("/order-completed")
    fun orderCompleted(@RequestBody @Valid req: OrderCompletedEventRequest): ResponseEntity<Void>{
        ingestService.ingest(req)
        return ResponseEntity.accepted().build()
    }
}