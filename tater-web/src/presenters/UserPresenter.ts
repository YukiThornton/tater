import { UserId } from "src/domains/User";
import { get, Writable } from 'svelte/store'

export default class UserPresenter {

    constructor(private userIdStore: Writable<string>) {}

    setUserId(userId: UserId) {
        this.userIdStore.set(userId.id)
    }

    getUserId(): UserId {
        return new UserId(get(this.userIdStore))
    }
}