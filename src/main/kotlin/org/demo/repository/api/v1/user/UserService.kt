package org.demo.repository.api.v1.user

import org.demo.repository.api.v1.user.domain.User
import org.demo.repository.api.v1.user.dto.CreateUserDto
import org.demo.repository.api.v1.user.persistence.UserRepository
import org.demo.repository.api.v1.user.ro.UserRO
import org.demo.repository.api.v1.user.ro.toRO
import org.demo.repository.common.PasswordEncoder
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Transactional(readOnly = true)
    fun findUserByUsername(username: String): UserRO? = userRepository.findByROUsername(username)

    @Transactional
    fun signUp(input: CreateUserDto): UserRO {
        val newUser =
            User.create(
                nickname = input.nickname,
                username = input.username,
                rawPassword = input.password,
                passwordEncoder = passwordEncoder,
            )
        userRepository.create(newUser)

        return newUser.toRO()
    }

    @Transactional(readOnly = true)
    fun signIn(
        username: String,
        rawPassword: String,
    ): UserRO? {
        val user = userRepository.findByUsername(username) ?: return null

        try {
            user.authenticate(rawPassword, passwordEncoder)

            return user.toRO()
        } catch (e: Exception) {
            logger.debug(e.message, e)
            return null
        }
    }

    @Transactional
    fun deleteAllUsers() {
        userRepository.deleteAll()
    }
}
