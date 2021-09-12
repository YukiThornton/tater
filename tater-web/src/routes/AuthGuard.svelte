<script lang="ts">
    import { navigate } from 'svelte-navigator';
    import { userId } from '@stores/user';

    $: authorized = !!$userId;
    $: if (!authorized) {
        navigate("/login", { replace: true });
    }
</script>

{#if authorized}
    <slot />
{/if}