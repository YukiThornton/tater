package com.tater.domain

interface FCC<T>: Iterable<T>{
    val values: List<T>
    override fun iterator() = values.iterator()
}