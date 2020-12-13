package com.tater.gateway

import com.tater.AutoResetMock
import com.tater.domain.*
import com.tater.driver.MovieApi
import com.tater.port.MovieSummaryPort
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
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
    @DisplayName("movieSummaryOf")
    inner class MovieSummaryOfTest {

        @Test
        fun `Return MovieSummary of specified MovieId`() {
            val movieId = MovieId("movieId1")
            val expected = MovieSummary(MovieId("movieId1"), MovieTitle("title1"))

            every { movieApi.getMovie("movieId1") } returns MovieApi.MovieJson("movieId1", "title1")

            sut.movieSummaryOf(movieId) shouldBeEqualTo expected

            verify(exactly = 1) { movieApi.getMovie("movieId1") }
        }

        @Test
        fun `Returns null when movieApi throws a NotFoundException`() {
            val movieId = MovieId("movieId1")

            every { movieApi.getMovie("movieId1") } throws MovieApi.NotFoundException("", RuntimeException())

            sut.movieSummaryOf(movieId) shouldBeEqualTo null

            verify(exactly = 1) { movieApi.getMovie("movieId1") }
        }

        @Test
        fun `Throws a UnavailableException when movieApi throws an error`() {
            val movieId = MovieId("movieId1")
            val errorFromMovieApi = mockk<Throwable>()
            val errorMessage = "Movie summary for movie(id=movieId1) unavailable"

            every { movieApi.getMovie("movieId1") } throws errorFromMovieApi

            { sut.movieSummaryOf(movieId) } shouldThrow MovieSummaryPort.UnavailableException::class withMessage errorMessage withCause Throwable::class

            verify(exactly = 1) { movieApi.getMovie("movieId1") }
        }
    }
}