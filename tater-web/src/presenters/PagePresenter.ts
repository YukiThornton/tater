import Page from "src/domains/Page";
import type { Writable } from "svelte/store";
import { navigate } from 'svelte-navigator';
import type { RouteModel } from "@stores/RouteModel";

export default class PagePresenter {

    constructor(
        private loginStore: Writable<RouteModel>,
        private topStore: Writable<RouteModel>
    ) {}

    initializePages() {
        this.pages.forEach((value, page) => {
            value.store.set({path: value.path, authRequired: page.authRequired})
        })
    }

    showPage(page: Page) {
        navigate(this.pages.get(page).path);
    }

    pages = new Map<Page, PathAndStore>([
        [Page.Top, {path: '/', store: this.topStore}],
        [Page.Login, {path: '/login', store: this.loginStore}],
    ])
}

interface PathAndStore {
    path: string;
    store: Writable<RouteModel>
}
