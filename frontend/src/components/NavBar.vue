<template>
    <div class="container">
        <div class="container_centered">
            <a class="logo">E-shop.</a>
            <div class="wrapper__links">
                <router-link to="/">Главная</router-link>
                <router-link to="/frsdejn">Что-то там</router-link>
            </div>
            <div class="actions">
                <div class="actions__container">
                    <div class="info__links">
                        <router-link to="/">Войти</router-link>
                        <router-link to="/frsdejn">Корзина</router-link>
                    </div>
                    <div class="user__buttons">
                        <button>eh</button>
                        <button>uh</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
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