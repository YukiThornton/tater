<script lang="ts">
    export let text: string;
    export let attr: {[key: string]: string|number|boolean} = {};
</script>

<span {...attr}>{text}</span>

<style lang="scss">
    span {
        font-size: 16px;
    }
</style>