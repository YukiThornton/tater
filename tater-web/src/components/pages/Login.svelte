<script lang="ts">
    import { errorMessage } from '@stores/pages/login';
    import LoginController from 'src/controllers/LoginController';

    let input: HTMLInputElement;
    let inputValue = '';
    let inputFocusRequired = false;
    $: if ($errorMessage) {
        inputFocusRequired = true;
    }
    $: if (inputFocusRequired) {
        input.focus();
        input.select();
    }

    function login() {
        const controller = new LoginController();
        controller.login(inputValue);
    }
    
    function clearFocus() {
        inputFocusRequired = false;
    }
</script>

<main>
    <input bind:this={input} bind:value={inputValue} on:change={clearFocus} data-tater-user-id-input />
    <button on:click={login} data-tater-login-button>Login</button>
    {#if $errorMessage}
        <p data-tater-login-message>{$errorMessage}</p>
    {/if}
</main>

<style>
</style>