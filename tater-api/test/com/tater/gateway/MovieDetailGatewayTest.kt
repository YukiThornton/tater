package com.tater.gateway

import com.tater.AutoResetMock
import com.tater.domain.*
import com.tater.driver.MovieApi
import com.tater.port.MovieDetailPort
import com.tater.port.MoviePort
import com.tater.usecase.MovieDetailsUnavailableException
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldThrow
import org.amshove.kluent.withCause
import org.amshove.kluent.withMessage
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.lang.RuntimeException

class MovieDetailGatewayTest: AutoResetMock {

    @InjectMockKs
    private lateinit var sut: MovieDetailGateway

    @MockK
    private lateinit var movieApi: MovieApi

    @Nested
    @DisplayName("getDetailsOf")
    inner class GetDetailsOf {

        @Nested
        @DisplayName("When specified movie exist and succeeds to get the data")
        inner class WhenSucceeds {

            @BeforeEach
            fun setup() {
                every { movieApi.getMovie("movieId1") } returns MovieApi.MovieDetailJson(
                        "movieId1",
                        "title1",
                        "overview1",
                        5.6,
                        1000
                )
            }

            @Test
            fun `Gets a movie from MovieApi`() {
                sut.getDetailsOf(MovieId("movieId1"))

                verify(exactly = 1) { movieApi.getMovie("movieId1") }
            }

            @Test
            fun `Returns a MovieDetails`() {
                val actual = sut.getDetailsOf(MovieId("movieId1"))

                actual shouldBeEqualTo MovieDetails(
                        MovieId("movieId1"),
                        MovieTitle("title1"),
                        MovieOverview("overview1"),
                        MovieReview(AverageScore(5.6), ReviewCount(1000))
                )
            }
        }

        @Nested
        @DisplayName("When movie api throws a NotFoundException")
        inner class WhenMovieApiThrowsANotFoundException {

            private val notFoundException = MovieApi.NotFoundException("", RuntimeException(""))

            @BeforeEach
            fun setup() {
                every { movieApi.getMovie("movieId1") } throws notFoundException
            }

            @Test
            fun `Returns null`() {
                val actual = sut.getDetailsOf(MovieId("movieId1"))

                verify(exactly = 1) { movieApi.getMovie("movieId1") }
                actual shouldBeEqualTo null
            }
        }

        @Nested
        @DisplayName("When movie api throws any exception")
        inner class WhenMovieApiThrowsAnyException {

            private val anyException = Throwable("", RuntimeException(""))

            @BeforeEach
            fun setup() {
                every { movieApi.getMovie("movieId1") } throws anyException
            }

            @Test
            fun `Throws an UnavailableException`() {
                val expectedException = MovieDetailPort.UnavailableException::class
                val exceptionCause = Throwable::class
                val exceptionMessage = "Movie(id=movieId1) is unavailable"

                { sut.getDetailsOf(MovieId("movieId1")) } shouldThrow expectedException withCause exceptionCause withMessage exceptionMessage

                verify(exactly = 1) { movieApi.getMovie("movieId1") }
            }
        }
    }
}