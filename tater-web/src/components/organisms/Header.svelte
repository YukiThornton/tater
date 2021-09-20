<script lang="ts">
    import AppIcon from '@atoms/AppIcon.svelte';
    import UserId from '@molecules/UserId.svelte'

    export let userId: string;
</script>

<header>
    <AppIcon />
    <UserId userId={userId} />
</header>

<style lang="scss">
    header {
        padding: 0 25px;
        display: flex;
        justify-content: space-between;
        align-items: baseline;
        border-bottom: 2px dashed #e4e4e4;
    }
</style>