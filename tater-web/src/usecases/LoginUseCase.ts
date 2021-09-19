import AuthState from "@domains/AuthState";
import type { TextInput } from "@domains/Input";
import { messages } from "@domains/Message";
import Page from "@domains/Page";
import { UserId } from "@domains/User";
import type AuthPresenter from "@presenters/AuthPresenter";
import type LoginPagePresenter from "@presenters/LoginPagePresenter";
import type PagePresenter from "@presenters/PagePresenter";
import type UserPresenter from "@presenters/UserPresenter";

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