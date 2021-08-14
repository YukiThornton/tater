package com.tater.rest

import com.tater.domain.*
import com.tater.domain.attribute.MovieId
import com.tater.domain.attribute.MovieReview
import com.tater.usecase.*
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*

class HttpRequestExecutor(
        private val jsonConverter: JsonConverter,
        private val viewingHistoryUsecase: ViewingHistoryUsecase,
        private val movieSearchUsecase: MovieSearchUsecase,
        private val movieAcquisitionUsecase: MovieAcquisitionUsecase,
) {

    companion object {
        private const val HEADER_USER_ID = "tater-user-id"
    }

    data class Result<T>(val responseStatus: HttpStatusCode, val responseBody: T? = null, val error: Throwable? = null)

    fun getV1Watched(requestCall: ApplicationCall): Result<MovieSummariesJson> {
        val userId = requestCall.request.header(HEADER_USER_ID)?.let(::UserId)
        return try {
            viewingHistoryUsecase.allMoviesWatchedBy(userId)
                    .let { jsonConverter.toMovieSummariesJson(it) }
                    .toOkResult()
        } catch (e: UserNotSpecifiedException) {
            Result(HttpStatusCode.BadRequest, null, e)
        } catch (e: WatchedMoviesUnavailableException) {
            Result(HttpStatusCode.InternalServerError, null, e)
        }
    }

    fun getV1TopRated(request: ApplicationCall): Result<ReviewedMovieListJson> {
        val userId = request.request.header(HEADER_USER_ID)?.let(::UserId)
        return try {
            movieSearchUsecase.topRatedMovies(userId)
                    .let { jsonConverter.toReviewedMovieListJson(it) }
                    .toOkResult()
        } catch (e: UserNotSpecifiedException) {
            Result(HttpStatusCode.BadRequest, null, e)
        } catch (e: TopRatedMoviesUnavailableException) {
            Result(HttpStatusCode.InternalServerError, null, e)
        }
    }

    fun getV1MovieWithId(requestCall: ApplicationCall): Result<MovieJson> {
        val movieId = requestCall.parameters["id"]!!.let(::MovieId)
        val userId = requestCall.request.header(HEADER_USER_ID)?.let(::UserId)
        return try {
            movieAcquisitionUsecase.getMovieOf(movieId, userId)
                    ?.let { jsonConverter.toMovieJson(it) }
                    ?.toOkResult()
                    ?: Result(HttpStatusCode.NotFound, null, null)
        } catch (e: UserNotSpecifiedException) {
            Result(HttpStatusCode.BadRequest, null, e)
        } catch (e: MovieUnavailableException) {
            Result(HttpStatusCode.InternalServerError, null, e)
        }
    }

    private fun <T> T.toOkResult(): Result<T> = Result(HttpStatusCode.OK, this)
}
