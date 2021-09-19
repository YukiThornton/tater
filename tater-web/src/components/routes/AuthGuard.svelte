<script lang="ts">
    import { authenticated } from '@stores/auth';
    import { authController } from 'src/container';

    $: {
        authenticated
        authController.whenAuthStatusChanges()
    }
</script>

{#if authenticated}
    <slot />
{/if}