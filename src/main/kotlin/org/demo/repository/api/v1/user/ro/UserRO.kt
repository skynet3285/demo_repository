package org.demo.repository.api.v1.user.ro

import java.time.OffsetDateTime

data class UserRO(
    val userId: Long,
    val nickname: String,
    val username: String,
    val status: String,
    val createdAt: OffsetDateTime,
    val lastAccessAt: OffsetDateTime,
    val role: String,
)
