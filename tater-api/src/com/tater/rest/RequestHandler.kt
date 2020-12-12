package com.tater.rest

import com.tater.domain.MovieSummaries
import com.tater.domain.MovieSummary
import com.tater.domain.UserId
import com.tater.usecase.ViewingHistoryUsecase
import io.ktor.http.*
import io.ktor.request.*

class RequestHandler(
    private val viewingHistoryUsecase: ViewingHistoryUsecase
) {

    companion object {
        private const val HEADER_USER_ID = "tater-user-id"
    }

    data class Response<T>(val statusCode: HttpStatusCode, val body: T)

    fun getV1Watched(request: ApplicationRequest): Response<MovieSummariesJson> {
        val userId = UserId(request.header(HEADER_USER_ID)!!)
        val summaries = viewingHistoryUsecase.allMoviesWatchedBy(userId)
        return Response(HttpStatusCode.OK, summaries.toJson())
    }
}

fun MovieSummary.toJson() = MovieSummaryJson(this.id.value, this.title.value)
fun MovieSummaries.toJson() = this.map { summary -> summary.toJson() }.let(::MovieSummariesJson)