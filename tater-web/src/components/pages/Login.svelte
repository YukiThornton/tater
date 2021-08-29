<script lang="ts">
    import { navigate } from 'svelte-navigator';
    import { userId } from '@stores/store';

    let input: HTMLInputElement;
    let inputValue = '';
    let showErrorMessage = false;

    function login() {
        const value = Number(inputValue)
        if (!Number.isNaN(value)) {
            userId.set(value);
            navigate("/")
        } else {
            showErrorMessage = true;
            input.focus();
            input.select();
        }
    }
</script>

<main>
    <input bind:this={input} bind:value={inputValue} data-tater-user-id-input />
    <button on:click={login} data-tater-login-button>Login</button>
    {#if showErrorMessage}
        <p data-tater-login-message>Invalid ID</p>
    {/if}
</main>

<style>
</style>