<script lang="ts">
    import { errorMessage } from '@stores/pages/login';
    import { loginController } from '@container';
    import LoginTemplate from '@templates/Login.svelte';

    let userId = '';
    let focusRequired = false;
    $: if ($errorMessage) {
        focusRequired = true;
    }

    function login() {
        loginController.login(userId);
    }
    
    function clearFocus() {
        focusRequired = false;
    }
</script>

<LoginTemplate
    bind:userId={userId}
    bind:focusRequired={focusRequired}
    bind:errorMessage={$errorMessage}
    on:login={login}
    on:input={clearFocus}
/>
