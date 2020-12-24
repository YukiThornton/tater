package com.tater.usecase

import com.tater.AutoResetMock
import com.tater.domain.*
import com.tater.port.MoviePort
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.amshove.kluent.any
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldThrow
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("RecommendationUsecase")
class RecommendationUsecaseTest: AutoResetMock {

    @InjectMockKs
    private lateinit var sut: RecommendationUsecase

    @MockK
    private lateinit var userIdChecker: UserIdChecker

    @MockK
    private lateinit var moviePort: MoviePort

    @Nested
    @DisplayName("recommendedMovies")
    inner class RecommendedMovies {

        @Nested
        @DisplayName("When enough movies are available")
        inner class WhenEnoughMoviesAreAvailable {

            private lateinit var actual: Movies
            private val userId = UserId("userId1")
            private val movies = mockk<Movies>("Movies returned from port")
            private val searchFilter = MovieSearchFilter.withMinimumReviewCount(ReviewCount(1000))
            private val sort = SortedBy.ReviewAverageDesc

            @BeforeEach
            fun setupAndExec() {
                every { userIdChecker.makeSureUserIdExists(userId) } returns any()
                every { moviePort.searchMovies(searchFilter, sort) } returns movies

                actual = sut.recommendedMovies(userId)
            }

            @Test
            fun `Checks userId with UserIdChecker`() {
                verify { userIdChecker.makeSureUserIdExists(userId) }
            }

            @Test
            fun `Gets movies with a filter`() {
                verify { moviePort.searchMovies(searchFilter, sort) }
            }

            @Test
            fun `Returns all movies retrieved from port`() {
                actual shouldBeEqualTo movies
            }
        }

        @Nested
        @DisplayName("When no movies are available")
        inner class WhenNoMoviesAreAvailable {

            private lateinit var actual: Movies

            @BeforeEach
            fun setupAndExec() {
                val userId = UserId("userId1")
                every { userIdChecker.makeSureUserIdExists(userId) } returns any()
                every { moviePort.searchMovies(any(), any()) } returns Movies(emptyList())

                actual = sut.recommendedMovies(userId)
            }

            @Test
            fun `Returns no movies`() {
                actual shouldBeEqualTo Movies(emptyList())
            }
        }

        @Nested
        @DisplayName("When UserIdChecker throws an exception")
        inner class WhenUserIdCheckerThrowsAnException {

            @BeforeEach
            fun setup() {
                every { userIdChecker.makeSureUserIdExists(null) } throws UserNotSpecifiedException("")
            }

            @Test
            fun `Throws a UserNotSpecifiedException`() {
                { sut.recommendedMovies(null) } shouldThrow UserNotSpecifiedException::class
                verify { userIdChecker.makeSureUserIdExists(null) }
            }
        }
    }
}