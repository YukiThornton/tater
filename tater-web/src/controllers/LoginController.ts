import { TextInput } from "src/domains/Input";
import LoginUseCase from "src/usecases/LoginUseCase";

export default class LoginController {
    login(id: string) {
        const useCase = new LoginUseCase()
        useCase.login(new TextInput(id));
    }
}