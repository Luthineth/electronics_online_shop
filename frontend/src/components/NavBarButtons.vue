<template>
    <div class="mainNav wrapper__links">
        <router-link to="/">Главная</router-link>
        <AllCategoriesDropdown/>
        <router-link to="/frsdejn">О нас</router-link>
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
</template>

<script setup>
import {onMounted} from "vue";
import {checkAuthorisation, loadNewPage} from "../utils/utils.js";
import {cartItemCount, userAuthorized} from "../utils/variables.js";
import store from "../stores/store";
import AllCategoriesDropdown from "./categories/AllCategoriesDropdown.vue";

const returnUserName = () => {
    const firstName = localStorage.getItem('firstName').charAt(0)
    const secondName = localStorage.getItem('secondName')

    return `${secondName} ${firstName}.`
};

const userLogOut = () => {
    localStorage.removeItem("token")
    localStorage.removeItem("firstName")
    localStorage.removeItem("secondName")

    userAuthorized.value = false

    loadNewPage('login')
};

onMounted(async () => {
    await store.dispatch("load");

    cartItemCount.value = store.state.cart.reduce((total, item) => total + item.quantity, 0)
    userAuthorized.value = checkAuthorisation()
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