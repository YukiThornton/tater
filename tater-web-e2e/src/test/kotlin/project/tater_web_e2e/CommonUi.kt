package project.tater_web_e2e

import com.codeborne.selenide.Condition.exactText
import com.codeborne.selenide.Selenide.`$`
import com.thoughtworks.gauge.Step

class CommonUi {

    @Step("ヘッダー部のアプリ名として<appName>が表示されている")
    fun appNameIsDisplayed(appName: String) {
        `$`("[data-tater-app-name]").shouldHave(exactText(appName))
    }

    @Step("表示中のユーザとしてID<userId>が表示されている")
    fun assertThatUserIdIsDisplayed(userId: String) {
        `$`("[data-tater-user-id").shouldHave(exactText(userId))
    }

}