<script lang="ts">
    export let text: string;
    export let light: boolean = false;
    export let attr: {[key: string]: string|number|boolean} = {};
</script>

<span {...attr} class:light>{text}</span>

<style lang="scss">
    span {
        font-size: $g-font-size-medium;
    }
    
    .light {
        color: $g-primary-text-color;
    }
</style>