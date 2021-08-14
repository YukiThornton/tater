package com.tater.domain

import com.tater.domain.attribute.*
import io.mockk.mockk
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("LocalizedMovie")
class LocalizedMovieTest {

    @Test
    fun `Returns id`() {
        val target = LocalizedMovie(Movie(MovieId("id1"), mockk(), mockk(), mockk(), mockk()), mockk())

        target.id() shouldBeEqualTo MovieId("id1")
    }

    @Test
    fun `Returns English title`() {
        val target = LocalizedMovie(
                Movie(mockk(), MovieTitle("englishTitle1"), mockk(), mockk(), mockk()),
                LocalizedMovieAttributes(MovieTitle("japaneseTitle1"))
        )

        target.englishTitle() shouldBeEqualTo MovieTitle("englishTitle1")
    }

    @Test
    fun `Returns Japanese title`() {
        val target = LocalizedMovie(
                Movie(mockk(), MovieTitle("englishTitle1"), mockk(), mockk(), mockk()),
                LocalizedMovieAttributes(MovieTitle("japaneseTitle1"))
        )

        target.japaneseTitle() shouldBeEqualTo MovieTitle("japaneseTitle1")
    }

    @Test
    fun `Returns null for Japanese title when LocalizedMovieAttributes does not exist`() {
        val target = LocalizedMovie(
                Movie(mockk(), MovieTitle("englishTitle1"), mockk(), mockk(), mockk()),
                null
        )

        target.japaneseTitle() shouldBeEqualTo null
    }

    @Test
    fun `Returns overview`() {
        val target = LocalizedMovie(Movie(mockk(), mockk(), MovieOverview("overview1"), mockk(), mockk()), mockk())

        target.overview() shouldBeEqualTo MovieOverview("overview1")
    }

    @Test
    fun `Returns runtime`() {
        val target = LocalizedMovie(Movie(mockk(), mockk(), mockk(), Runtime(123), mockk()), mockk())

        target.runtime() shouldBeEqualTo Runtime(123)
    }

    @Test
    fun `Returns review`() {
        val target = LocalizedMovie(Movie(mockk(), mockk(), mockk(), mockk(), MovieReview(AverageScore(5.6), ReviewCount(1000))), mockk())

        target.review() shouldBeEqualTo MovieReview(AverageScore(5.6), ReviewCount(1000))
    }
}