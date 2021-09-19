import Page from "src/domains/Page";
import AuthPresenter from "src/presenters/AuthPresenter";
import PagePresenter from "src/presenters/PagePresenter";

export default class AuthUseCase {
    readonly authPresenter = new AuthPresenter()
    readonly pagePresenter = new PagePresenter()

    showLoginIfNotAllPagesAreAuthorized() {
        if (!this.authPresenter.getAuthState().allPagesAreAuthorized()) {
            this.pagePresenter.showPage(Page.Login)
        }
    }
}