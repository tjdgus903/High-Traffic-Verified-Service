package com.example.glowpass.outbox.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "member_profile")
class MemberProfile(

    @Id
    @Column(name = "member_id")
    val memberId: Long,

    @Column(name = "age_range")
    var ageRange: String? = null,

    @Column(name = "preferred_categories")
    var preferredCategories: String? = null,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()
) {
}