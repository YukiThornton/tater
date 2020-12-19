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
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("ViewingHistoryUsecase")
class ViewingHistoryUsecaseTest: AutoResetMock {

    @InjectMockKs
    private lateinit var sut: ViewingHistoryUsecase

    @MockK
    private lateinit var viewingHistoryPort: ViewingHistoryPort

    @MockK
    private lateinit var movieSummaryPort: MovieSummaryPort

    @Nested
    @DisplayName("allMoviesWatchedBy")
    inner class AllMoviesWatchedByTest {
        @Test
        fun `Returns MovieSummaries for specified user`() {

            val userId = UserId("userId1")
            val histories = mockk<ViewingHistories>()
            val movieId1 = mockk<MovieId>()
            val movieId2 = mockk<MovieId>()
            val movieIds = MovieIds(listOf(movieId1, movieId2))
            val summary1 = mockk<MovieSummary>()
            val summary2 = mockk<MovieSummary>()

            val expected = MovieSummaries(listOf(summary1, summary2))

            every { viewingHistoryPort.viewingHistoriesFor(userId) } returns histories
            every { histories.movieIds() } returns movieIds
            coEvery { movieSummaryPort.movieSummaryOf(movieId1) } returns summary1
            coEvery { movieSummaryPort.movieSummaryOf(movieId2) } returns summary2

            sut.allMoviesWatchedBy(userId) shouldBeEqualTo expected

            verify { viewingHistoryPort.viewingHistoriesFor(userId) }
            verify { histories.movieIds() }
            coVerify { movieSummaryPort.movieSummaryOf(movieId1) }
            coVerify { movieSummaryPort.movieSummaryOf(movieId2) }
        }

        @Test
        fun `Returns MovieSummaries skipping unavailable Movie Summaries`() {

            val userId = UserId("userId1")
            val histories = mockk<ViewingHistories>()
            val movieId1 = mockk<MovieId>()
            val movieId2 = mockk<MovieId>()
            val movieId3 = mockk<MovieId>()
            val movieIds = MovieIds(listOf(movieId1, movieId2, movieId3))
            val summary1 = mockk<MovieSummary>()
            val summary3 = mockk<MovieSummary>()

            val expected = MovieSummaries(listOf(summary1, summary3))

            every { viewingHistoryPort.viewingHistoriesFor(userId) } returns histories
            every { histories.movieIds() } returns movieIds
            coEvery { movieSummaryPort.movieSummaryOf(movieId1) } returns summary1
            coEvery { movieSummaryPort.movieSummaryOf(movieId2) } returns null
            coEvery { movieSummaryPort.movieSummaryOf(movieId3) } returns summary3

            sut.allMoviesWatchedBy(userId) shouldBeEqualTo expected

            verify { viewingHistoryPort.viewingHistoriesFor(userId) }
            verify { histories.movieIds() }
            coVerify { movieSummaryPort.movieSummaryOf(movieId1) }
            coVerify { movieSummaryPort.movieSummaryOf(movieId2) }
            coVerify { movieSummaryPort.movieSummaryOf(movieId3) }
        }

        @Test
        fun `Throws a UserNotSpecifiedException when UserId is null`() {
            { sut.allMoviesWatchedBy(null) } shouldThrow UserNotSpecifiedException::class
        }

        @Test
        fun `Throws a WatchedMoviesUnavailableException when viewingHistoryPort throws a UnavailableException`() {
            val userId = UserId("userId1")
            val errorFromPort = ViewingHistoryPort.UnavailableException("error", Exception(""))
            val errorMessage = "Movies watched by user(id=userId1) are unavailable"

            every { viewingHistoryPort.viewingHistoriesFor(userId) } throws errorFromPort

            { sut.allMoviesWatchedBy(userId) } shouldThrow WatchedMoviesUnavailableException::class withCause ViewingHistoryPort.UnavailableException::class withMessage errorMessage

            verify { viewingHistoryPort.viewingHistoriesFor(userId) }
        }

        @Test
        fun `Throws a WatchedMoviesUnavailableException when movieSummaryPort throws a UnavailableException`() {
            val userId = UserId("userId1")
            val histories = mockk<ViewingHistories>()
            val movieId1 = MovieId("movieId1")
            val movieId2 = MovieId("movieId2")
            val movieIds = MovieIds(listOf(movieId1, movieId2))
            val errorFromPort = MovieSummaryPort.UnavailableException("error", Exception(""))
            val errorMessage = "Movies(ids=[movieId1,movieId2]) watched by user(id=userId1) are unavailable"

            every { viewingHistoryPort.viewingHistoriesFor(userId) } returns histories
            every { histories.movieIds() } returns movieIds
            coEvery { movieSummaryPort.movieSummaryOf(movieId1) } throws errorFromPort
            coEvery { movieSummaryPort.movieSummaryOf(movieId2) } returns mockk();

            { sut.allMoviesWatchedBy(userId) } shouldThrow WatchedMoviesUnavailableException::class withCause MovieSummaryPort.UnavailableException::class withMessage errorMessage

            verify { viewingHistoryPort.viewingHistoriesFor(userId) }
            verify { histories.movieIds() }
            coVerify { movieSummaryPort.movieSummaryOf(movieId1) }
        }
    }
}