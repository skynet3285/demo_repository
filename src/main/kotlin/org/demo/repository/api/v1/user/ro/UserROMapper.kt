package org.demo.repository.api.v1.user.ro

import org.demo.repository.api.v1.user.domain.User

fun User.toRO(): UserRO =
    UserRO(
        userId = this.userId,
        nickname = this.nickname,
        username = this.username,
        status = this.status.name,
        createdAt = this.createdAt,
        lastAccessAt = this.lastAccessAt,
        role = this.role.name,
    )
