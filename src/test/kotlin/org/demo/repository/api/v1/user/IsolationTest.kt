package org.demo.repository.api.v1.user

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class IsolationTest :
    BehaviorSpec({
        isolationMode = IsolationMode.InstancePerRoot

        // BehaviorSpec is BDD(Given-When-Then)
        // Given, When = TestContainer
        // Then = TestCase = Leaf node

        var counter = 0

        Given("루트 컨테이너 A") {
            println("루트 컨테이너 A (counter = $counter)")

            When("counter를 증가시키면 1") {
                println("counter를 증가시키면 1")
                ++counter

                Then("counter는 1이어야 한다") {
                    println("It container: counter = $counter")
                    counter shouldBe 1
                }
            }

            When("counter를 증가시키면 2") {
                println("counter를 증가시키면 2")
                ++counter

                Then("counter는 2이어야 한다") {
                    println("It container: counter = $counter")
                    counter shouldBe 2
                }
            }

            When("counter를 증가시키면 3") {
                println("counter를 증가시키면 3")
                ++counter

                Then("counter는 3이어야 한다") {
                    println("It container: counter = $counter")
                    counter shouldBe 3
                }
            }
        }

        Given("루트 컨테이너 B") {
            println("루트 컨테이너 B (counter = $counter)")

            When("counter를 증가시키면 1") {
                println("counter를 증가시키면 1")
                ++counter

                Then("counter는 1이어야 한다 1") {
                    println("counter는 1이어야 한다 1")
                    counter shouldBe 1
                }

                Then("counter는 1이어야 한다 2") {
                    println("counter는 1이어야 한다 2")
                    counter shouldBe 1
                }
            }

            When("counter를 증가시키면 2") {
                println("counter를 증가시키면 2")
                ++counter

                Then("counter는 2이어야 한다") {
                    println("counter는 2이어야 한다")
                    counter shouldBe 2
                }
            }
        }
    })
