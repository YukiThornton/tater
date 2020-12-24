package com.tater.domain

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