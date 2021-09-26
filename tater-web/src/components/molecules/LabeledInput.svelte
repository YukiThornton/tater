<script lang="ts">
    import Input from '@atoms/Input.svelte';
    import Text from '@atoms/Text.svelte';
    import ErrorText from '@atoms/ErrorText.svelte';
    
    export let value: string;
    export let id: string;
    export let focus: boolean;
    export let errorMessage: string;
</script>

<div>
    {#if errorMessage}
        <ErrorText
            text={errorMessage}
            attr={{'data-tater-login-message': ''}}
        />
    {/if}
    <label for={id}>
        <Text text="User ID" />
        <Input
        id={id}
        bind:value={value}
        bind:focus={focus} 
        on:input
        attr={{'data-tater-user-id-input': ''}} />
    </label>
</div>

<style lang="scss">
    div {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 1vh;
    }
</style>