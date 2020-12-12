package com.tater.gateway

import com.tater.AutoResetMock
import com.tater.domain.MovieId
import com.tater.domain.UserId
import com.tater.domain.ViewingHistories
import com.tater.domain.ViewingHistory
import com.tater.driver.TaterDb
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested

@DisplayName("ViewingHistoryGateway")
class ViewingHistoryGatewayTest: AutoResetMock {

    @InjectMockKs
    private lateinit var sut: ViewingHistoryGateway

    @MockK
    private lateinit var taterDb: TaterDb

    @Nested
    @DisplayName("viewingHistoriesFor")
    inner class ViewingHistoriesForTest {

        @Test
        fun `Returns ViewingHistories of specified user using db client`() {
            val userId = UserId("userId1")

            val expected = ViewingHistories(listOf(
                ViewingHistory(userId, MovieId("movieId1")),
                ViewingHistory(userId, MovieId("movieId2"))
            ))

            val historyData1 = mockk<TaterDb.ViewingHistoryDataset>()
            val historyData2 = mockk<TaterDb.ViewingHistoryDataset>()
            val datasets = listOf(historyData1, historyData2)

            every { taterDb.selectViewingHistoriesByUserId("userId1") } returns datasets
            every { historyData1.userId } returns "userId1"
            every { historyData1.movieId } returns "movieId1"
            every { historyData2.userId } returns "userId1"
            every { historyData2.movieId } returns "movieId2"

            sut.viewingHistoriesFor(userId) shouldBeEqualTo expected

            verify(exactly = 1) { taterDb.selectViewingHistoriesByUserId("userId1") }
            verify { historyData1.movieId }
            verify { historyData2.movieId }
        }
    }
}