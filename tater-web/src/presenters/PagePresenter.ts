import Page from "src/domains/Page";
import { login as loginStore, top as topStore } from '@stores/routes';
import type { Writable } from "svelte/store";
import { navigate } from 'svelte-navigator';
import type { RouteModel } from "@stores/RouteModel";

export default class PagePresenter {
    initializePages() {
        pages.forEach((value, page) => {
            value.store.set({path: value.path, authRequired: page.authRequired})
        })
    }

    showPage(page: Page) {
        navigate(pages.get(page).path);
    }
}

interface PathAndStore {
    path: string;
    store: Writable<RouteModel>
}
const pages = new Map<Page, PathAndStore>([
    [Page.Top, {path: '/', store: topStore}],
    [Page.Login, {path: '/login', store: loginStore}],
])
