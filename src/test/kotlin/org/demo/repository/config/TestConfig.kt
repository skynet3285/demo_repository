package org.demo.repository.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.extensions.spring.SpringExtension
import io.kotest.extensions.spring.SpringTestLifecycleMode

// https://kotest.io/docs/6.0/extensions/spring.html
object TestConfig : AbstractProjectConfig() {
    override val extensions: List<Extension> =
        listOf(SpringExtension(SpringTestLifecycleMode.Root)) // default is SpringTestLifecycleMode.Test
}
