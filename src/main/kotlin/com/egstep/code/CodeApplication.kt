package com.egstep.code

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(
    scanBasePackages = ["com.egstep"]
)
class CodeApplication

fun main(args: Array<String>) {
    runApplication<CodeApplication>(*args)
}
