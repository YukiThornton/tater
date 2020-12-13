package com.tater

import com.tater.rest.RequestHandler
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import com.fasterxml.jackson.databind.*
import com.tater.config.Configuration
import com.tater.driver.MovieApi
import com.tater.driver.MovieApiClient
import com.tater.driver.TaterDb
import com.tater.driver.TaterPostgresqlDb
import com.tater.gateway.MovieSummaryGateway
import com.tater.gateway.ViewingHistoryGateway
import com.tater.port.MovieSummaryPort
import com.tater.port.ViewingHistoryPort
import com.tater.usecase.ViewingHistoryUsecase
import io.ktor.jackson.*
import io.ktor.features.*
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import org.kodein.di.ktor.kodein

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    kodein {
        val configuration = Configuration("/etc/tater/app.properties")
        bind<Configuration>() with singleton { configuration }
        bind<TaterDb>() with singleton { TaterPostgresqlDb(configuration.taterDb()) }
        bind<MovieApi>() with singleton { MovieApiClient(configuration.movieApi()) }
        bind<MovieSummaryPort>() with singleton { MovieSummaryGateway(instance()) }
        bind<ViewingHistoryPort>() with singleton { ViewingHistoryGateway(instance()) }
        bind<ViewingHistoryUsecase>() with singleton { ViewingHistoryUsecase(instance(), instance()) }
        bind<RequestHandler>() with singleton { RequestHandler(instance()) }
    }

    val handler by kodein().instance<RequestHandler>()

    routing {
        get("/v1/systems/ping") {
            call.respondText("PONG!", contentType = ContentType.Text.Plain)
        }
        get("/v1/watched") {
            val result = handler.getV1Watched(call.request)
            if (result.error != null) {
                call.application.environment.log.error("Failed on GET /v1/watched", result.error)
            }
            call.respond(result.responseStatus, result.responseBody ?: "")
        }
    }
}
