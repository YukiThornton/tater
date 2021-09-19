<script lang="ts">
    import { authenticated } from '@stores/auth';
    import { authController } from '@container';

    $: {
        authenticated
        authController.whenAuthStatusChanges()
    }
</script>

{#if authenticated}
    <slot />
{/if}