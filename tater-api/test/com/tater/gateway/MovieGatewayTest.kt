package com.tater.gateway

import com.tater.AutoResetMock
import com.tater.domain.*
import com.tater.domain.attribute.*
import com.tater.driver.MovieApi
import com.tater.port.MoviePort
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.lang.RuntimeException

class MovieGatewayTest: AutoResetMock {

    @InjectMockKs
    private lateinit var sut: MovieGateway

    @MockK
    private lateinit var movieApi: MovieApi

    @Nested
    @DisplayName("getMovieOf")
    inner class GetMovieOf {

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
                sut.getMovieOf(MovieId("movieId1"))

                verify(exactly = 1) { movieApi.getMovie("movieId1") }
            }

            @Test
            fun `Returns a Movie`() {
                val actual = sut.getMovieOf(MovieId("movieId1"))

                actual shouldBeEqualTo Movie(
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
                val actual = sut.getMovieOf(MovieId("movieId1"))

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
                val expectedException = MoviePort.UnavailableException::class
                val exceptionCause = Throwable::class
                val exceptionMessage = "Movie(id=movieId1) is unavailable"

                { sut.getMovieOf(MovieId("movieId1")) } shouldThrow expectedException withCause exceptionCause withMessage exceptionMessage

                verify(exactly = 1) { movieApi.getMovie("movieId1") }
            }
        }
    }

    @Nested
    @DisplayName("fetchMovieOf")
    inner class FetchMovieOfTest {

        @Nested
        @DisplayName("When movie exists")
        inner class WhenMovieExists {

            @BeforeEach
            fun setup() {
                coEvery { movieApi.fetchMovie("movieId1") } returns MovieApi.MovieDetailJson("movieId1", "title1", "overview1", 5.6, 1000)
            }

            @Test
            fun `Returns MovieSummary of specified MovieId`() {
                runBlocking {
                    val actual = sut.fetchMovieOf(MovieId("movieId1"))

                    actual shouldBeEqualTo Movie(
                            MovieId("movieId1"),
                            MovieTitle("title1"),
                            MovieOverview("overview1"),
                            MovieReview(AverageScore(5.6), ReviewCount(1000))
                    )
                }
            }

            @Test
            fun `Fetches movie from MovieApi`() {
                runBlocking {
                    sut.fetchMovieOf(MovieId("movieId1"))
                }

                coVerify(exactly = 1) { movieApi.fetchMovie("movieId1") }
            }
        }

        @Nested
        @DisplayName("When movie does not exist")
        inner class WhenMovieDoesNotExist {

            @BeforeEach
            fun setup() {
                coEvery { movieApi.fetchMovie("movieId1") } throws MovieApi.NotFoundException("", RuntimeException())
            }

            @Test
            fun `Returns null`() {
                runBlocking {
                    val actual = sut.fetchMovieOf(MovieId("movieId1"))

                    actual shouldBeEqualTo null
                }
            }
        }

        @Nested
        @DisplayName("When movie api throws an error")
        inner class WhenMovieApiThrowsAnError {

            private val errorFromMovieApi = mockk<Throwable>()

            @BeforeEach
            fun setup() {
                coEvery { movieApi.fetchMovie("movieId1") } throws errorFromMovieApi
            }

            @Test
            fun `Throws a UnavailableException`() {
                val expectedException = MoviePort.UnavailableException::class
                val exceptionCause = Throwable::class
                val exceptionMessage = "Movie(id=movieId1) is unavailable"

                runBlocking {
                    coInvoking{ sut.fetchMovieOf(MovieId("movieId1")) } shouldThrow expectedException withMessage exceptionMessage withCause exceptionCause
                }
            }
        }
    }
}