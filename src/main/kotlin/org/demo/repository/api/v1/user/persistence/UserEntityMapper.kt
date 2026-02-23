package org.demo.repository.api.v1.user.persistence

import org.demo.repository.api.v1.user.domain.User
import org.demo.repository.api.v1.user.domain.UserRole
import org.demo.repository.api.v1.user.domain.UserStatus
import org.demo.repository.api.v1.user.ro.UserRO

fun UserEntity.toRO(): UserRO =
    UserRO(
        userId = this.id.value,
        nickname = this.nickname,
        username = this.username,
        status = this.status,
        createdAt = this.createdAt,
        lastAccessAt = this.lastAccessAt,
        role = this.role,
    )

fun UserEntity.toDomain(): User =
    User.from(
        userId = this.id.value,
        nickname = this.nickname,
        username = this.username,
        password = this.password,
        status = UserStatus.valueOf(this.status),
        createdAt = this.createdAt,
        lastAccessAt = this.lastAccessAt,
        role = UserRole.valueOf(this.role),
    )
