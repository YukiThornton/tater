package com.tater.usecase

import com.tater.AutoResetMock
import com.tater.domain.*
import com.tater.port.MovieSummaryPort
import com.tater.port.ViewingHistoryPort
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldThrow
import org.amshove.kluent.withCause
import org.amshove.kluent.withMessage
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("ViewingHistoryUsecase")
class ViewingHistoryUsecaseTest: AutoResetMock {

    @InjectMockKs
    private lateinit var sut: ViewingHistoryUsecase

    @MockK
    private lateinit var userIdChecker: UserIdChecker

    @MockK
    private lateinit var viewingHistoryPort: ViewingHistoryPort

    @MockK
    private lateinit var movieSummaryPort: MovieSummaryPort

    @Nested
    @DisplayName("allMoviesWatchedBy")
    inner class AllMoviesWatchedByTest {

        @Nested
        @DisplayName("When all movie summaries are unavailable")
        inner class WhenAllMovieSummariesAreUnavailable {

            private lateinit var actual: MovieSummaries
            private val userId = UserId("userId1")
            private val movieId1 = mockk<MovieId>()
            private val movieId2 = mockk<MovieId>()
            private val summary1 = mockk<MovieSummary>()
            private val summary2 = mockk<MovieSummary>()

            @BeforeEach
            fun setupAndExec() {
                val histories = mockk<ViewingHistories>()
                every { userIdChecker.makeSureUserIdExists(userId) } returns userId
                every { viewingHistoryPort.getViewingHistoriesFor(userId) } returns histories
                every { histories.watchedMovieIds } returns MovieIds(listOf(movieId1, movieId2))
                coEvery { movieSummaryPort.fetchMovieSummaryOf(movieId1) } returns summary1
                coEvery { movieSummaryPort.fetchMovieSummaryOf(movieId2) } returns summary2

                actual = sut.allMoviesWatchedBy(userId)
            }

            @Test
            fun `Checks userId with UserIdChecker`() {
                verify { userIdChecker.makeSureUserIdExists(userId) }
            }

            @Test
            fun `Gets all viewing histories for specified user`() {
                verify { viewingHistoryPort.getViewingHistoriesFor(userId) }
            }

            @Test
            fun `Fetches each movie summary of found viewing histories`() {
                coVerify { movieSummaryPort.fetchMovieSummaryOf(movieId1) }
                coVerify { movieSummaryPort.fetchMovieSummaryOf(movieId2) }
            }

            @Test
            fun `Returns all movie summaries for specified user`() {
                actual shouldBeEqualTo MovieSummaries(listOf(summary1, summary2))
            }
        }

        @Nested
        @DisplayName("When some movie summaries are available")
        inner class WhenSomeMovieSummariesAreAvailable {

            private lateinit var actual: MovieSummaries
            private val summary1 = mockk<MovieSummary>()
            private val summary3 = mockk<MovieSummary>()

            @BeforeEach
            fun setupAndExec() {
                val userId = UserId("userId1")
                val histories = mockk<ViewingHistories>()
                val movieId1 = mockk<MovieId>()
                val movieId2 = mockk<MovieId>()
                val movieId3 = mockk<MovieId>()
                every { userIdChecker.makeSureUserIdExists(userId) } returns userId
                every { viewingHistoryPort.getViewingHistoriesFor(userId) } returns histories
                every { histories.watchedMovieIds } returns MovieIds(listOf(movieId1, movieId2, movieId3))
                coEvery { movieSummaryPort.fetchMovieSummaryOf(movieId1) } returns summary1
                coEvery { movieSummaryPort.fetchMovieSummaryOf(movieId2) } returns null
                coEvery { movieSummaryPort.fetchMovieSummaryOf(movieId3) } returns summary3

                actual = sut.allMoviesWatchedBy(userId)
            }

            @Test
            fun `Returns only available movie summaries`() {
                actual shouldBeEqualTo MovieSummaries(listOf(summary1, summary3))
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
                { sut.allMoviesWatchedBy(null) } shouldThrow UserNotSpecifiedException::class
                verify { userIdChecker.makeSureUserIdExists(null) }
            }
        }

        @Nested
        @DisplayName("When viewingHistoryPort throws an UnavailableException")
        inner class WhenViewingHistoryPortThrowsAnUnavailableException {

            private val userId = UserId("userId1")

            @BeforeEach
            fun setup() {
                val errorFromPort = mockk<ViewingHistoryPort.UnavailableException>()
                every { userIdChecker.makeSureUserIdExists(userId) } returns userId
                every { viewingHistoryPort.getViewingHistoriesFor(userId) } throws errorFromPort
            }

            @Test
            fun `Throws a WatchedMoviesUnavailableException`() {
                val errorMessage = "Movies watched by user(id=userId1) are unavailable"
                { sut.allMoviesWatchedBy(userId) } shouldThrow WatchedMoviesUnavailableException::class withCause ViewingHistoryPort.UnavailableException::class withMessage errorMessage
            }
        }

        @Nested
        @DisplayName("When movieSummaryPort throws an UnavailableException")
        inner class WhenMovieSummaryPortThrowsAnUnavailableException {

            private val userId = UserId("userId1")

            @BeforeEach
            fun setup() {
                val histories = mockk<ViewingHistories>()
                val movieId1 = MovieId("movieId1")
                val movieId2 = MovieId("movieId2")
                every { userIdChecker.makeSureUserIdExists(userId) } returns userId
                every { viewingHistoryPort.getViewingHistoriesFor(userId) } returns histories
                every { histories.watchedMovieIds } returns MovieIds(listOf(movieId1, movieId2))
                coEvery { movieSummaryPort.fetchMovieSummaryOf(movieId1) } throws MovieSummaryPort.UnavailableException("error", Exception(""))
                coEvery { movieSummaryPort.fetchMovieSummaryOf(movieId2) } returns mockk();
            }

            @Test
            fun `Throws a WatchedMoviesUnavailableException when movieSummaryPort throws a UnavailableException`() {
                val errorMessage = "Movies(ids=[movieId1,movieId2]) watched by user(id=userId1) are unavailable"
                { sut.allMoviesWatchedBy(userId) } shouldThrow WatchedMoviesUnavailableException::class withCause MovieSummaryPort.UnavailableException::class withMessage errorMessage
            }
        }
    }
}