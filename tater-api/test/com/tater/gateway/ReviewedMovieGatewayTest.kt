package com.tater.gateway

import com.tater.AutoResetMock
import com.tater.domain.*
import com.tater.driver.MovieApi
import com.tater.port.ReviewedMoviePort
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.amshove.kluent.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("ReviewedMovieGateway")
class ReviewedMovieGatewayTest: AutoResetMock {

    @InjectMockKs
    private lateinit var sut: ReviewedMovieGateway

    @MockK
    private lateinit var movieApi: MovieApi

    @Nested
    @DisplayName("searchMovies")
    inner class SearchMoviesTest {

        @Nested
        @DisplayName("When movies exist")
        inner class WhenMoviesExist {

            private val theFilter = mockk<MovieSearchFilter>()
            private val theSortKey = SortedBy.ReviewAverageDesc

            private val conditions = mapOf(
                    "sort_by" to "vote_average.desc",
                    "vote_count.gte" to 1234,
                    "include_adult" to false,
                    "include_video" to true,
            )

            @BeforeEach
            fun setup() {
                every { theFilter.minReviewCount() } returns 1234
                every { theFilter.includeAdult() } returns false
                every { theFilter.includeVideo() } returns true
                every { movieApi.searchMovies(conditions) } returns MovieApi.MovieListJson(listOf(
                        MovieApi.MovieJson("id1", "title1", 5.6, 1000),
                        MovieApi.MovieJson("id2", "title2", 5.5, 1200),
                        MovieApi.MovieJson("id3", "title3", 5.4, 900),
                ))
            }

            @Test
            fun `Queries filter for condition values to call API with the condition`() {
                sut.searchMovies(theFilter, theSortKey)

                verify { theFilter.minReviewCount() }
                verify { theFilter.includeAdult() }
                verify { theFilter.includeVideo() }
                verify(exactly = 1) { movieApi.searchMovies(conditions) }
            }

            @Test
            fun `Returns found movies`() {
                val actual = sut.searchMovies(theFilter, theSortKey)

                actual shouldBeEqualTo ReviewedMovies(listOf(
                        ReviewedMovie(MovieId("id1"), MovieTitle("title1"), MovieReview(AverageScore(5.6), ReviewCount(1000))),
                        ReviewedMovie(MovieId("id2"), MovieTitle("title2"), MovieReview(AverageScore(5.5), ReviewCount(1200))),
                        ReviewedMovie(MovieId("id3"), MovieTitle("title3"), MovieReview(AverageScore(5.4), ReviewCount(900))),
                ))
            }
        }

        @Nested
        @DisplayName("When movie api throws an error")
        inner class WhenMovieApiThrowsAnError {

            private val theFilter = mockk<MovieSearchFilter>()
            private val theSortKey = mockk<SortedBy>()

            private val anyExceptionFromApi = mockk<Throwable>()

            @BeforeEach
            fun setup() {
                every { theFilter.minReviewCount() } returns 1234
                every { theFilter.includeAdult() } returns false
                every { theFilter.includeVideo() } returns true
                every { movieApi.searchMovies(any()) } throws anyExceptionFromApi
            }

            @Test
            fun `Throws a SearchUnavailableException`() {
                val expectedException = ReviewedMoviePort.SearchUnavailableException::class
                val exceptionCause = Throwable::class
                val message = "MovieApi's movie search functionality is not available"

                { sut.searchMovies(theFilter, theSortKey) } shouldThrow expectedException withCause exceptionCause withMessage message
            }
        }
    }
}