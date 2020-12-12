package com.tater.gateway

import com.tater.AutoResetMock
import com.tater.domain.*
import com.tater.driver.MovieApi
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("MovieSummaryGateway")
class MovieSummaryGatewayTest: AutoResetMock {

    @InjectMockKs
    private lateinit var sut: MovieSummaryGateway

    @MockK
    private lateinit var movieApi: MovieApi

    @Nested
    @DisplayName("movieSummariesOf")
    inner class MovieSummariesOfTest {

        @Test
        fun `Return MovieSummaries of specified MovieIds`() {
            val movieIds = MovieIds(listOf(MovieId("movieId1"), MovieId("movieId2")))
            val expected = MovieSummaries(listOf(
                MovieSummary(MovieId("movieId1"), MovieTitle("title1")),
                MovieSummary(MovieId("movieId2"), MovieTitle("title2"))
            ))

            every { movieApi.getMovie("movieId1") } returns MovieApi.MovieJson("movieId1", "title1")
            every { movieApi.getMovie("movieId2") } returns MovieApi.MovieJson("movieId2", "title2")

            sut.movieSummariesOf(movieIds) shouldBeEqualTo expected

            verify(exactly = 1) { movieApi.getMovie("movieId1") }
            verify(exactly = 1) { movieApi.getMovie("movieId2") }
        }
    }
}