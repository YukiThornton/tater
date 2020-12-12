package com.tater.domain

import io.mockk.mockk
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested

@DisplayName("ViewingHistory")
class ViewingHistoriesTest {

    @Nested
    @DisplayName("movieIds")
    inner class MovieIdsTest {
        @Test
        fun `Returns MovieIds of all histories`() {
            val sut = ViewingHistories(
                listOf(
                    ViewingHistory(mockk(), MovieId("movieId1")),
                    ViewingHistory(mockk(), MovieId("movieId2"))
                )
            )

            val expected = MovieIds(listOf(
                MovieId("movieId1"),
                MovieId("movieId2")
            ))
            sut.movieIds() shouldBeEqualTo expected
        }
    }
}