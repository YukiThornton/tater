package com.tater.rest

import com.tater.domain.*
import com.tater.usecase.*
import io.ktor.http.*
import io.ktor.request.*

class HttpRequestExecutor(
    private val viewingHistoryUsecase: ViewingHistoryUsecase,
    private val movieSearchUsecase: MovieSearchUsecase
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

    fun getV1TopRated(request: ApplicationRequest): Result<MovieListJson> {
        val userId = request.header(HEADER_USER_ID)?.let(::UserId)
        return try {
            movieSearchUsecase.topRatedMovies(userId).toJson()
                    .let { Result(HttpStatusCode.OK, it, null) }
        } catch (e: UserNotSpecifiedException) {
            Result(HttpStatusCode.BadRequest, null, e)
        } catch (e: TopRatedMoviesUnavailableException) {
            Result(HttpStatusCode.InternalServerError, null, e)
        }
    }
}

fun MovieSummary.toJson() = MovieSummaryJson(this.id.value, this.title.value)
fun MovieSummaries.toJson() = this.map { summary -> summary.toJson() }.let(::MovieSummariesJson)

fun MovieReview.toJson() = ReviewJson(this.averageScore.value, this.count.value)
fun PersonalizedMovie.toJson() = MovieJson(this.movieId.value, this.movieTitle.value, this.watched, this.movieReview.toJson())
fun PersonalizedMovies.toJson() = this.map { movie -> movie.toJson() }.let(::MovieListJson)