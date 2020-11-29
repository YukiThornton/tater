package project.tater_e2e.helper

import com.natpryce.konfig.*

class Configurations {
    companion object {
        private val config = ConfigurationProperties.fromResource("e2e.properties")
        val taterApiEndpoint = config[taterApi.endpoint]
        val wiremockHost = config[wiremock.host]
        val wiremockPort = config[wiremock.port]
        val movieApiToken = config[movieApi.token]
        val taterDbDriver = config[taterDb.driver]
        val taterDbUrl = config[taterDb.url]
        val taterDbUserName = config[taterDb.userName]
        val taterDbPassword = config[taterDb.password]
        val taterDbSchema = config[taterDb.schema]
    }

    private object taterApi: PropertyGroup() {
        val endpoint by stringType
    }

    private object wiremock: PropertyGroup() {
        val host by stringType
        val port by intType
    }

    private object movieApi: PropertyGroup() {
        val token by stringType
    }

    private object taterDb: PropertyGroup() {
        val driver by stringType
        val url by stringType
        val userName by stringType
        val password by stringType
        val schema by stringType
    }
}