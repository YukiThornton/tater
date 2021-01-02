package com.tater.gateway

import com.tater.AutoResetMock
import com.tater.domain.*
import com.tater.driver.MovieApi
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

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
                        MovieReview(AverageScore(5.6), ReviewCount(1000))
                )
            }
        }
    }
}