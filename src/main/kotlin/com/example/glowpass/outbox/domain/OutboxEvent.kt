package org.example.com.example.glowpass.outbox.domain

import jakarta.persistence.*
import jakarta.validation.Payload
import java.time.Instant


/**
 * Outbox 패턴의 핵심 테이블 매핑.
 *
 * 목표:
 * - API가 이벤트를 받으면 "DB에 안전하게 저장"하고 즉시 응답(202)
 * - 실제 처리는 Worker가 outbox를 읽어서 수행
 */
@Entity
@Table(
    name = "outbox_events",
    uniqueConstraints = [UniqueConstraint(name = "uk_outbox_event", columnNames = ["event_id", "event_type"])]
)
class OutboxEvent (
    @Column(name = "event_id", nullable = false, length = 100)
    val eventId : String,

    @Column(name = "event_type", nullable = false, length = 50)
    val eventType : String,

    @Column(name = "payload", nullable = false, columnDefinition = "jsonb")
    val payload: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: OutboxStatus = OutboxStatus.PENDING
){
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Column(name = "retry_count", nullable = false)
    var retryCount: Int = 0

    @Column(name = "next_retry_at")
    var nextRetryAt: Instant? = null

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now()

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()

    fun markProcessing(){
        status = OutboxStatus.PROCESSING
        updatedAt = Instant.now()
    }

    fun markDone(){
        status = OutboxStatus.DONE
        updatedAt = Instant.now()
    }

    fun markRetry(nextRetryAt: Instant){
        status = OutboxStatus.PENDING
        retryCount += 1
        this.nextRetryAt = nextRetryAt
        updatedAt = Instant.now()
    }

    fun markFailed(){
        status = OutboxStatus.FAILED
        updatedAt = Instant.now()
    }
}
/**
 * PENDING : 아직 처리 전(대기)
 * PROCESSING : 지금 처리 중(동시 처리/중복 처리 방지용)
 * DONE : 처리 완료
 * FAILED : 실패
 */
enum class OutboxStatus {PENDING, PROCESSING, DONE, FAILED}