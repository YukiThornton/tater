package com.tater.domain

import com.tater.domain.attribute.MovieId
import com.tater.domain.attribute.MovieIds
import io.mockk.mockk
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("ViewingHistories")
class ViewingHistoriesTest {

    @Nested
    @DisplayName("watched")
    inner class Watched {

        @Test
        fun `Returns true if movieIds contains the given id`() {
            val sut = ViewingHistories(mockk(), MovieIds(listOf(MovieId("movieId1"), MovieId("movieId2"))))
            sut.watched(MovieId("movieId1")) shouldBeEqualTo true
        }

        @Test
        fun `Returns false if movieIds does not contain the given id`() {
            val sut = ViewingHistories(mockk(), MovieIds(listOf(MovieId("movieId1"), MovieId("movieId2"))))
            sut.watched(MovieId("movieId99")) shouldBeEqualTo false
        }
    }
}