package project.tater_web_e2e

import com.codeborne.selenide.Configuration
import com.codeborne.selenide.Selenide
import com.thoughtworks.gauge.AfterScenario
import com.thoughtworks.gauge.BeforeSuite
import project.tater_web_e2e.pages.URL

class SetupAndTeardown {
    @BeforeSuite
    fun setupConfig() {
        Configuration.baseUrl = URL.baseUrl()
    }

    @AfterScenario
    fun closeApp() {
        Selenide.closeWebDriver()
    }
}