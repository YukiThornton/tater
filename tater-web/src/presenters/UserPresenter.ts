import { UserId } from "src/domains/User";
import { userId as userIdStore } from '@stores/user';
import { get } from 'svelte/store'

export default class UserPresenter {
    setUserId(userId: UserId) {
        userIdStore.set(userId.id)
    }

    getUserId(): UserId {
        return new UserId(get(userIdStore))
    }
}