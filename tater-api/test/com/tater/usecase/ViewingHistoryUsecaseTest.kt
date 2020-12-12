package com.tater.usecase

import com.tater.AutoResetMock
import com.tater.domain.MovieIds
import com.tater.domain.MovieSummaries
import com.tater.domain.UserId
import com.tater.domain.ViewingHistories
import com.tater.port.MovieSummaryPort
import com.tater.port.ViewingHistoryPort
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.amshove.kluent.shouldBeEqualTo
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
            val movieIds = mockk<MovieIds>()

            val expected = mockk<MovieSummaries>()

            every { viewingHistoryPort.viewingHistoriesFor(userId) } returns histories
            every { histories.movieIds() } returns movieIds
            every { movieSummaryPort.movieSummariesOf(movieIds) } returns expected

            sut.allMoviesWatchedBy(userId) shouldBeEqualTo expected

            verify { viewingHistoryPort.viewingHistoriesFor(userId) }
            verify { histories.movieIds() }
            verify { movieSummaryPort.movieSummariesOf(movieIds) }

        }
    }
}