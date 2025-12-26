package com.example.glowpass.outbox.infra

import com.example.glowpass.outbox.domain.ProcessedEvent
import org.springframework.data.jpa.repository.JpaRepository

interface ProcessedEventRepository : JpaRepository<ProcessedEvent, String> {
}

