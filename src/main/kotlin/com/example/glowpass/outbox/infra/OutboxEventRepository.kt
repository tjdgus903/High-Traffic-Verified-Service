package com.example.glowpass.outbox.infra

import com.example.glowpass.outbox.domain.OutboxEvent
import com.example.glowpass.outbox.domain.OutboxStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional
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

    /**
     * 만약 같은 값이 insert 될 경우 동일한 키값이 있으면 에러를 뱉지 않고 패스
     * */
    @Modifying
    @Transactional
    @Query(
        value = """
            insert into outbox_events
                (event_id, event_type, payload, status, retry_count, next_retry_at, created_at, updated_at)
            values
                (:eventId, :eventType, :payload, :status, 0, null, now(), now())
            on conflict (event_id, event_type) do nothing
        """,
        nativeQuery = true
    )
    fun insertIfAbsent(
        @Param("eventId") eventId: String,
        @Param("eventType") eventType: String,
        @Param("payload") payload: String,
        @Param("status") status: String
    ): Int
}
