package com.tater.rest

import com.tater.domain.MovieSummaries
import com.tater.domain.MovieSummary
import com.tater.domain.UserId
import com.tater.usecase.UserNotSpecifiedException
import com.tater.usecase.ViewingHistoryUsecase
import com.tater.usecase.WatchedMoviesUnavailableException
import io.ktor.http.*
import io.ktor.request.*

class RequestHandler(
    private val viewingHistoryUsecase: ViewingHistoryUsecase
) {

    companion object {
        private const val HEADER_USER_ID = "tater-user-id"
    }

    data class Result<T>(val responseStatus: HttpStatusCode, val responseBody: T? = null, val error: Throwable? = null)

    fun getV1Watched(request: ApplicationRequest): Result<MovieSummariesJson> {
        val userId = request.header(HEADER_USER_ID)?.let(::UserId)
        return try {
            val summaries = viewingHistoryUsecase.allMoviesWatchedBy(userId)
            Result(HttpStatusCode.OK, summaries.toJson())
        } catch (e: UserNotSpecifiedException) {
            Result(HttpStatusCode.BadRequest, null, e)
        } catch (e: WatchedMoviesUnavailableException) {
            Result(HttpStatusCode.InternalServerError, null, e)
        }
    }
}

fun MovieSummary.toJson() = MovieSummaryJson(this.id.value, this.title.value)
fun MovieSummaries.toJson() = this.map { summary -> summary.toJson() }.let(::MovieSummariesJson)