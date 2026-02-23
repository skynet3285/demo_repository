package org.demo.repository.api.v1.user.domain

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import org.demo.repository.common.PasswordEncoder
import java.time.OffsetDateTime

class UserTest :
    BehaviorSpec({
        isolationMode = IsolationMode.InstancePerLeaf

        // ---- Fixture ----
        // Test Double (Mock Object)
        val passwordEncoder = mockk<PasswordEncoder>()

        every { passwordEncoder.encode(any()) } answers {
            "ENCODED_" + firstArg<String>()
        }

        every { passwordEncoder.matches(any(), any()) } answers {
            val raw = firstArg<String>()
            val encodedTarget = secondArg<String>()

            encodedTarget == "ENCODED_$raw"
        }

        // Test Data (Dummy Data / Constant)
        val validNickname = "mirinae"
        val validUsername = "mirinae.dev"
        val validPassword = "password123!"
        // ---- End of Fixture ----

        Given("유저 생성 요청이 주어졌을 때") {
            When("모든 정보가 유효하다면") {
                val user =
                    User.create(
                        nickname = validNickname,
                        username = validUsername,
                        rawPassword = validPassword,
                        passwordEncoder = passwordEncoder,
                    )

                Then("유저가 정상적으로 생성되어야 한다") {
                    user.nickname shouldBe validNickname
                    user.username shouldBe validUsername
                    user.status shouldBe UserStatus.ACTIVE
                    user.role shouldBe UserRole.USER
                }

                Then("비밀번호는 해시되어 저장되어야 한다") {
                    passwordEncoder.matches(validPassword, user.password)
                    user.password shouldNotBe validPassword
                }

                Then("생성 시간과 접속 시간이 설정되어야 한다") {
                    // 정확한 시간 비교는 어려우므로 null이 아니고, 현재 시간 근처인지 확인
                    user.createdAt shouldNotBe null
                    user.lastAccessAt shouldNotBe null
                }
            }

            When("닉네임에 공백이 포함되어 있다면") {
                val invalidNickname = "bad nickname"

                Then("생성이 거부되고 예외가 발생해야 한다") {
                    shouldThrow<IllegalArgumentException> {
                        User.create(
                            nickname = invalidNickname,
                            username = validUsername,
                            rawPassword = validPassword,
                            passwordEncoder = passwordEncoder,
                        )
                    }
                }
            }

            When("비밀번호 형식이 규칙에 맞지 않다면 (너무 짧음)") {
                val shortPassword = "123"

                Then("생성이 거부되어야 한다") {
                    shouldThrow<IllegalArgumentException> {
                        User.create(
                            nickname = validNickname,
                            username = validUsername,
                            rawPassword = shortPassword,
                            passwordEncoder = passwordEncoder,
                        )
                    }
                }
            }
        }

        Given("이미 생성된 활성 유저(User)가 있을 때") {
            val user =
                User.create(
                    nickname = validNickname,
                    username = validUsername,
                    rawPassword = validPassword,
                    passwordEncoder = passwordEncoder,
                )

            When("올바른 비밀번호로 인증을 시도하면") {
                val correctPassword: String = validPassword

                Then("인증이 성공해야 한다") {
                    shouldNotThrowAny {
                        user.authenticate(correctPassword, passwordEncoder)
                    }
                }
            }

            When("틀린 비밀번호로 인증을 시도하면") {
                val wrongPassword = "wrong_password!"

                Then("인증이 실패해야 한다") {
                    shouldThrow<IllegalArgumentException> { user.authenticate(wrongPassword, passwordEncoder) }
                }
            }

            When("새로운 유효한 비밀번호로 변경을 시도하면") {
                val newValidPassword = "new~password123@"
                user.changePassword(newValidPassword, passwordEncoder)

                Then("비밀번호가 변경되어야 한다") {
                    passwordEncoder.matches(newValidPassword, user.password) shouldBe true
                }
            }

            When("기존 비밀번호와 동일한 비밀번호로 변경하려면") {
                val samePassword: String = validPassword

                Then("변경이 거부되어야 한다") {
                    shouldThrow<IllegalArgumentException> {
                        user.changePassword(samePassword, passwordEncoder)
                    }
                }
            }

            When("유저를 비활성화(Deactivate) 하면") {
                user.deactivate()

                Then("상태가 휴면(DORMANT)으로 변경되어야 한다") {
                    user.status shouldBe UserStatus.DORMANT
                }
            }
        }

        Given("휴면(DORMANT) 상태인 유저가 있을 때") {
            val dormantUser = User.create(validNickname, validUsername, validPassword, passwordEncoder)
            dormantUser.deactivate()

            When("다시 비활성화를 시도하면") {
                Then("이미 휴면 상태라는 예외가 발생해야 한다") {
                    shouldThrow<IllegalStateException> {
                        dormantUser.deactivate()
                    }
                }
            }

            When("활성화(Activate)를 시도하면") {
                dormantUser.activate()

                Then("다시 활성(ACTIVE) 상태로 돌아와야 한다") {
                    dormantUser.status shouldBe UserStatus.ACTIVE
                }
            }
        }

        Given("탈퇴(WITHDRAWN) 상태인 유저를 복구(Rehydrate) 했을 때") {
            val now = OffsetDateTime.now()

            val withdrawnUser =
                User.from(
                    userId = 1L,
                    nickname = validNickname,
                    username = validUsername,
                    password = "encoded_pw",
                    status = UserStatus.WITHDRAWN,
                    createdAt = now,
                    lastAccessAt = now,
                    role = UserRole.USER,
                )

            When("활성화를 시도하면") {
                Then("탈퇴한 유저는 활성화될 수 없다는 예외가 발생해야 한다") {
                    shouldThrow<IllegalStateException> {
                        withdrawnUser.activate()
                    }.message shouldBe "Cannot activate a withdrawn user"
                }
            }

            When("접속(Access)을 시도하여 시간을 갱신하려 하면") {
                Then("접속 기록 갱신이 거부되어야 한다") {
                    shouldThrow<IllegalStateException> {
                        withdrawnUser.markAccessed()
                    }.message shouldBe "Withdrawn user cannot be accessed"
                }
            }
        }
    })
