package project.tater_web_e2e

import com.codeborne.selenide.CollectionCondition
import com.codeborne.selenide.Condition.exactText
import com.codeborne.selenide.Selenide.`$$`
import com.codeborne.selenide.Selenide.`$`
import com.thoughtworks.gauge.Step

class MovieList {

    companion object {
        private const val movieSelector = "[data-tater-movie]"
        private const val titleSelector = "[data-tater-title]"
        private const val ratingSelector = "[data-tater-rating]"
    }


    @Step("<index>番目の映画のタイトルとして<title>が表示されている")
    fun assertThatMovieTitleIsDisplayed(index: Int, title: String) {
        `$`(movieSelector, index - 1).find(titleSelector).shouldHave(exactText(title))
    }

    @Step("映画<title>の評価として<rating>が表示されている")
    fun assertThatMovieRatingIsDisplayed(title: String, rating: String) {
        val movieIndex = `$$`(titleSelector).shouldHave(CollectionCondition.itemWithText(title)).indexOfFirst { it.text() == title }
        `$`(movieSelector, movieIndex).find(ratingSelector).shouldHave(exactText(rating))
    }
}