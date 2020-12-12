package com.tater.port

import com.tater.domain.MovieIds
import com.tater.domain.MovieSummaries

interface MovieSummaryPort {
    fun movieSummariesOf(movieIds: MovieIds): MovieSummaries
}