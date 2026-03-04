package org.demo.repository.config

import com.github.skynet3285.exposed.exception.translator.annotation.ExposedExceptionTranslationPostProcessor
import com.github.skynet3285.exposed.exception.translator.support.ExposedExceptionTranslator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.support.SQLExceptionTranslator
import javax.sql.DataSource

// ExposedSQLException to Spring's DataAccessException in Repository layer
@Configuration
class ExposedConfig {
    @Bean
    fun exposedExceptionTranslator(dataSource: DataSource): SQLExceptionTranslator = ExposedExceptionTranslator(dataSource)

    @Bean
    fun exposedExceptionTranslationPostProcessor(): ExposedExceptionTranslationPostProcessor = ExposedExceptionTranslationPostProcessor()
}
