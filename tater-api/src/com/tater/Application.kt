package com.tater

import com.fasterxml.jackson.databind.SerializationFeature
import com.tater.config.Configuration
import com.tater.driver.MovieApi
import com.tater.driver.MovieApiClient
import com.tater.driver.TaterDb
import com.tater.driver.TaterPostgresqlDb
import com.tater.gateway.LocalizedAttributesGateway
import com.tater.gateway.MovieGateway
import com.tater.gateway.ReviewedMovieGateway
import com.tater.gateway.ViewingHistoryGateway
import com.tater.port.LocalizedAttributesPort
import com.tater.port.MoviePort
import com.tater.port.ReviewedMoviePort
import com.tater.port.ViewingHistoryPort
import com.tater.rest.HttpRequestExecutor
import com.tater.rest.JsonConverter
import com.tater.usecase.MovieAcquisitionUsecase
import com.tater.usecase.MovieSearchUsecase
import com.tater.usecase.UserIdChecker
import com.tater.usecase.ViewingHistoryUsecase
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.response.*
import io.ktor.routing.*
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

    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Get)
        header("tater-user-id")
        anyHost()
    }

    kodein {
        val configuration = Configuration("/etc/tater/app.properties")
        bind<Configuration>() with singleton { configuration }
        bind<TaterDb>() with singleton { TaterPostgresqlDb(configuration.taterDb()) }
        bind<MovieApi>() with singleton { MovieApiClient(configuration.movieApi()) }
        bind<MoviePort>() with singleton { MovieGateway(instance()) }
        bind<LocalizedAttributesPort>() with singleton { LocalizedAttributesGateway(instance()) }
        bind<ViewingHistoryPort>() with singleton { ViewingHistoryGateway(instance()) }
        bind<ReviewedMoviePort>() with singleton { ReviewedMovieGateway(instance()) }
        bind<UserIdChecker>() with singleton { UserIdChecker() }
        bind<MovieAcquisitionUsecase>() with singleton { MovieAcquisitionUsecase(instance(), instance(), instance()) }
        bind<ViewingHistoryUsecase>() with singleton { ViewingHistoryUsecase(instance(), instance(), instance()) }
        bind<MovieSearchUsecase>() with singleton { MovieSearchUsecase(instance(), instance(), instance()) }
        bind<HttpRequestExecutor>() with singleton { HttpRequestExecutor(JsonConverter(), instance(), instance(), instance()) }
    }

    val executor by kodein().instance<HttpRequestExecutor>()

    routing {
        get("/v1/systems/ping") {
            call.respondText("PONG!", contentType = ContentType.Text.Plain)
        }
        get("/v1/movies/{id}") {
            val result = executor.getV1MovieWithId(call)
            if (result.error != null) {
                call.application.environment.log.error("Failed on GET /v1/movies/:id", result.error)
            }
            call.respond(result.responseStatus, result.responseBody ?: "")
        }
        get("/v1/watched") {
            val result = executor.getV1Watched(call)
            if (result.error != null) {
                call.application.environment.log.error("Failed on GET /v1/watched", result.error)
            }
            call.respond(result.responseStatus, result.responseBody ?: "")
        }
        get("/v1/top-rated") {
            val result = executor.getV1TopRated(call)
            if (result.error != null) {
                call.application.environment.log.error("Failed on GET /v1/top-rated", result.error)
            }
            call.respond(result.responseStatus, result.responseBody ?: "")
        }
    }
}

