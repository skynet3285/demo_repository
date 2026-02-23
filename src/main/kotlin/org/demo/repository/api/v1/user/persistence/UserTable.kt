package org.demo.repository.api.v1.user.persistence

import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.javatime.timestampWithTimeZone

object UserTable : LongIdTable("user", "user_id") {
    val nickname = varchar("nickname", 64)
    val username = varchar("username", 64)
    val password = varchar("password", 256)
    val status = varchar("status", 16)
    val createdAt = timestampWithTimeZone("created_at")
    val lastAccessAt = timestampWithTimeZone("last_access_at")
    val role = varchar("role", 16)
}
