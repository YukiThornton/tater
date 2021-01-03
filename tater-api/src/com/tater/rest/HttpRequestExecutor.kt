package com.tater.rest

import com.tater.domain.*
import com.tater.usecase.*
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*

class HttpRequestExecutor(
    private val viewingHistoryUsecase: ViewingHistoryUsecase,
    private val movieSearchUsecase: MovieSearchUsecase,
    private val movieDetailUsecase: MovieDetailUsecase,
) {

    companion object {
        private const val HEADER_USER_ID = "tater-user-id"
    }

    data class Result<T>(val responseStatus: HttpStatusCode, val responseBody: T? = null, val error: Throwable? = null)

    fun getV1Watched(requestCall: ApplicationCall): Result<MovieSummariesJson> {
        val userId = requestCall.request.header(HEADER_USER_ID)?.let(::UserId)
        return try {
            viewingHistoryUsecase.allMoviesWatchedBy(userId).toJson().toOkResult()
        } catch (e: UserNotSpecifiedException) {
            Result(HttpStatusCode.BadRequest, null, e)
        } catch (e: WatchedMoviesUnavailableException) {
            Result(HttpStatusCode.InternalServerError, null, e)
        }
    }

    fun getV1TopRated(request: ApplicationCall): Result<ReviewedMovieListJson> {
        val userId = request.request.header(HEADER_USER_ID)?.let(::UserId)
        return try {
            movieSearchUsecase.topRatedMovies(userId).toJson().toOkResult()
        } catch (e: UserNotSpecifiedException) {
            Result(HttpStatusCode.BadRequest, null, e)
        } catch (e: TopRatedMoviesUnavailableException) {
            Result(HttpStatusCode.InternalServerError, null, e)
        }
    }

    fun getV1MovieWithId(requestCall: ApplicationCall): Result<MovieDetailJson> {
        val movieId = requestCall.parameters["id"]!!.let(::MovieId)
        val userId = requestCall.request.header(HEADER_USER_ID)?.let(::UserId)
        return try {
            movieDetailUsecase.detailsOf(movieId, userId)?.toJson()?.toOkResult()
                    ?: Result(HttpStatusCode.NotFound, null, null)
        } catch (e: UserNotSpecifiedException) {
            Result(HttpStatusCode.BadRequest, null, e)
        } catch (e: MovieDetailsUnavailableException) {
            Result(HttpStatusCode.InternalServerError, null, e)
        }
    }

    private fun <T> T.toOkResult(): Result<T> = Result(HttpStatusCode.OK, this)
}

fun MovieSummary.toJson() = MovieSummaryJson(this.id.value, this.title.value)
fun MovieSummaries.toJson() = this.map { summary -> summary.toJson() }.let(::MovieSummariesJson)

fun MovieReview.toJson() = ReviewJson(this.averageScore.value, this.count.value)
fun PersonalizedMovie.toJson() = ReviewedMovieJson(this.movieId.value, this.movieTitle.value, this.watched, this.movieReview.toJson())
fun PersonalizedMovies.toJson() = this.map { movie -> movie.toJson() }.let(::ReviewedMovieListJson)

fun Movie.toJson() = MovieDetailJson(this.id.value, this.title.value, this.overview.value, this.review.toJson())