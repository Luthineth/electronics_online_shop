<template>
    <footer
            class="text-center d-flex flex-column footer"
    >
        <div class="pt-0">
            By Artem Lihachev (backend) & Ekaterina Pustovalova (frontend)
        </div>

        <v-divider/>

        <div>
            {{ new Date().getFullYear() }} —
            <strong>
                <router-link to="/">
                    E-shop
                </router-link>
            </strong>
        </div>
    </footer>
</template>

<style lang="scss" scoped>
.footer {
  bottom: 0;
  width: 100%;
  position: fixed;
  height: auto;
    background-color: #e2e2e2;
}
</style>