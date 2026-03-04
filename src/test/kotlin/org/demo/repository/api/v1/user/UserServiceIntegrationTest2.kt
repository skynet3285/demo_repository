package org.demo.repository.api.v1.user

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import org.assertj.core.error.ShouldBeTrue.shouldBeTrue
import org.demo.repository.api.v1.user.domain.User
import org.demo.repository.api.v1.user.dto.CreateUserDto
import org.demo.repository.common.PasswordEncoder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.DuplicateKeyException
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class UserServiceIntegrationTest2(
    @Autowired private val userService: UserService,
    private val passwordEncoder: PasswordEncoder,
    service: UserService,
) : BehaviorSpec({
        isolationMode = IsolationMode.InstancePerRoot

        val validCreateUserDto =
            CreateUserDto(
                nickname = "im_test1234",
                username = "test1234",
                password = "test1234@",
            )

        // 의도적으로 데이터베이스를 끊으면, org.springframework.dao.DataAccessResourceFailureException 예외가 발생한다

        Given("이미 유저가 존재하는 상태에서") {
            userService.deleteAllUsers()
            userService.signUp(validCreateUserDto)

            When("동일한 username(unique)로 회원가입을 하면 예외가 발생하고") {
                val exception =
                    shouldThrow<DuplicateKeyException> {
                        userService.signUp(validCreateUserDto)
                    }

                Then("Spring의 DuplicateKeyException 계층으로 변환되어야 한다") {
                    // message:
                    // Exposed Operation; ERROR: duplicate key value violates unique constraint "user_username_key"
                    shouldBeTrue(exception.message?.contains("username") ?: false)
                }
            }
        }

        Given("이미 유저가 존재하는 상태에서 2") {
            userService.deleteAllUsers()
            val existingUser =
                User.create(
                    username = validCreateUserDto.username,
                    nickname = validCreateUserDto.nickname,
                    rawPassword = validCreateUserDto.password,
                    passwordEncoder = passwordEncoder,
                )
            existingUser.setUserId(10L)

            userService.unsafeSignUp(existingUser)

            When("동일한 id(pk)로 회원가입을 하면 예외가 발생하고") {
                val duplicateUser =
                    User.create(
                        nickname = "another_nickname",
                        username = "anotherUsername".lowercase(),
                        rawPassword = "anotherPassword".lowercase(),
                        passwordEncoder = passwordEncoder,
                    )
                duplicateUser.setUserId(10L)

                val exception =
                    shouldThrow<DuplicateKeyException> {
                        userService.unsafeSignUp(duplicateUser)
                    }

                Then("Spring의 DuplicateKeyException 계층으로 변환되어야 한다") {
                    // message:
                    // Exposed Operation; ERROR: duplicate key value violates unique constraint "user_pkey"
                    shouldBeTrue(exception.message?.contains("user_pkey") ?: false)
                }
            }
        }
    })
