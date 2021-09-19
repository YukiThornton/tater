import AuthState from "src/domains/AuthState";
import type { TextInput } from "src/domains/Input";
import { messages } from "src/domains/Message";
import Page from "src/domains/Page";
import { UserId } from "src/domains/User";
import AuthPresenter from "src/presenters/AuthPresenter";
import LoginPagePresenter from "src/presenters/LoginPagePresenter";
import PagePresenter from "src/presenters/PagePresenter";
import UserPresenter from "src/presenters/UserPresenter";

export default class LoginUseCase {
    login(id: TextInput) {
        const authPresenter = new AuthPresenter();
        const userPresenter = new UserPresenter();
        const pagePresenter = new PagePresenter();
        const loginPagePresenter = new LoginPagePresenter();

        if (UserId.isValid(id)) {
            loginPagePresenter.clearErrorMessage();
            authPresenter.setAuthState(new AuthState(true))
            userPresenter.setUserId(UserId.fromInput(id));
            pagePresenter.showPage(Page.Top);
        } else {
            loginPagePresenter.setErrorMessage(messages.invalidLoginId)
        }
    }
}