package com.tater.rest

import com.tater.domain.*
import com.tater.usecase.RecommendationUsecase
import com.tater.usecase.UserNotSpecifiedException
import com.tater.usecase.ViewingHistoryUsecase
import com.tater.usecase.WatchedMoviesUnavailableException
import io.ktor.http.*
import io.ktor.request.*

class HttpRequestExecutor(
    private val viewingHistoryUsecase: ViewingHistoryUsecase,
    private val recommendationUsecase: RecommendationUsecase
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

    fun getV1Recommended(request: ApplicationRequest): Result<MovieListJson> {
        val userId = request.header(HEADER_USER_ID)?.let(::UserId)
        return recommendationUsecase.recommendedMovies(userId).toJson().let {
            Result(HttpStatusCode.OK, it, null)
        }
    }
}

fun MovieSummary.toJson() = MovieSummaryJson(this.id.value, this.title.value)
fun MovieSummaries.toJson() = this.map { summary -> summary.toJson() }.let(::MovieSummariesJson)

fun MovieReview.toJson() = ReviewJson(this.averageScore.value, this.count.value)
fun Movie.toJson() = MovieJson(this.id.value, this.title.value, this.review.toJson())
fun Movies.toJson() = this.map { movie -> movie.toJson() }.let(::MovieListJson)