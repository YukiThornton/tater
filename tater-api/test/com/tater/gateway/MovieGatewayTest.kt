package com.tater.gateway

import com.tater.AutoResetMock
import com.tater.domain.*
import com.tater.driver.MovieApi
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("MovieGateway")
class MovieGatewayTest: AutoResetMock {

    @InjectMockKs
    private lateinit var sut: MovieGateway

    @MockK
    private lateinit var movieApi: MovieApi

    @Nested
    @DisplayName("searchMovies")
    inner class SearchMoviesTest {

        @Nested
        @DisplayName("When movies exist")
        inner class WhenMoviesExist {

            private lateinit var actual: Movies
            private val filter = mockk<MovieSearchFilter>()
            private val conditions = mapOf(
                    "sort_by" to "vote_average.desc",
                    "vote_count.gte" to 1234,
                    "include_adult" to false,
                    "include_video" to true,
            )

            @BeforeEach
            fun setupAndExec() {
                every { filter.minReviewCount() } returns 1234
                every { filter.includeAdult() } returns false
                every { filter.includeVideo() } returns true
                every { movieApi.searchMovies(conditions) } returns MovieApi.MovieListJson(listOf(
                        MovieApi.MovieJson("id1", "title1", 5.6, 1000),
                        MovieApi.MovieJson("id2", "title2", 5.5, 1200),
                        MovieApi.MovieJson("id3", "title3", 5.4, 900),
                ))

                actual = sut.searchMovies(filter, SortedBy.ReviewAverageDesc)
            }

            @Test
            fun `Queries filter for condition values`() {
                verify { filter.minReviewCount() }
                verify { filter.includeAdult() }
                verify { filter.includeVideo() }
            }

            @Test
            fun `Calls API method with the created conditions`() {
                verify { movieApi.searchMovies(conditions) }
            }

            @Test
            fun `Returns found movies`() {
                actual shouldBeEqualTo Movies(listOf(
                        Movie(MovieId("id1"), MovieTitle("title1"), MovieReview(AverageScore(5.6), ReviewCount(1000))),
                        Movie(MovieId("id2"), MovieTitle("title2"), MovieReview(AverageScore(5.5), ReviewCount(1200))),
                        Movie(MovieId("id3"), MovieTitle("title3"), MovieReview(AverageScore(5.4), ReviewCount(900))),
                ))
            }
        }
    }
}