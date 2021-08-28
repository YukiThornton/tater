package project.tater_web_e2e

import com.codeborne.selenide.Selenide.`$`
import com.codeborne.selenide.Selenide.open
import com.thoughtworks.gauge.Step

class Login {

    @Step("ログイン画面を表示する")
    fun openPage() {
        open(Page.Login.path)
    }

    @Step("ユーザID入力欄に<id>を入力する")
    fun loginWithUserId(userId: String) {
        `$`("[data-tater-user-id-input]").value = userId
    }

    @Step("ログインボタンをクリックする")
    fun clickLoginButton() {
        `$`("[data-tater-login-button]").click()
    }
}