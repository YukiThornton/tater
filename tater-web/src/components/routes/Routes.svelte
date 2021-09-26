<script lang="ts">
    import { Router, Route } from 'svelte-navigator';
    import { onMount } from 'svelte';
    import { login, top } from '@stores/routes';
    import type { RouteModel } from '@stores/RouteModel';
    import AuthRoute from '@routes/AuthRoute.svelte';
    import Login from '@pages/Login.svelte';
    import Top from '@pages/Top.svelte';
    import { appController } from '@container';
    
    let pages: [RouteModel, any][]
    onMount(() => {
        appController.initializePages();

        pages = [
            [$login, Login],
            [$top, Top]
        ]
    })
</script>

<Router primary={false}>
    {#if pages}
        {#each pages as [page, component]}
            {#if page.authRequired }
                <AuthRoute path={page.path}><svelte:component this={component} /></AuthRoute>
            {:else}
                <Route path={page.path}><svelte:component this={component} /></Route>
            {/if}
        {/each}
    {/if}
</Router>
