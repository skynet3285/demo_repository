package org.demo.repository.infra.exception

import org.slf4j.LoggerFactory
import org.springframework.dao.DataAccessException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    @ExceptionHandler(DataAccessException::class)
    fun handleDataAccessException(ex: DataAccessException): ResponseEntity<String> {
        logger.error("Global Handler: ${ex.message}", ex)

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("An unexpected error occurred. Please try again later.")
    }
}
