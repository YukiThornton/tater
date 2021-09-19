import { TextInput } from "@domains/Input";
import type LoginUseCase from "@usecases/LoginUseCase";

export default class LoginController {
    constructor(private loginUseCase: LoginUseCase){}

    login(id: string) {
        this.loginUseCase.login(new TextInput(id));
    }
}