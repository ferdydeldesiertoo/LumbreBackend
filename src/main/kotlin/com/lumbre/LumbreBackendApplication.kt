package com.lumbre

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LumbreBackendApplication

fun main(args: Array<String>) {
    runApplication<LumbreBackendApplication>(*args)
}
