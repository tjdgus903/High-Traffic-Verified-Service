package com.example.glowpass.outbox.infra

import com.example.glowpass.outbox.domain.OutboxEvent
import com.example.glowpass.outbox.domain.OutboxStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.Instant

interface OutboxEventRepository : JpaRepository<OutboxEvent, Long> {

    @Query(
        """
            select e from OutboxEvent e
            where e.status = :status
                and (e.nextRetryAt is null or e.nextRetryAt <= :now)
            order by e.createdAt asc
        """
    )
    fun findPending(
        @Param("status") status: OutboxStatus = OutboxStatus.PENDING,
        @Param("now") now: Instant,
    ): List<OutboxEvent>
}
