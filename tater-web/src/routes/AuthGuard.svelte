<script lang="ts">
    import { authenticated } from '@stores/auth';
    import AuthController from 'src/controllers/AuthController';

    $: {
        authenticated
        new AuthController().whenAuthStatusChanges()
    }
</script>

{#if authenticated}
    <slot />
{/if}