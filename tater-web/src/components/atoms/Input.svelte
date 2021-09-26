<script lang="ts">
    export let value: string;
    export let id: string;
    export let focus: boolean = false;
    export let attr: {[key: string]: string|number|boolean} = {};

    let input: HTMLInputElement;

    $: {
        if (focus) {
        input.focus();
        input.select();
    }
    }
</script>

<input id={id} bind:this={input} bind:value={value} on:input {...attr} />

<style lang="scss">
    input {
        color: $g-primary-text-color;
        background-color: $g-bg-color;
        font-family: $g-primary-font-family;
        font-size: $g-font-size-medium;
        padding: 0 6px;
        border: none;
        border-bottom: 1px solid $g-primary-text-color;
        
        &:focus-visible {
            outline: none;
            border: none;
            border-bottom: 1px solid $g-primary-text-color;
        }
    }
</style>
