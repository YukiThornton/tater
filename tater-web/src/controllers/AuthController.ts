import AuthUseCase from "src/usecases/AuthUseCase";

export default class AuthController {
    private authUseCase = new AuthUseCase()
    whenAuthStatusChanges() {
        this.authUseCase.showLoginIfNotAllPagesAreAuthorized()
    }
}