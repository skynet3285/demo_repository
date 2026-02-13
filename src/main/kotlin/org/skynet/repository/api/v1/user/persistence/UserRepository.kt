package org.skynet.repository.api.v1.user.persistence

import org.jetbrains.exposed.v1.core.eq
import org.skynet.repository.api.v1.user.domain.User
import org.skynet.repository.api.v1.user.domain.UserRole
import org.skynet.repository.api.v1.user.domain.UserStatus
import org.skynet.repository.api.v1.user.ro.UserRO
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import kotlin.random.Random

@Repository
class UserRepository {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    private fun toRO(entity: UserEntity): UserRO =
        UserRO(
            userId = entity.id.value,
            nickname = entity.nickname,
            username = entity.username,
            status = entity.status,
            createdAt = entity.createdAt.toString(),
            lastAccessAt = entity.lastAccessAt,
            role = entity.role,
        )

    private fun toDomain(entity: UserEntity): User =
        User.from(
            userId = entity.id.value,
            nickname = entity.nickname,
            username = entity.username,
            password = entity.password,
            status = UserStatus.valueOf(entity.status),
            createdAt = entity.createdAt,
            lastAccessAt = entity.lastAccessAt,
            role = UserRole.valueOf(entity.role),
        )

    fun findById(userId: Long): User? {
        val userEntity = UserEntity.findById(userId) ?: return null

        return toDomain(userEntity)
    }

    fun findByIdForUpdate(userId: Long): User? =
        UserEntity
            .find { UserTable.id eq userId }
            .forUpdate()
            .singleOrNull()
            ?.let { toDomain(it) }

    fun findROById(userId: Long): UserRO? {
        val userEntity = UserEntity.findById(userId) ?: return null

        return toRO(userEntity)
    }

    fun create(user: User) {
        // TODO: TSID
        val id = System.currentTimeMillis() + Random.nextLong(1000)

        UserEntity.new(id) {
            nickname = user.nickname
            username = user.username
            password = user.password
            status = user.status.name
            createdAt = user.createdAt
            lastAccessAt = user.lastAccessAt
            role = user.role.name
        }
    }

    fun save(user: User) {
        val userEntity = UserEntity.findById(user.userId) ?: return

        userEntity.nickname = user.nickname
        userEntity.username = user.username
        userEntity.password = user.password
        userEntity.status = user.status.name
        userEntity.lastAccessAt = user.lastAccessAt
        userEntity.role = user.role.name
    }

    fun delete(user: User) {
        val userEntity = UserEntity.findById(user.userId) ?: return
        userEntity.delete()
    }
}
