<template>
    <div class="mainNav wrapper__links">
        <router-link to="/">Главная</router-link>
        <AllCategoriesDropdown/>
        <router-link to="/about">О нас</router-link>
    </div>

    <div class="mainNav wrapper__links actions">
        <button
            class="button__logout"
            v-if="userAuthorized"
            @click="userLogOut()"
        >
            <v-icon icon="mdi-logout"/>
            {{ returnUserName() }}
        </button>

        <router-link
            to="/login"
            v-else
        >
            <v-icon icon="mdi-login"/>
            Войти
        </router-link>

        <router-link to="/cart">
            <v-icon icon="mdi-cart-outline"/>
            ({{ cartItemCount }})
        </router-link>
    </div>

    <v-snackbar
        v-model="showSessionExpiredSnackbar"
        color="error"
        :timeout="3000"
    >
        Время вашего сеанса истекло, перезайдите в аккаунт!
    </v-snackbar>
</template>

<script setup>
import {onMounted, onUnmounted, ref} from "vue";
import {checkAuthorisation, loadNewPage} from "../utils/utils.js";
import {cartItemCount, tokenExpirationDate, userAuthorized} from "../utils/variables.js";
import store from "../stores/store";
import AllCategoriesDropdown from "./categories/AllCategoriesDropdown.vue";

const showSessionExpiredSnackbar = ref(false);

const returnUserName = () => {
    const firstName = localStorage.getItem('firstName').charAt(0)
    const secondName = localStorage.getItem('secondName')

    return `${secondName} ${firstName}.`
};

const userLogOut = () => {
    localStorage.removeItem("token")
    localStorage.removeItem("firstName")
    localStorage.removeItem("secondName")

    showSessionExpiredSnackbar.value = false
    userAuthorized.value = false

    loadNewPage('login')
};

const checkTokenExpiration = () => {
    const tokenExpiration = tokenExpirationDate.value;
    const currentTime = Math.floor(Date.now() / 1000);

    if (tokenExpiration && tokenExpiration < currentTime) {
        showSessionExpiredSnackbar.value = true;
    }
};

const tokenExpirationCheckInterval = setInterval(checkTokenExpiration, 10000);

onMounted(async () => {
    await store.dispatch("load");

    cartItemCount.value = store.state.cart.reduce((total, item) => total + item.quantity, 0)
    userAuthorized.value = checkAuthorisation()
});

onUnmounted(() => {
    clearInterval(tokenExpirationCheckInterval);
});
</script>

<style scoped lang="scss">
a:hover {
    color: darkgreen;
}
.mainNav{
    padding: 0 10px 10px 10px;
}
.wrapper__links{
    display: flex;
    align-items: flex-end;
    gap: 1.425rem;
    text-decoration: none;
    -webkit-transition: all 0.2s ease-in-out;
    transition: all 0.2s ease-in-out;
}
.actions{
    margin-left: auto;
}
.button__logout:hover{
    color: darkgreen;
}
.show-all-categories:hover{
    color: darkgreen;
}
</style>