package project.tater_web_e2e

import com.codeborne.selenide.WebDriverRunner
import com.thoughtworks.gauge.Step
import org.amshove.kluent.shouldBeEqualTo

class MovieList {

    @Step("映画一覧画面が表示されている")
    fun assertMovieListPageIsDisplayed() {
        WebDriverRunner.url() shouldBeEqualTo URL.fullUrlOf(Page.MovieList)
    }
}