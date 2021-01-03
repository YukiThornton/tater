package com.tater.domain

import com.tater.domain.attribute.MovieId
import com.tater.domain.attribute.MovieTitle
import io.mockk.mockk
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("Movie")
class MovieTest {

    @Nested
    @DisplayName("summarize")
    inner class SummarizeTest {

        @Test
        fun `Returns a MovieSummary`() {
            val target = Movie(MovieId("id1"), MovieTitle("title1"), mockk(), mockk())
            val expected = MovieSummary(MovieId("id1"), MovieTitle("title1"))

            target.summarize() shouldBeEqualTo expected
        }
    }
}