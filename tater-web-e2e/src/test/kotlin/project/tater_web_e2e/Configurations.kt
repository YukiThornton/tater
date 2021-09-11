package project.tater_web_e2e

import com.natpryce.konfig.*

object Configurations {

    private val config = ConfigurationProperties.fromResource("e2e.properties")
    val movieApiHost = config[movieApi.host]
    val movieApiPort = config[movieApi.port]
    val movieApiToken = config[movieApi.token]

    private object movieApi: PropertyGroup() {
        val host by stringType
        val port by intType
        val token by stringType
    }
}