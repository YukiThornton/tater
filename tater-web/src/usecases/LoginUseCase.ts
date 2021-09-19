import AuthState from "src/domains/AuthState";
import type { TextInput } from "src/domains/Input";
import { messages } from "src/domains/Message";
import Page from "src/domains/Page";
import { UserId } from "src/domains/User";
import type AuthPresenter from "src/presenters/AuthPresenter";
import type LoginPagePresenter from "src/presenters/LoginPagePresenter";
import type PagePresenter from "src/presenters/PagePresenter";
import type UserPresenter from "src/presenters/UserPresenter";

export default class LoginUseCase {
    
    constructor(
        private authPresenter: AuthPresenter,
        private userPresenter: UserPresenter,
        private pagePresenter: PagePresenter,
        private loginPagePresenter: LoginPagePresenter,
    ) {}

    login(id: TextInput) {
        if (UserId.isValid(id)) {
            this.loginPagePresenter.clearErrorMessage();
            this.authPresenter.setAuthState(new AuthState(true))
            this.userPresenter.setUserId(UserId.fromInput(id));
            this.pagePresenter.showPage(Page.Top);
        } else {
            this.loginPagePresenter.setErrorMessage(messages.invalidLoginId)
        }
    }
}