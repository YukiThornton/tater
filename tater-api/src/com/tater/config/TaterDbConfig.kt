package com.tater.config

interface TaterDbConfig {
    fun url(): String
    fun driver(): String
    fun user(): String
    fun password(): String
}