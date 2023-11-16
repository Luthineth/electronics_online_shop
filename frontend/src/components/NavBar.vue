<template>
    <div class="container">
        <div class="container_centered">
            <a class="logo">E-shop.</a>
            <SearchBar v-if="isSearchBarShown"/>
            <NavBarButtons v-else/>
        </div>
    </div>
</template>

<script setup>
import NavBarButtons from "./NavBarButtons.vue";
import SearchBar from "./SearchBar.vue";
import {isSearchBarShown} from "../utils/utils.js";
</script>

<style scoped lang="scss">
.container{
    margin-bottom: 6rem;
    display: flex;
    justify-content: center;
}
.container_centered{
    z-index: 1;
    position: fixed;
    padding: 0 5rem;
    display: flex;
    background-color: #faf8f5;
    border-bottom: #e2e2e2 1px solid;
    height: 80px;
    width: 100%;
    max-width: 1500px;
}
.logo{
    width: 10vw;
    max-width: 100px;
    margin-right: 1.9rem;
    margin-bottom: .475rem;
    font-size: 25px;
    font-weight: 800;
    display: flex;
    align-items: flex-end;
}
</style>