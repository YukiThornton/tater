import Page from "src/domains/Page";
import type AuthPresenter from "src/presenters/AuthPresenter";
import type PagePresenter from "src/presenters/PagePresenter";

export default class AuthUseCase {

    constructor(
        private authPresenter: AuthPresenter,
        private pagePresenter: PagePresenter,
    ) {}

    showLoginIfNotAllPagesAreAuthorized() {
        if (!this.authPresenter.getAuthState().allPagesAreAuthorized()) {
            this.pagePresenter.showPage(Page.Login)
        }
    }
}