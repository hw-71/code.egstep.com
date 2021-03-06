package com.egstep.fwk.config.db

import com.opentable.db.postgres.embedded.EmbeddedPostgres
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import javax.sql.DataSource

@Configuration
class EmbeddedDb {
    @Primary
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean(name = ["embeddedPrimaryDb"])
    fun memoryPg(): DataSource {
        return EmbeddedPostgres.builder()
            .setServerConfig("timezone", "Asia/Seoul")
            .setPort(55432)
            .start().postgresDatabase
    }
}