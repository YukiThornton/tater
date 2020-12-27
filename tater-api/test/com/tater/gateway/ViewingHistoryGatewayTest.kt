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

            private val theUserId = UserId("userId1")

            private val historyData1 = mockk<TaterDb.ViewingHistoryDataset>()
            private val historyData2 = mockk<TaterDb.ViewingHistoryDataset>()
            private val datasets = listOf(historyData1, historyData2)

            @BeforeEach
            fun setup() {
                every { taterDb.selectViewingHistoriesByUserId("userId1") } returns datasets
                every { historyData1.movieId } returns "movieId1"
                every { historyData2.movieId } returns "movieId2"
            }

            @Test
            fun `Gets ViewingHistories from Tater DB`() {
                sut.getViewingHistoriesFor(theUserId)

                verify(exactly = 1) { taterDb.selectViewingHistoriesByUserId("userId1") }
            }

            @Test
            fun `Returns ViewingHistories of specified user`() {
                val actual = sut.getViewingHistoriesFor(theUserId)

                actual shouldBeEqualTo ViewingHistories(theUserId, MovieIds(listOf(MovieId("movieId1"), MovieId("movieId2"))))
            }
        }

        @Nested
        @DisplayName("When Tater DB throws any error")
        inner class WhenTaterDbThrowsError {

            @BeforeEach
            fun setup() {
                every { taterDb.selectViewingHistoriesByUserId("userId1") } throws mockk()
            }

            @Test
            fun `Throws a UnavailableException`() {
                val expectedException = ViewingHistoryPort.UnavailableException::class
                val exceptionCause = Throwable::class
                val exceptionMessage = "Viewing history for user(id=userId1) unavailable"

                { sut.getViewingHistoriesFor(UserId("userId1")) } shouldThrow expectedException withCause exceptionCause withMessage exceptionMessage
            }
        }
    }
}