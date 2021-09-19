import type AuthUseCase from "@usecases/AuthUseCase";

export default class AuthController {
    constructor(private authUseCase: AuthUseCase){}

    whenAuthStatusChanges() {
        this.authUseCase.showLoginIfNotAllPagesAreAuthorized()
    }
}