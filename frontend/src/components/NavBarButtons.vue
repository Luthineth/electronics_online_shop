<template>
    <div class="mainNav wrapper__links">
        <router-link to="/">Главная</router-link>
        <div class="text-center">
            <v-menu
                open-on-hover
            >
                <template v-slot:activator="{ props }">
                    <button
                        v-bind="props"
                    >
                        Dropdown
                    </button>
                </template>

                <v-list>
                    <v-list-item
                        v-for="(item, index) in items"
                        :key="index"
                    >
                        <v-list-item-title>{{ item.title }}</v-list-item-title>
                    </v-list-item>
                </v-list>
            </v-menu>
        </div>

        <div class="show-all-categories">
            <button>
                Каталог
                <AllCategoriesModal/>
            </button>
        </div>

        <router-link to="/frsdejn">
            О нас
        </router-link>
    </div>
    <div class="actions">
        <div class="actions__container">
            <div class="wrapper__links">
                <button
                    class="button__logout"
                    v-if="userAuthorized"
                    @click="userLogOut()"
                >
                    <v-icon icon="mdi-logout"></v-icon>
                    {{ returnUserName() }}
                </button>
                <router-link
                    to="/login"
                    v-else
                >
                    <v-icon icon="mdi-login"></v-icon>
                    Войти
                </router-link>
                <router-link to="/cart">
                    <v-icon icon="mdi-cart-outline"></v-icon>
                    ({{ cartItemCount }})
                </router-link>
            </div>
            <div class="wrapper__links">
                <v-icon icon="mdi-magnify" @click="showSearchBar"></v-icon>
                <router-link to="/account">
                    <v-icon icon="mdi-account-outline"></v-icon>
                </router-link>
            </div>
        </div>
    </div>
</template>

<script setup>
import {onMounted} from "vue";
import {cartItemCount, checkAuthorisation, isSearchBarShown, loadNewPage, userAuthorized} from "../utils/utils.js";
import store from "../stores/store";
import AllCategoriesModal from "./AllCategoriesModal.vue";

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

const showSearchBar = () => {
    isSearchBarShown.value = true
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
  margin-bottom: .475rem;
  display: flex;
}
.actions__container{
  display: flex;
  flex-flow: column nowrap;
  align-items: flex-end;
  justify-content: space-between;
  gap: .7125rem;
}
.button__logout:hover{
    color: darkgreen;
}
.show-all-categories:hover{
    color: darkgreen;
}
</style>