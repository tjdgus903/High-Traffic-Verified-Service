package com.example.glowpass.outbox.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

/**
 * 이벤트 중복 방지
 * event_id 가 PK여서 같은 eventId가 들어오면 DB 로 방어
 */
@Entity
@Table(name = "processed_events")
class ProcessedEvent (
    @Id
    @Column(name = "event_id", length = 100)
    val eventId: String,

    @Column(name = "processed_at", nullable = false)
    val processedAt: Instant = Instant.now()
)