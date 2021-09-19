import Page from "@domains/Page";
import type AuthPresenter from "@presenters/AuthPresenter";
import type PagePresenter from "@presenters/PagePresenter";

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