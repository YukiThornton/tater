package com.tater.domain

data class LocalizedMovie(private val movie: Movie, private val japaneseAttributes: LocalizedMovieAttributes) {
    fun id() = movie.id
    fun title(language: Language)
            = if (language == Language.English) movie.title else japaneseAttributes.title
    fun overview() = movie.overview
    fun runtime() = movie.runtime
    fun review() = movie.review
}