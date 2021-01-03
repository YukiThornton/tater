package com.tater.gateway

import com.tater.AutoResetMock
import com.tater.domain.*
import com.tater.domain.attribute.MovieId
import com.tater.domain.attribute.MovieTitle
import com.tater.driver.MovieApi
import com.tater.port.MovieSummaryPort
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

@DisplayName("MovieSummaryGateway")
class MovieSummaryGatewayTest: AutoResetMock {

    @InjectMockKs
    private lateinit var sut: MovieSummaryGateway

    @MockK
    private lateinit var movieApi: MovieApi

    @Nested
    @DisplayName("fetchMovieSummaryOf")
    inner class FetchMovieSummaryOfTest {

        @Nested
        @DisplayName("When movie exists")
        inner class WhenMovieExists {

            @BeforeEach
            fun setup() {
                coEvery { movieApi.fetchMovie("movieId1") } returns MovieApi.MovieDetailJson("movieId1", "title1", "", 0.0, 0)
            }

            @Test
            fun `Returns MovieSummary of specified MovieId`() {
                runBlocking {
                    val actual = sut.fetchMovieSummaryOf(MovieId("movieId1"))

                    actual shouldBeEqualTo MovieSummary(MovieId("movieId1"), MovieTitle("title1"))
                }
            }

            @Test
            fun `Fetches movie from MovieApi`() {
                runBlocking {
                    sut.fetchMovieSummaryOf(MovieId("movieId1"))
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
                    val actual = sut.fetchMovieSummaryOf(MovieId("movieId1"))

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
                val expectedException = MovieSummaryPort.UnavailableException::class
                val exceptionCause = Throwable::class
                val exceptionMessage = "Movie summary for movie(id=movieId1) unavailable"

                runBlocking {
                    coInvoking{ sut.fetchMovieSummaryOf(MovieId("movieId1")) } shouldThrow expectedException withMessage exceptionMessage withCause exceptionCause
                }
            }
        }
    }
}