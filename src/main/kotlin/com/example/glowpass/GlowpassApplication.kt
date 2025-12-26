package com.example.glowpass

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GlowpassApplication

fun main(args: Array<String>){
    runApplication<GlowpassApplication>(*args)
}