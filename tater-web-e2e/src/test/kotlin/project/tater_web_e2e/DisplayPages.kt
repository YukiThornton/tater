package project.tater_web_e2e

import com.codeborne.selenide.Selenide.open
import com.codeborne.selenide.WebDriverRunner
import com.thoughtworks.gauge.Step
import org.amshove.kluent.shouldBeEqualTo
import project.tater_web_e2e.pages.Page
import project.tater_web_e2e.pages.URL

class DisplayPages {

    private fun open(page: Page) {
        open(page.path)
    }

    private fun assertDisplayed(page: Page) {
        WebDriverRunner.url() shouldBeEqualTo URL.fullUrlOf(page)
    }

    @Step("ログイン画面を表示する")
    fun openLoginPage() {
        open(Page.Login)
    }

    @Step("ログイン画面が表示されている")
    fun assertLoginPageIsDisplayed() {
        assertDisplayed(Page.Login)
    }

    @Step("映画一覧画面を表示する")
    fun openMovieListPage() {
        open(Page.MovieList)
    }

    @Step("映画一覧画面が表示されている")
    fun assertMovieListPageIsDisplayed() {
        assertDisplayed(Page.MovieList)
    }
}
