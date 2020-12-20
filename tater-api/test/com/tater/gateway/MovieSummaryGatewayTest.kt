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

        @Test
        fun `Return MovieSummary of specified MovieId`() {
            runBlocking {
                val movieId = MovieId("movieId1")
                val expected = MovieSummary(MovieId("movieId1"), MovieTitle("title1"))

                coEvery { movieApi.fetchMovie("movieId1") } returns MovieApi.MovieJson("movieId1", "title1")

                sut.fetchMovieSummaryOf(movieId) shouldBeEqualTo expected

                coVerify(exactly = 1) { movieApi.fetchMovie("movieId1") }
            }
        }

        @Test
        fun `Returns null when movieApi throws a NotFoundException`() {
            runBlocking {
                val movieId = MovieId("movieId1")

                coEvery { movieApi.fetchMovie("movieId1") } throws MovieApi.NotFoundException("", RuntimeException())

                sut.fetchMovieSummaryOf(movieId) shouldBeEqualTo null

                coVerify(exactly = 1) { movieApi.fetchMovie("movieId1") }
            }
        }

        @Test
        fun `Throws a UnavailableException when movieApi throws an error`() {
            runBlocking {
                val movieId = MovieId("movieId1")
                val errorFromMovieApi = mockk<Throwable>()
                val errorMessage = "Movie summary for movie(id=movieId1) unavailable"

                coEvery { movieApi.fetchMovie("movieId1") } throws errorFromMovieApi

                coInvoking { sut.fetchMovieSummaryOf(movieId) } shouldThrow MovieSummaryPort.UnavailableException::class withMessage errorMessage withCause Throwable::class

                coVerify(exactly = 1) { movieApi.fetchMovie("movieId1") }
            }
        }
    }
}