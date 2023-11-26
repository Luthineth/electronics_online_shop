<template>
    <div class="landing">
        <div class="centered">
            <h1 class="error-title">404</h1>
            <h2 class="error-message">Тут пока пусто:(</h2>
            <v-btn
                    variant="outlined"
                    @click="$router.push('/')"
            >
                Вернуться
            </v-btn>
        </div>
    </div>
</template>

<style scoped lang="scss">
@import url('https://fonts.googleapis.com/css2?family=Handjet:wght@100&display=swap');
.landing{
  top: 0;
  z-index: 0;
}
.centered{
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
}
.error-title{
  font-family: "Handjet", sans-serif;
  text-transform: uppercase;
  font-size: 15rem;
}
.error-message{
  margin-bottom: 20px;
}
</style>