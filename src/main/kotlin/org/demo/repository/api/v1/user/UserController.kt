package org.demo.repository.api.v1.user

import org.demo.repository.api.v1.user.domain.User
import org.demo.repository.api.v1.user.dto.CreateUserDto
import org.demo.repository.api.v1.user.ro.UserRO
import org.demo.repository.common.PasswordEncoder
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
    private val passwordEncoder: PasswordEncoder,
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

    // 의도적으로 동일한 id 회원가입
    @GetMapping("/test")
    fun test() {
        // 만일, 의도적으로 데이터베이스를 끊으면, org.springframework.dao.TransientDataAccessResourceException가 발생한다

        val validCreateUserDto =
            CreateUserDto(
                nickname = "this_is_test",
                username = "test9999",
                password = "test1234@",
            )

        val existingUser =
            User.create(
                username = validCreateUserDto.username,
                nickname = validCreateUserDto.nickname,
                rawPassword = validCreateUserDto.password,
                passwordEncoder = passwordEncoder,
            )
        existingUser.setUserId(1L)

        userService.unsafeSignUp(existingUser)
    }
}
