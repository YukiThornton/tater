import type { Message } from "src/domains/Message";
import type { Writable } from "svelte/store";

export default class LoginPagePresenter {

    constructor(private errorMessageStore: Writable<string>) {}

    setErrorMessage(message: Message) {
        this.errorMessageStore.set(message.text);
    }
    
    clearErrorMessage() {
        this.errorMessageStore.set(null)
    }
}