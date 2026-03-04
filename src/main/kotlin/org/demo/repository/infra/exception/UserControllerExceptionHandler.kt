package org.demo.repository.infra.exception

import org.demo.repository.api.v1.user.UserController
import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.dao.DuplicateKeyException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(assignableTypes = [UserController::class])
@Order(0)
class UserControllerExceptionHandler {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    @ExceptionHandler(DuplicateKeyException::class)
    fun handleLocalDuplicateKey(ex: DuplicateKeyException): ResponseEntity<String> {
        logger.error("Handled by Controller: ${ex.message}", ex)

        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body("Duplicate key error")
    }
}
