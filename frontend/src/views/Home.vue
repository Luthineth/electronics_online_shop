<template>
    <div class="tt">
        <v-img
            src="../../public/background.jpg"
            cover
        />
        <div class="welcome">
            Технологии, которые изменяют мир, теперь у вас под рукой.
            <v-btn
                class="ma-auto"
                color="black"
                variant="tonal"
                width="200px"
                @click="router.push('/about')"
            >
                Узнать о нас
            </v-btn>
        </div>
    </div>
</template>

<script setup>
import router from "../router/router";
</script>

<style scoped lang="scss">
@import url('https://fonts.googleapis.com/css2?family=Montserrat:ital,wght@1,200;1,300&display=swap');
.tt{
    height: 100vh;
    width: 100vw;
}
.welcome{
    font-family: "Montserrat", sans-serif;
    font-size: large;
    z-index: 3;
    position: absolute;
    display: flex;
    flex-direction: column;
    gap: 10px;
    width: 100%;
    top: 50%;
    text-align: center;
}
</style>