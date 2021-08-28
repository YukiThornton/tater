package project.tater_web_e2e

import com.codeborne.selenide.Configuration
import com.thoughtworks.gauge.BeforeSuite

class Setup {
    @BeforeSuite
    fun setupConfig() {
        Configuration.baseUrl = URL.baseUrl()
    }
}