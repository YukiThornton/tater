package com.tater.gateway

import com.tater.AutoResetMock
import com.tater.domain.*
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

            private var actual: MovieSummary? = null

            @BeforeEach
            fun setupAndExec() {
                coEvery { movieApi.fetchMovie("movieId1") } returns MovieApi.MovieJson("movieId1", "title1")

                runBlocking {
                    actual = sut.fetchMovieSummaryOf(MovieId("movieId1"))
                }
            }

            @Test
            fun `Returns MovieSummary of specified MovieId`() {
                actual shouldBeEqualTo MovieSummary(MovieId("movieId1"), MovieTitle("title1"))
            }

            @Test
            fun `Fetches movie from MovieApi`() {
                coVerify(exactly = 1) { movieApi.fetchMovie("movieId1") }
            }
        }

        @Nested
        @DisplayName("When movie does not exist")
        inner class WhenMovieDoesNotExist {

            private var actual: MovieSummary? = null

            @BeforeEach
            fun setupAndExec() {
                coEvery { movieApi.fetchMovie("movieId1") } throws MovieApi.NotFoundException("", RuntimeException())

                runBlocking {
                    actual = sut.fetchMovieSummaryOf(MovieId("movieId1"))
                }
            }

            @Test
            fun `Returns null`() {
                actual shouldBeEqualTo null
            }
        }

        @Nested
        @DisplayName("When movie api throws an error")
        inner class WhenMovieApiThrowsAnError {

            private val errorFromMovieApi = mockk<Throwable>()
            private val errorMessage = "Movie summary for movie(id=movieId1) unavailable"

            @BeforeEach
            fun setup() {
                coEvery { movieApi.fetchMovie("movieId1") } throws errorFromMovieApi
            }

            @Test
            fun `Throws a UnavailableException`() {
                runBlocking {
                    coInvoking{ sut.fetchMovieSummaryOf(MovieId("movieId1")) } shouldThrow MovieSummaryPort.UnavailableException::class withMessage errorMessage withCause Throwable::class
                }
            }
        }
    }
}