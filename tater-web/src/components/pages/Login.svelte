<script lang="ts">
    import { navigate } from 'svelte-navigator';
    import { userId } from './../../store';

    let userIdInput = '';
    function login() {
        userId.set(userIdInput);
        navigate("/")
    }
</script>

<main>
    <input bind:value={userIdInput} data-tater-user-id-input />
    <button on:click={login} data-tater-login-button>Login</button>
</main>

<style>
</style>