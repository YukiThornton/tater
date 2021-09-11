package project.tater_web_e2e

import com.codeborne.selenide.Configuration
import com.codeborne.selenide.Selenide
import com.thoughtworks.gauge.AfterScenario
import com.thoughtworks.gauge.BeforeScenario
import com.thoughtworks.gauge.BeforeSuite
import project.tater_web_e2e.pages.URL

class SetupAndTeardown {

    private val movieApi = MovieApi()

    @BeforeSuite
    fun setupConfig() {
        Configuration.baseUrl = URL.baseUrl()
    }

    @BeforeScenario
    fun clearAllMocks() {
        movieApi.clearAllMocks()
    }

    @AfterScenario
    fun closeApp() {
        Selenide.closeWebDriver()
    }
}