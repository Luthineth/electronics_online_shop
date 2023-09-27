<template>
    <NavBar></NavBar>
    <suspense>
        <router-view/>
    </suspense>
    <MainFooter>
    </MainFooter>
</template>

<script setup>
import NavBar from "./components/NavBar.vue";
import MainFooter from "./components/MainFooter.vue";
</script>
