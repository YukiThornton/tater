package com.tater.driver

interface MovieApi {
    fun getMovie(id: String): MovieJson

    data class MovieJson(val id: String, val title: String)
}