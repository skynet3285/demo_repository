package org.demo.repository.api.v1.user.persistence

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.LongEntity
import org.jetbrains.exposed.v1.dao.LongEntityClass

class UserEntity(
    id: EntityID<Long>,
) : LongEntity(id) {
    companion object : LongEntityClass<UserEntity>(UserTable)

    var nickname by UserTable.nickname
    var username by UserTable.username
    var password by UserTable.password
    var status by UserTable.status
    var createdAt by UserTable.createdAt
    var lastAccessAt by UserTable.lastAccessAt
    var role by UserTable.role
}
