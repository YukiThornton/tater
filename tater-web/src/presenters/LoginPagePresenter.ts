import type { Writable } from "svelte/store";
import type { Message } from "@domains/Message";

export default class LoginPagePresenter {

    constructor(private errorMessageStore: Writable<string>) {}

    setErrorMessage(message: Message) {
        this.errorMessageStore.set(message.text);
    }
    
    clearErrorMessage() {
        this.errorMessageStore.set(null)
    }
}