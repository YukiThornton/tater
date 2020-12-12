package com.tater.config

import java.io.File
import java.util.*

class Configuration(private val confFilePath: String) {
    private val config: Properties
    init {
        config = Properties().apply { File(confFilePath).inputStream().use(this::load) }
    }

    fun taterDb(): TaterDbConfig {
        return object : TaterDbConfig {
            override fun url() = config.getProperty("taterDbUrl")
            override fun driver() = config.getProperty("taterDbDriver")
            override fun user() = config.getProperty("taterDbUser")
            override fun password() = config.getProperty("taterDbPassword")

        }
    }

    fun movieApi(): MovieApiConfig {
        return object : MovieApiConfig {
            override fun endpoint() = config.getProperty("movieApiEndpoint")
            override fun authToken() = config.getProperty("movieApiToken")
        }
    }
}