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
        @DisplayName("When all movie summaries are available")
        inner class WhenAllMovieSummariesAreAvailable {

            private val theUserId = UserId("userId1")

            private val movieId1 = mockk<MovieId>()
            private val movieId2 = mockk<MovieId>()
            private val historiesForTheUser = ViewingHistories(theUserId, MovieIds(listOf(movieId1, movieId2)))
            private val summary1 = mockk<MovieSummary>()
            private val summary2 = mockk<MovieSummary>()

            @BeforeEach
            fun setup() {
                every { userIdChecker.makeSureUserIdExists(theUserId) } returns theUserId
                every { viewingHistoryPort.getViewingHistoriesFor(theUserId) } returns historiesForTheUser
                coEvery { movieSummaryPort.fetchMovieSummaryOf(movieId1) } returns summary1
                coEvery { movieSummaryPort.fetchMovieSummaryOf(movieId2) } returns summary2
            }

            @Test
            fun `Checks userId with UserIdChecker`() {
                sut.allMoviesWatchedBy(theUserId)

                verify { userIdChecker.makeSureUserIdExists(theUserId) }
            }

            @Test
            fun `Gets all viewing histories for specified user and then fetches each movie summary`() {
                sut.allMoviesWatchedBy(theUserId)

                verify(exactly = 1) { viewingHistoryPort.getViewingHistoriesFor(theUserId) }
                coVerify(exactly = 1) { movieSummaryPort.fetchMovieSummaryOf(movieId1) }
                coVerify(exactly = 1) { movieSummaryPort.fetchMovieSummaryOf(movieId2) }
            }

            @Test
            fun `Returns all movie summaries for specified user`() {
                val actual = sut.allMoviesWatchedBy(theUserId)

                actual shouldBeEqualTo MovieSummaries(listOf(summary1, summary2))
            }
        }

        @Nested
        @DisplayName("When some movie summaries are available")
        inner class WhenSomeMovieSummariesAreAvailable {

            private val theUserId = UserId("userId1")

            private val summary1 = mockk<MovieSummary>()
            private val summary3 = mockk<MovieSummary>()

            @BeforeEach
            fun setup() {
                val movieId1 = mockk<MovieId>()
                val movieId2 = mockk<MovieId>()
                val movieId3 = mockk<MovieId>()
                val historiesForTheUser = ViewingHistories(theUserId, MovieIds(listOf(movieId1, movieId2, movieId3)))
                every { userIdChecker.makeSureUserIdExists(theUserId) } returns theUserId
                every { viewingHistoryPort.getViewingHistoriesFor(theUserId) } returns historiesForTheUser
                coEvery { movieSummaryPort.fetchMovieSummaryOf(movieId1) } returns summary1
                coEvery { movieSummaryPort.fetchMovieSummaryOf(movieId2) } returns null
                coEvery { movieSummaryPort.fetchMovieSummaryOf(movieId3) } returns summary3

            }

            @Test
            fun `Returns only available movie summaries`() {
                val actual = sut.allMoviesWatchedBy(theUserId)

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
            }
        }

        @Nested
        @DisplayName("When viewingHistoryPort throws an UnavailableException")
        inner class WhenViewingHistoryPortThrowsAnUnavailableException {

            private val theUserId = UserId("userId1")

            @BeforeEach
            fun setup() {
                every { userIdChecker.makeSureUserIdExists(theUserId) } returns theUserId
                every { viewingHistoryPort.getViewingHistoriesFor(theUserId) } throws mockk<ViewingHistoryPort.UnavailableException>()
            }

            @Test
            fun `Throws a WatchedMoviesUnavailableException`() {
                val expectedException = WatchedMoviesUnavailableException::class
                val exceptionCause = ViewingHistoryPort.UnavailableException::class
                val exceptionMessage = "Movies watched by user(id=userId1) are unavailable"

                { sut.allMoviesWatchedBy(theUserId) } shouldThrow expectedException withCause exceptionCause withMessage exceptionMessage
            }
        }

        @Nested
        @DisplayName("When movieSummaryPort throws an UnavailableException")
        inner class WhenMovieSummaryPortThrowsAnUnavailableException {

            private val theUserId = UserId("userId1")

            @BeforeEach
            fun setup() {
                val movieId1 = MovieId("movieId1")
                val movieId2 = MovieId("movieId2")
                val histories = ViewingHistories(theUserId, MovieIds(listOf(movieId1, movieId2)))
                every { userIdChecker.makeSureUserIdExists(theUserId) } returns theUserId
                every { viewingHistoryPort.getViewingHistoriesFor(theUserId) } returns histories
                coEvery { movieSummaryPort.fetchMovieSummaryOf(movieId1) } throws MovieSummaryPort.UnavailableException("error", Exception(""))
                coEvery { movieSummaryPort.fetchMovieSummaryOf(movieId2) } returns mockk();
            }

            @Test
            fun `Throws a WatchedMoviesUnavailableException when movieSummaryPort throws a UnavailableException`() {
                val expectedException = WatchedMoviesUnavailableException::class
                val exceptionCause = MovieSummaryPort.UnavailableException::class
                val exceptionMessage = "Movies(ids=[movieId1,movieId2]) watched by user(id=userId1) are unavailable"

                { sut.allMoviesWatchedBy(theUserId) } shouldThrow expectedException withCause exceptionCause withMessage exceptionMessage
            }
        }
    }
}