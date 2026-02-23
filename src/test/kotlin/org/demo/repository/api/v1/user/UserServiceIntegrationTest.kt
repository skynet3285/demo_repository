package org.demo.repository.api.v1.user

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.demo.repository.api.v1.user.dto.CreateUserDto
import org.jetbrains.exposed.v1.exceptions.ExposedSQLException
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class UserServiceIntegrationTest(
    private val userService: UserService,
) : BehaviorSpec({
        isolationMode = IsolationMode.InstancePerRoot

        val validCreateUserDto =
            CreateUserDto(
                nickname = "im_test1234",
                username = "test1234",
                password = "test1234@",
            )

        Given("유저가 없는 상태에서") {
            userService.deleteAllUsers()

            When("올바른 파라미터로 회원가입을 하면") {
                Then("유저 정보가 정상적으로 저장되어야 한다") {

                    shouldNotThrowAny {
                        userService.signUp(validCreateUserDto)
                    }

                    val savedUser = userService.findUserByUsername(validCreateUserDto.username)
                    savedUser shouldNotBe null
                    savedUser?.nickname shouldBe validCreateUserDto.nickname
                    savedUser?.username shouldBe validCreateUserDto.username
                }
            }

            When("올바르지 않은 파라미터로 회원가입을 하면") {
                val invalidCreateUserDto =
                    CreateUserDto(
                        nickname = "wrong nickname", // space not allowed
                        username = "short", // too short
                        password = "shor", // too short
                    )

                Then("예외가 발생해야 한다") {
                    shouldThrow<IllegalArgumentException> {
                        userService.signUp(invalidCreateUserDto)
                    }
                }
            }
        }

        Given("이미 유저가 존재하는 상태에서 1") {
            userService.deleteAllUsers()
            userService.signUp(validCreateUserDto)

            When("동일한 username으로 회원가입을 하면") {
                val duplicateUsernameDto =
                    CreateUserDto(
                        nickname = "another_nickname",
                        username = validCreateUserDto.username, // duplicate username
                        password = "password@",
                    )

                Then("예외가 발생해야 한다") {
                    shouldThrow<ExposedSQLException> {
                        userService.signUp(duplicateUsernameDto)
                    }
                }
            }
        }

        Given("이미 유저가 존재하는 상태에서 2") {
            userService.deleteAllUsers()
            userService.signUp(validCreateUserDto)

            When("동일하지 않은 username으로 회원가입을 하면") {
                val uniqueUsernameDto =
                    CreateUserDto(
                        nickname = "unique-nickname",
                        username = "unique.username",
                        password = "password@",
                    )

                Then("회원가입이 성공해야 한다") {
                    shouldNotThrowAny {
                        userService.signUp(uniqueUsernameDto)
                    }

                    val savedUser = userService.findUserByUsername(uniqueUsernameDto.username)
                    savedUser shouldNotBe null
                    savedUser?.nickname shouldBe uniqueUsernameDto.nickname
                    savedUser?.username shouldBe uniqueUsernameDto.username
                }
            }
        }
    })
