package com.tater.config

interface MovieApiConfig {
    fun endpoint(): String
    fun authToken(): String
}