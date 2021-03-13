package com.egstep.code

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(
    scanBasePackages = ["com.egstep"],
    exclude = [DataSourceAutoConfiguration::class]
)
class AppMain

fun main(args: Array<String>) {
    runApplication<AppMain>(*args)
}
