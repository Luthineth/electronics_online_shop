<template>
    <div class="mainNav wrapper__links">
        <router-link to="/">Главная</router-link>
        <router-link to="/frsdejn">Каталог</router-link>
        <router-link to="/frsdejn">О нас</router-link>
    </div>
    <div class="actions">
        <div class="actions__container">
            <div class="wrapper__links">
                <router-link to="/" v-if="userAuthorized">
                    <v-icon icon="mdi-logout"></v-icon>
                    Doe J.
                </router-link>
                <router-link to="/" v-else>
                    <v-icon icon="mdi-login"></v-icon>
                    Войти
                </router-link>
                <div @click="updateItemCount()">
                    <v-icon icon="mdi-cart-outline"></v-icon>
                    ({{ itemCount }})
                </div>
            </div>
            <div class="wrapper__links">
                <v-icon icon="mdi-magnify"></v-icon>
                <v-icon icon="mdi-account-outline"></v-icon>
            </div>
        </div>
    </div>
</template>

<script setup>
import {ref} from "vue";

let itemCount = ref(3)
let userAuthorized = ref(false)

const updateItemCount = () => {
    itemCount.value += 1;
    userAuthorized.value = !userAuthorized.value
};
</script>

<style scoped lang="scss">
a:hover {
  color: red;
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
</style>