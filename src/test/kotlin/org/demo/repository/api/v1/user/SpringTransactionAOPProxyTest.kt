package org.demo.repository.api.v1.user

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import org.springframework.aop.support.AopUtils
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class SpringTransactionAOPProxyTest(
    private val userService: UserService,
) : DescribeSpec({
        isolationMode = IsolationMode.InstancePerRoot

        it("check proxy type") {
            println("Is AOP Proxy: ${AopUtils.isAopProxy(userService)}")
            println("Is JDK Proxy: ${AopUtils.isJdkDynamicProxy(userService)}")
            println("Is CGLIB Proxy: ${AopUtils.isCglibProxy(userService)}")
        }

        // Spring Transaction AOP Proxy behavior
        describe("Self-invocation과 AOP 프록시 적용 테스트") {
            describe("트랜잭션이 없는 외부 메서드를 호출할 때") {
                it("self-invocation으로 호출된 @Transactional 메서드는 프록시를 거치지 않는다") {
                    userService.internalPlainCallerMethod()
                }
            }

            describe("트랜잭션이 적용된 메서드를 호출할 때") {
                it("self-invocation으로 호출된 다른 @Transactional 메서드는 새로운 프록시 적용이 되지 않는다") {
                    // internalOuterDefaultPropagationTxMethod는 트랜잭션이 적용되어 있어서 AOP 프록시가 적용된다
                    // self-invocation로써 호출되는 모든 @Transactional 메서드는 AOP 프록시가 적용되지 않는다
                    // 따라서, 자식의 @Transactional 메서드들은 부모의 트랜잭션 안에서 실행된다
                    // 그리고, 자식의 @Transactional 옵션은 아무런 의미가 없다.
                    userService.internalOuterDefaultPropagationTxMethod()
                }

                it("private 메서드는 AOP 적용 대상이 아니지만 기존 트랜잭션 안에서 실행된다") {
                    // 위 경우와 동일하다
                    userService.internalOuterDefaultPropagationTxMethod2()
                }
            }
        }

        describe("External invocation (다른 Bean을 통한 호출)") {
            describe("트랜잭션이 없는 외부 메서드를 호출할 때") {
                it("트랜잭션 없는 외부 메서드가 호출한 @Transactional 메서드는 Proxy를 통해 정상적으로 트랜잭션이 적용된다") {
                    // externalPlainCallerMethod는 트랜잭션이 없고 내부에서 다른 Bean의 트랜잭션 메서드를 호출한다
                    // 따라서 AOP 프록시가 적용된 메서드가 호출되고, 트랜잭션이 정상적으로 적용된다
                    userService.externalPlainCallerMethod()
                }
            }

            describe("트랜잭션이 적용된 메서드를 호출할 때") {
                it("트랜잭션이 적용된 외부 메서드가 호출한 @Transactional 메서드도 Proxy를 통해 정상 적용된다") {
                    // externalPlainCallerMethod는 트랜잭션이 적용되어 있어서 AOP 프록시가 적용된다
                    // 내부에서 다른 Bean의 트랜잭션 메서드를 호출한다
                    // 따라서 AOP 프록시가 적용된 메서드가 호출되고, 트랜잭션이 정상적으로 적용된다
                    userService.externalOuterDefaultPropagationTxMethod()
                }
            }
        }
    })
