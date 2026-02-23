package org.demo.repository.api.v1.user

import org.demo.repository.api.v1.user.dto.CreateUserDto
import org.demo.repository.api.v1.user.ro.UserRO
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/user")
class UserController(
    private val userService: UserService,
) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    @GetMapping()
    fun hello(): String {
        logger.debug("Hello endpoint called")

        return "Hello, User!"
    }

    @PostMapping()
    fun signUp(
        @RequestBody input: CreateUserDto,
    ): UserRO {
        logger.debug("SignUp endpoint called with input: $input")

        return userService.signUp(input)
    }

    @DeleteMapping()
    fun deleteAllUsers() {
        userService.deleteAllUsers()
    }
}
