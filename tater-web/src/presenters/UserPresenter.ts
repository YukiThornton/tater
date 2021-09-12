import { UserId } from "src/domains/User";
import { userId } from '@stores/user';
import { get } from 'svelte/store'

export default class UserPresenter {
    getUserId(): UserId {
        return new UserId(get(userId))
    }
}