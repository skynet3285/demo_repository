package org.demo.repository.common

interface PasswordEncoder {
    fun encode(raw: String): String

    fun matches(
        raw: String,
        encodedTarget: String,
    ): Boolean
}
