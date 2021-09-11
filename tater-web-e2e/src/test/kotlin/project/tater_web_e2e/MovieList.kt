package project.tater_web_e2e

import com.codeborne.selenide.Condition.exactText
import com.codeborne.selenide.Selenide.`$`
import com.thoughtworks.gauge.Step

class MovieList {

    @Step("<index>番目の映画のタイトルとして<title>が表示されている")
    fun assertThatMovieTitleIsDisplayed(index: Int, title: String) {
        `$`("[data-tater-movie]", index - 1).find("[data-tater-title]").shouldHave(exactText(title))
    }
}