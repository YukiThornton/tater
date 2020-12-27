package com.tater.usecase

import com.tater.AutoResetMock
import com.tater.domain.*
import com.tater.port.MoviePort
import com.tater.port.ViewingHistoryPort
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.amshove.kluent.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("MovieSearchUsecase")
class MovieSearchUsecaseTest: AutoResetMock {

    @InjectMockKs
    private lateinit var sut: MovieSearchUsecase

    @MockK
    private lateinit var userIdChecker: UserIdChecker

    @MockK
    private lateinit var moviePort: MoviePort

    @MockK
    private lateinit var viewingHistoryPort: ViewingHistoryPort

    @Nested
    @DisplayName("topRatedMovies")
    inner class TopRatedMovies {

        @Nested
        @DisplayName("When enough movies are available")
        inner class WhenEnoughMoviesAreAvailable {

            private lateinit var actual: PersonalizedMovies
            private val userId = UserId("userId1")
            private val movies = mockk<Movies>("Movies returned from port")
            private val searchFilter = MovieSearchFilter.withMinimumReviewCount(ReviewCount(1000))
            private val sort = SortedBy.ReviewAverageDesc
            private val viewingHistories = mockk<ViewingHistories>()
            private val personalizedMovies = mockk<PersonalizedMovies>()

            @BeforeEach
            fun setupAndExec() {
                mockkObject(PersonalizedMovies.Companion)
                every { userIdChecker.makeSureUserIdExists(userId) } returns userId
                every { moviePort.searchMovies(searchFilter, sort) } returns movies
                every { movies.isEmpty() } returns false
                every { viewingHistoryPort.getViewingHistoriesFor(userId) } returns viewingHistories
                every { PersonalizedMovies.from(movies, viewingHistories) } returns personalizedMovies

                actual = sut.topRatedMovies(userId)
            }

            @Test
            fun `Makes sure that userId exists with UserIdChecker`() {
                verify { userIdChecker.makeSureUserIdExists(userId) }
            }

            @Test
            fun `Gets necessary data from each port and create personalized movies out of them`() {
                verify(exactly = 1) { moviePort.searchMovies(searchFilter, sort) }
                verify(exactly = 1) { viewingHistoryPort.getViewingHistoriesFor(userId) }
                verify { PersonalizedMovies.from(movies, viewingHistories) }
            }

            @Test
            fun `Returns created data`() {
                actual shouldBeEqualTo personalizedMovies
            }
        }

        @Nested
        @DisplayName("When no movies are available")
        inner class WhenNoMoviesAreAvailable {

            private lateinit var actual: PersonalizedMovies

            @BeforeEach
            fun setupAndExec() {
                val userId = UserId("userId1")
                val movies = mockk<Movies>()
                every { userIdChecker.makeSureUserIdExists(userId) } returns userId
                every { moviePort.searchMovies(any(), any()) } returns movies
                every { movies.isEmpty() } returns true

                actual = sut.topRatedMovies(userId)
            }

            @Test
            fun `Gets movies but does not get viewing histories`() {
                verify(exactly = 1) { moviePort.searchMovies(any(), any()) }
                verify(exactly = 0) { viewingHistoryPort.getViewingHistoriesFor(any()) }
            }

            @Test
            fun `Returns no movies`() {
                actual shouldBeEqualTo PersonalizedMovies(emptyList())
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
            fun `Throws the same UserNotSpecifiedException`() {
                { sut.topRatedMovies(null) } shouldThrow UserNotSpecifiedException::class
                verify { userIdChecker.makeSureUserIdExists(null) }
            }
        }

        @Nested
        @DisplayName("When movie port throws an exception")
        inner class WhenMoviePortThrowsAnException {

            private val userId = UserId("userId1")

            @BeforeEach
            fun setup() {
                every { userIdChecker.makeSureUserIdExists(userId) } returns userId
                every { moviePort.searchMovies(any(), any()) } throws MoviePort.SearchUnavailableException("", null)
            }

            @Test
            fun `Throws a TopRatedMoviesUnavailableException`() {
                val errorMessage = "Top rated movies for user(id=userId1) are unavailable"
                { sut.topRatedMovies(userId) } shouldThrow TopRatedMoviesUnavailableException::class withCause MoviePort.SearchUnavailableException::class withMessage errorMessage
                verify(exactly = 1) { moviePort.searchMovies(any(), any()) }
            }
        }

        @Nested
        @DisplayName("When viewing history port throws an exception")
        inner class WhenViewingHistoryPortThrowsAnException {

            private val userId = UserId("userId1")

            @BeforeEach
            fun setup() {
                val movies = mockk<Movies>()
                every { userIdChecker.makeSureUserIdExists(userId) } returns userId
                every { moviePort.searchMovies(any(), any()) } returns movies
                every { movies.isEmpty() } returns false
                every { viewingHistoryPort.getViewingHistoriesFor(userId) } throws ViewingHistoryPort.UnavailableException("", null)
            }

            @Test
            fun `Throws a TopRatedMoviesUnavailableException`() {
                val errorMessage = "Top rated movies for user(id=userId1) are unavailable"
                { sut.topRatedMovies(userId) } shouldThrow TopRatedMoviesUnavailableException::class withCause ViewingHistoryPort.UnavailableException::class withMessage errorMessage
                verify(exactly = 1) { viewingHistoryPort.getViewingHistoriesFor(userId) }
            }
        }
    }
}