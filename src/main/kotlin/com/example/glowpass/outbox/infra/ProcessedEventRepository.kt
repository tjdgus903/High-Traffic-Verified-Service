package org.example.com.example.glowpass.outbox.infra

import org.example.com.example.glowpass.outbox.domain.ProcessedEvent
import org.springframework.data.jpa.repository.JpaRepository

interface ProcessedEventRepository : JpaRepository<ProcessedEvent, String> {
}