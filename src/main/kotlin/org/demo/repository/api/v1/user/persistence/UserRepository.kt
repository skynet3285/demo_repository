package org.demo.repository.api.v1.user.persistence

import org.demo.repository.api.v1.user.domain.User
import org.demo.repository.api.v1.user.ro.UserRO
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteAll
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.update
import org.slf4j.LoggerFactory
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.stereotype.Repository
import kotlin.random.Random

@Repository
class UserRepository {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    fun findById(userId: Long): User? {
        val userEntity = UserEntity.findById(userId) ?: return null

        return userEntity.toDomain()
    }

    fun findByIdForUpdate(userId: Long): User? =
        UserEntity
            .find { UserTable.id eq userId }
            .forUpdate()
            .singleOrNull()
            ?.let { it.toDomain() }

    fun findROById(userId: Long): UserRO? {
        val userEntity = UserEntity.findById(userId) ?: return null

        return userEntity.toRO()
    }

    fun findByUsername(username: String): User? {
        val userEntity = UserEntity.find { UserTable.username eq username }.singleOrNull() ?: return null

        return userEntity.toDomain()
    }

    fun findByROUsername(username: String): UserRO? {
        val userEntity = UserEntity.find { UserTable.username eq username }.singleOrNull() ?: return null

        return userEntity.toRO()
    }

    fun create(user: User) {
        // TODO: TSID
        val id = System.currentTimeMillis() + Random.nextLong(1000)

        UserTable.insert {
            it[UserTable.id] = id
            it[nickname] = user.nickname
            it[username] = user.username
            it[password] = user.password
            it[status] = user.status.name
            it[createdAt] = user.createdAt
            it[lastAccessAt] = user.lastAccessAt
            it[role] = user.role.name
        }

        user.setUserId(id)
    }

    fun save(user: User) {
        check(user.userId != -1L) {
            "Cannot update a user that has not been created yet."
        }

        val updatedRows =
            UserTable.update({ UserTable.id eq user.userId }) {
                it[nickname] = user.nickname
                it[username] = user.username
                it[password] = user.password
                it[status] = user.status.name
                it[lastAccessAt] = user.lastAccessAt
                it[role] = user.role.name
            }

        if (updatedRows == 0) {
            throw OptimisticLockingFailureException("data not found or already deleted: ${user.userId}")
        }
    }

    fun delete(user: User): Boolean = UserTable.deleteWhere { UserTable.id eq user.userId } == 1

    fun deleteAll() {
        UserTable.deleteAll()
    }
}
