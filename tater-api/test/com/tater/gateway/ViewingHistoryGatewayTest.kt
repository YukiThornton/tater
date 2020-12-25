package com.tater.gateway

import com.tater.AutoResetMock
import com.tater.domain.*
import com.tater.driver.TaterDb
import com.tater.port.ViewingHistoryPort
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldThrow
import org.amshove.kluent.withCause
import org.amshove.kluent.withMessage
import org.junit.jupiter.api.BeforeEach
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
    @DisplayName("getViewingHistoriesFor")
    inner class GetViewingHistoriesForTest {

        @Nested
        @DisplayName("When Tater DB returns viewing histories of specified user")
        inner class WhenTaterDbReturnsViewingHistories {

            private lateinit var actual: ViewingHistories
            private val userId = UserId("userId1")
            private val historyData1 = mockk<TaterDb.ViewingHistoryDataset>()
            private val historyData2 = mockk<TaterDb.ViewingHistoryDataset>()
            private val datasets = listOf(historyData1, historyData2)

            @BeforeEach
            fun setupAndExec() {
                every { taterDb.selectViewingHistoriesByUserId("userId1") } returns datasets
                every { historyData1.movieId } returns "movieId1"
                every { historyData2.movieId } returns "movieId2"

                actual = sut.getViewingHistoriesFor(userId)
            }

            @Test
            fun `Gets ViewingHistories from Tater DB`() {
                verify(exactly = 1) { taterDb.selectViewingHistoriesByUserId("userId1") }
            }

            @Test
            fun `Gets movieId from data retrieved from Tater DB`() {
                verify { historyData1.movieId }
                verify { historyData2.movieId }
            }

            @Test
            fun `Returns ViewingHistories of specified user`() {
                actual shouldBeEqualTo ViewingHistories(userId, MovieIds(listOf(MovieId("movieId1"), MovieId("movieId2"))))
            }
        }

        @Nested
        @DisplayName("When Tater DB throws any error")
        inner class WhenTaterDbThrowsError {
            private val errorFromClient = mockk<Throwable>()
            private val errorMessage = "Viewing history for user(id=userId1) unavailable"

            @BeforeEach
            fun setup() {
                every { taterDb.selectViewingHistoriesByUserId("userId1") } throws errorFromClient
            }

            @Test
            fun `Throws a UnavailableException`() {
                val sutFunc = { sut.getViewingHistoriesFor(UserId("userId1")) }
                sutFunc shouldThrow ViewingHistoryPort.UnavailableException::class withCause Throwable::class withMessage errorMessage
            }
        }
    }
}