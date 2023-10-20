<template>
    <div class="authorization-container">
        <v-card variant="outlined" class="authorization-card">
            <div v-if="isLogin">
                <form @submit.prevent="submit" class="form__login">
                    <v-card-title>Вход в учетную запись</v-card-title>
                    <v-text-field
                        v-model="email.value.value"
                        :error-messages="email.errorMessage.value"
                        label="E-mail"
                        variant="underlined"
                    ></v-text-field>

                    <v-text-field
                        v-model="password.value.value"
                        :error-messages="password.errorMessage.value"
                        label="Пароль"
                        variant="underlined"
                    ></v-text-field>
                    <v-card-actions class="d-flex justify-center">
                        <v-btn
                            class="me-4"
                            color="black"
                            variant="tonal"
                            type="submit"
                        >
                            <v-icon icon="mdi-login"></v-icon>
                            Войти
                        </v-btn>

                        <v-btn @click="handleReset">
                            <v-icon icon="mdi-close"></v-icon>
                            Очистить
                        </v-btn>
                    </v-card-actions>
                </form>
            </div>
            <div v-else>
                <form @submit.prevent="submit" class="form__signup">
                    <v-card-title>Регистрация</v-card-title>
                    <v-text-field
                        v-model="name.value.value"
                        :error-messages="name.errorMessage.value"
                        label="Имя"
                        variant="underlined"
                    ></v-text-field>

                    <v-text-field
                        v-model="lastName.value.value"
                        :error-messages="lastName.errorMessage.value"
                        label="Фамилия"
                        variant="underlined"
                    ></v-text-field>

                    <v-text-field
                        v-model="email.value.value"
                        :error-messages="email.errorMessage.value"
                        label="E-mail"
                        variant="underlined"
                    ></v-text-field>

                    <v-text-field
                        v-model="password.value.value"
                        :error-messages="password.errorMessage.value"
                        label="Пароль"
                        variant="underlined"
                    ></v-text-field>
                    <v-card-actions class="d-flex justify-center">
                        <v-btn
                            class="me-4"
                            color="black"
                            variant="tonal"
                            type="submit"
                        >
                            <v-icon icon="mdi-check"></v-icon>
                            Сохранить
                        </v-btn>

                        <v-btn @click="handleReset">
                            <v-icon icon="mdi-close"></v-icon>
                            Очистить
                        </v-btn>
                    </v-card-actions>
                </form>
            </div>
            <div class="text-center">
                <button @click="toggleForm" class="change-form-type">{{ isLogin ? 'Нет аккаунта? Присоединиться' : 'Есть аккаунт? Войти' }}</button>
            </div>
        </v-card>
    </div>
</template>

<script setup>
import { ref } from 'vue';
import { useField, useForm } from 'vee-validate'

const { handleSubmit, handleReset } = useForm({
    validationSchema: {
        name (value) {
            if (value?.length >= 2) return true

            return 'Имя должно содержать минимум 2 символа.'
        },
        lastName (value) {
            if (value?.length >= 2) return true

            return 'Фамилия должна содержать минимум 2 символа.'
        },
        email (value) {
            if (/^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i.test(value)) return true

            return 'Введите e-mail правильно.'
        },
        password (value) {
            if (/^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[!@#$%^&*]).{8,}$/.test(value)) return true

            return 'Пароль должен содержать минимум 1 заглавную букву, 1 строчную букву, 1 цифру, ' +
                '1 специальный символ. Длина не менее 8 символов.'
        },
    },
})
const name = useField('name')
const lastName = useField('lastName')
const email = useField('email')
const password = useField('password')

const submit = handleSubmit(values => {
    alert(JSON.stringify(values, null, 2))
})
const isLogin = ref(true);

const login = () => {
};



const toggleForm = () => {
    isLogin.value = !isLogin.value;
};
</script>

<style scoped lang="scss">
.authorization-container {
    height: 80vh;
    display: flex;
    justify-content: center;
    align-items: center;
}
.authorization-card{
    width: 50vw;
    max-width: 500px;
    padding: 20px;
}
.v-card-title{
    padding: 0;
    text-align: center;
}
.change-form-type{
    text-decoration: underline;
}
</style>
