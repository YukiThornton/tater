package com.tater.domain

import com.tater.domain.attribute.MovieId
import com.tater.domain.attribute.MovieTitle

data class MovieSummary (
        val id: MovieId,
        val title: MovieTitle,
)