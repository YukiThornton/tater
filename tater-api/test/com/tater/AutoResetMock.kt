package com.tater

import io.mockk.MockKAnnotations
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

interface AutoResetMock {
    @BeforeEach
    fun beforeEach() {
        MockKAnnotations.init(this)
    }

    @AfterEach
    fun afterEach() {
        unmockkAll()
    }
}