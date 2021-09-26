<script lang="ts">
    import Routes from '@routes/Routes.svelte';
</script>

<Routes />

<style lang="scss" global>
    @import "../node_modules/normalize.css/normalize";
    :global(body) {
        margin: 0;
        background-color: $g-bg-color;
        color: $g-primary-text-color;
        font-family: $g-primary-font-family;
    }
</style>