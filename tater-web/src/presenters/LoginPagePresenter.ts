import { errorMessage as errorMessageStore } from "@stores/pages/login";
import type { Message } from "src/domains/Message";

export default class LoginPagePresenter {
    setErrorMessage(message: Message) {
        errorMessageStore.set(message.text);
    }
    
    clearErrorMessage() {
        errorMessageStore.set(null)
    }
}