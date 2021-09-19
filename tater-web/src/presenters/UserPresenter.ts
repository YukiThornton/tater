import { get, Writable } from 'svelte/store'
import { UserId } from "@domains/User";

export default class UserPresenter {

    constructor(private userIdStore: Writable<string>) {}

    setUserId(userId: UserId) {
        this.userIdStore.set(userId.id)
    }

    getUserId(): UserId {
        return new UserId(get(this.userIdStore))
    }
}