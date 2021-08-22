package project.tater_web_e2e

import com.codeborne.selenide.Condition.text
import com.codeborne.selenide.Configuration
import com.codeborne.selenide.Selenide.`$`
import com.codeborne.selenide.Selenide.open
import com.thoughtworks.gauge.BeforeSuite
import com.thoughtworks.gauge.Step

class CommonUi {

    @BeforeSuite
    fun setupConfig() {
        Configuration.baseUrl = "http://localhost:18100"
    }

    @Step("画面を開く")
    fun openPage() {
        open("/")
    }

    @Step("ヘッダー部のアプリ名として<appName>が表示されている")
    fun appNameIsDisplayed(appName: String) {
        `$`("[data-tater-app-name]").shouldHave(text(appName))
    }
}