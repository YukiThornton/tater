package com.tater.domain

import com.tater.domain.attribute.ReviewCount
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("MovieSearchFilter")
class MovieSearchFilterTest {

    @Nested
    @DisplayName("withMinimumReviewCount and default values")
    inner class WithMinimumReviewCountAndDefaultValues {

        @Test
        fun `Returns a MovieSearchFilter with specified min review count and other default values`() {
            val count = ReviewCount(123)
            val actual = MovieSearchFilter.withMinimumReviewCount(count)

            actual.minReviewCount() shouldBeEqualTo 123
            actual.includeAdult() shouldBeEqualTo false
            actual.includeVideo() shouldBeEqualTo false
        }
    }
}