package org.skynet.repository

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DemoRepositoryApplication

fun main(args: Array<String>) {
    runApplication<DemoRepositoryApplication>(*args)
}
