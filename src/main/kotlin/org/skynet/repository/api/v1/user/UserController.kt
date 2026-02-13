package org.skynet.repository.api.v1.user

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/user")
class UserController {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    @GetMapping()
    fun hello(): String {
        logger.debug("Hello endpoint called")

        return "Hello, User!"
    }
}
