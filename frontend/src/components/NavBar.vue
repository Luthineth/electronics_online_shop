<template>
    <div class="container">
        <div class="container_centered">
            <a class="logo">E-shop.</a>
            <SearchBar></SearchBar>
        </div>
    </div>
</template>

<script setup>
import NavBarButtons from "./NavBarButtons.vue";
import SearchBar from "./SearchBar.vue";
</script>

<style scoped lang="scss">
.container{
    height: 90px;
    padding: .475rem 0;
    z-index: 100;
    position: relative;
    margin-bottom: 3.8rem;
    display: flex;
    align-items: flex-end;
}
.logo{
    margin-right: 1.9rem;
    margin-bottom: .475rem;
    font-size: 25px;
    font-weight: 800;
    display: flex;
    align-items: flex-end;
}
</style>