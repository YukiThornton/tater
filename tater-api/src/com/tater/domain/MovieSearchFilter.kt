package com.tater.domain

import com.tater.domain.attribute.ReviewCount

data class MovieSearchFilter private constructor(
        private val minReviewCount: ReviewCount,
        private val includeVideo: Boolean = false,
        private val includeAdult: Boolean = false
) {

    companion object {
        fun withMinimumReviewCount(count: ReviewCount): MovieSearchFilter {
            return MovieSearchFilter(count)
        }
    }

    fun minReviewCount() = minReviewCount.value
    fun includeVideo() = includeVideo
    fun includeAdult() = includeAdult
}