<script lang="ts">
    import { Route } from 'svelte-navigator';
    import AuthGuard from '@routes/AuthGuard.svelte';

    export let path;
</script>

<Route {path}>
    <AuthGuard>
        <slot />
    </AuthGuard>
</Route>