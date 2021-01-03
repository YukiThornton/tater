package com.tater.usecase

import com.tater.AutoResetMock
import com.tater.domain.*
import com.tater.domain.attribute.MovieId
import com.tater.domain.attribute.MovieIds
import com.tater.port.MoviePort
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
    private lateinit var moviePort: MoviePort

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
            private val movie1 = mockk<Movie>()
            private val movie2 = mockk<Movie>()
            private val summary1 = mockk<MovieSummary>()
            private val summary2 = mockk<MovieSummary>()

            @BeforeEach
            fun setup() {
                every { userIdChecker.makeSureUserIdExists(theUserId) } returns theUserId
                every { viewingHistoryPort.getViewingHistoriesFor(theUserId) } returns historiesForTheUser
                coEvery { moviePort.fetchMovieOf(movieId1) } returns movie1
                coEvery { moviePort.fetchMovieOf(movieId2) } returns movie2
                every { movie1.summarize() } returns summary1
                every { movie2.summarize() } returns summary2
            }

            @Test
            fun `Checks userId with UserIdChecker`() {
                sut.allMoviesWatchedBy(theUserId)

                verify { userIdChecker.makeSureUserIdExists(theUserId) }
            }

            @Test
            fun `Gets all viewing histories for specified user and then fetches each movie to create summaries`() {
                sut.allMoviesWatchedBy(theUserId)

                verify(exactly = 1) { viewingHistoryPort.getViewingHistoriesFor(theUserId) }
                coVerify(exactly = 1) { moviePort.fetchMovieOf(movieId1) }
                coVerify(exactly = 1) { moviePort.fetchMovieOf(movieId2) }
                verify { movie1.summarize() }
                verify { movie2.summarize() }
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

            private val movie1 = mockk<Movie>()
            private val movie3 = mockk<Movie>()
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
                coEvery { moviePort.fetchMovieOf(movieId1) } returns movie1
                coEvery { moviePort.fetchMovieOf(movieId2) } returns null
                coEvery { moviePort.fetchMovieOf(movieId3) } returns movie3
                every { movie1.summarize() } returns summary1
                every { movie3.summarize() } returns summary3
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
        @DisplayName("When moviePort throws an UnavailableException")
        inner class WhenMoviePortThrowsAnUnavailableException {

            private val theUserId = UserId("userId1")

            @BeforeEach
            fun setup() {
                val movieId1 = MovieId("movieId1")
                val movieId2 = MovieId("movieId2")
                val histories = ViewingHistories(theUserId, MovieIds(listOf(movieId1, movieId2)))
                every { userIdChecker.makeSureUserIdExists(theUserId) } returns theUserId
                every { viewingHistoryPort.getViewingHistoriesFor(theUserId) } returns histories
                coEvery { moviePort.fetchMovieOf(movieId1) } throws MoviePort.UnavailableException(Exception(""), "error")
                coEvery { moviePort.fetchMovieOf(movieId2) } returns mockk();
            }

            @Test
            fun `Throws a WatchedMoviesUnavailableException when moviePort throws a UnavailableException`() {
                val expectedException = WatchedMoviesUnavailableException::class
                val exceptionCause = MoviePort.UnavailableException::class
                val exceptionMessage = "Movies(ids=[movieId1,movieId2]) watched by user(id=userId1) are unavailable"

                { sut.allMoviesWatchedBy(theUserId) } shouldThrow expectedException withCause exceptionCause withMessage exceptionMessage
            }
        }
    }
}