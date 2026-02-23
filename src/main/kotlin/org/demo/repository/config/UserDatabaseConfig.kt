package org.demo.repository.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class UserDatabaseConfig {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    init {
        logger.info("User DB Connecting...")
    }

    @Bean
    fun userDataSource(): DataSource = createDataSource()

    private fun createDataSource(): HikariDataSource {
        val config =
            HikariConfig().apply {
                jdbcUrl = "jdbc:postgresql://" + "localhost:5432/demo_user"
                username = "demo_userdb"
                password = "demo_userdb"
                schema = "public"
                driverClassName = "org.postgresql.Driver"
                maximumPoolSize = 20

                addDataSourceProperty("ssl", "false")
            }

        return HikariDataSource(config)
    }
}
