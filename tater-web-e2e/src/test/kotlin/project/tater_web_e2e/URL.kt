package project.tater_web_e2e

object URL {
    fun baseUrl() = "http://localhost:18100"

    fun fullUrlOf(page: Page) = "${baseUrl()}${page.path}"
}