package org.demo.repository.api.v1.user.domain

import org.demo.repository.common.PasswordEncoder
import java.time.OffsetDateTime
import java.time.ZoneOffset

enum class UserStatus {
    ACTIVE, // 활성
    DORMANT, // 휴면
    WITHDRAWN, // 탈퇴
}

enum class UserRole {
    USER, // 일반 사용자
    ADMIN, // 관리자
}

class User {
    var userId: Long = -1L
        private set
    var nickname: String
        private set

    val username: String // Immutable

    var password: String
        private set

    var status: UserStatus = UserStatus.ACTIVE
        private set

    val createdAt: OffsetDateTime // // Immutable

    var lastAccessAt: OffsetDateTime = OffsetDateTime.MIN
        private set

    var role: UserRole = UserRole.USER
        private set

    private constructor (
        userId: Long,
        nickname: String,
        username: String,
        password: String,
        status: UserStatus,
        createdAt: OffsetDateTime,
        lastAccessAt: OffsetDateTime,
        role: UserRole,
    ) {
        this.userId = userId
        this.nickname = nickname
        this.username = username
        this.password = password
        this.status = status
        this.createdAt = createdAt
        this.lastAccessAt = lastAccessAt
        this.role = role
    }

    companion object {
        private val USERNAME_REGEX = Regex("^[a-z0-9.]{6,30}$")

        private fun validateUsername(username: String) {
            require(USERNAME_REGEX.matches(username)) {
                "Username must be 6-30 chars with lowercase, numbers, or dots."
            }
        }

        private val PASSWORD_REGEX = Regex("^[a-z0-9~!@]{6,16}$")

        private fun validatePassword(password: String) {
            require(PASSWORD_REGEX.matches(password)) {
                "Password must be 6-16 chars with lowercase, numbers, or ~!@."
            }
        }

        private fun validateNickname(nickname: String) {
            require(nickname.isNotBlank() && nickname.length in 1..16) { "Nickname must be 1-16 chars." }
            require(!nickname.contains(" ")) { "Nickname cannot contain spaces." }
        }

        fun create(
            nickname: String,
            username: String,
            rawPassword: String,
            passwordEncoder: PasswordEncoder,
        ): User {
            val now = OffsetDateTime.now(ZoneOffset.UTC)

            validateUsername(username)
            validateNickname(nickname)
            validatePassword(rawPassword)

            return User(
                userId = -1L,
                nickname = nickname,
                username = username,
                password = passwordEncoder.encode(rawPassword),
                status = UserStatus.ACTIVE,
                createdAt = now,
                lastAccessAt = now,
                role = UserRole.USER,
            )
        }

        fun from(
            userId: Long,
            nickname: String,
            username: String,
            password: String,
            status: UserStatus,
            createdAt: OffsetDateTime,
            lastAccessAt: OffsetDateTime,
            role: UserRole,
        ): User =
            User(
                userId = userId,
                nickname = nickname,
                username = username,
                password = password,
                status = status,
                createdAt = createdAt,
                lastAccessAt = lastAccessAt,
                role = role,
            )
    }

    fun setUserId(id: Long) {
        require(this.userId == -1L) { "User ID can only be set once." }
        this.userId = id
    }

    fun changePassword(
        newRawPassword: String,
        passwordEncoder: PasswordEncoder,
    ) {
        require(!passwordEncoder.matches(newRawPassword, this.password)) {
            "New password cannot be the same as the old password"
        }

        validatePassword(newRawPassword)
        this.password = passwordEncoder.encode(newRawPassword)
    }

    fun changeNickname(newNickname: String) {
        validateNickname(newNickname)
        this.nickname = newNickname
    }

    fun authenticate(
        rawPassword: String,
        passwordEncoder: PasswordEncoder,
    ) {
        check(status == UserStatus.ACTIVE) {
            "User is not active (Status: $status)"
        }
        require(passwordEncoder.matches(rawPassword, this.password)) {
            "Password does not match"
        }

        markAccessed()
    }

    fun deactivate() {
        check(status != UserStatus.WITHDRAWN) {
            "Cannot deactivate a withdrawn user"
        }
        check(status != UserStatus.DORMANT) {
            "User is already dormant"
        }
        status = UserStatus.DORMANT
    }

    fun activate() {
        check(status != UserStatus.WITHDRAWN) {
            "Cannot activate a withdrawn user"
        }
        if (status == UserStatus.ACTIVE) return
        status = UserStatus.ACTIVE
    }

    fun markAccessed() {
        check(status != UserStatus.WITHDRAWN) {
            "Withdrawn user cannot be accessed"
        }
        lastAccessAt = OffsetDateTime.now(ZoneOffset.UTC)
    }
}
