package org.demo.repository.api.v1.user

import org.demo.repository.api.v1.user.domain.User
import org.demo.repository.api.v1.user.dto.CreateUserDto
import org.demo.repository.api.v1.user.persistence.UserRepository
import org.demo.repository.api.v1.user.ro.UserRO
import org.demo.repository.api.v1.user.ro.toRO
import org.demo.repository.common.PasswordEncoder
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    // ---- AOP TEST METHODS ----
    @Transactional
    fun defaultPropagationTxMethod() {
        logger.debug("Calling defaultPropagationTxMethod()")
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun requiresNewPropagationTxMethod() {
        logger.debug("Calling requiresNewPropagationTxMethod()")
    }

    private fun privateInnerMethod() {
        logger.debug("Calling privateInnerMethod()")
    }

    // self-invocation 테스트
    fun internalPlainCallerMethod() {
        logger.debug("Calling internalPlainCallerMethod()")
        defaultPropagationTxMethod()
        requiresNewPropagationTxMethod()
    }

    @Transactional
    fun internalOuterDefaultPropagationTxMethod() {
        logger.debug("Calling internalOuterDefaultPropagationTxMethod()")
        defaultPropagationTxMethod()
        requiresNewPropagationTxMethod()
    }

    @Transactional
    fun internalOuterDefaultPropagationTxMethod2() {
        logger.debug("Calling internalOuterDefaultPropagationTxMethod2()")
        privateInnerMethod()
        defaultPropagationTxMethod()
        requiresNewPropagationTxMethod()
    }

    // proxy 호출 테스트
    fun externalPlainCallerMethod() {
        logger.debug("Calling externalPlainCallerMethod()")
        userRepository.defaultPropagationTxMethod()
        userRepository.requiresNewPropagationTxMethod()
    }

    @Transactional
    fun externalOuterDefaultPropagationTxMethod() {
        logger.debug("Calling externalOuterDefaultPropagationTxMethod()")
        userRepository.defaultPropagationTxMethod()
        userRepository.requiresNewPropagationTxMethod()
    }
    // -----------------------

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

    @Transactional
    fun unsafeSignUp(user: User) {
        userRepository.unsafeCreate(user)
    }

    @Transactional
    fun updateNickname(
        userId: Long,
        newNickname: String,
    ): UserRO? {
        val user = userRepository.findByIdForUpdate(userId) ?: return null

        user.changeNickname(newNickname)

        userRepository.save(user)

        return user.toRO()
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
