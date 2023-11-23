<template>
    <AlertContainer
        v-if="signUpSuccess"
        :color="'success'"
        :icon="'mdi-check-circle-outline'"
        :message="'Поздравляем! Вы успешно зарегистрированы'"
    />
    <AlertContainer
        v-if="logInError || signUpError"
        :color="'error'"
        :icon="'mdi-alert-circle-outline'"
        :message="logInError ? 'Неправильный e-mail или пароль' : 'Такой пользователь уже существует'"
    />
    <div class="authorization-container">
        <v-card
            variant="outlined"
            class="authorization-card"
        >
            <div
                v-if="isLogin"
                key="logInContainer"
            >
                <v-card-title>Вход в учетную запись</v-card-title>

                <v-text-field
                    v-model="email.value.value"
                    label="E-mail"
                    variant="underlined"
                />

                <v-text-field
                    v-model="password.value.value"
                    label="Пароль"
                    variant="underlined"
                    type="password"
                />

                <v-card-actions class="d-flex justify-center">
                    <v-btn
                        class="me-4"
                        color="black"
                        variant="tonal"
                        @click="userLogIn()"
                    >
                        <v-icon icon="mdi-login"/>
                        Войти
                    </v-btn>

                    <v-btn @click="handleReset(); logInError = false">
                        <v-icon icon="mdi-close"/>
                        Очистить
                    </v-btn>
                </v-card-actions>
            </div>
            <div
                v-else
                key="signUpContainer"
            >
                <form
                    @submit.prevent="userSignUp()"
                    class="form__signup"
                >
                    <v-card-title>Регистрация</v-card-title>

                    <v-text-field
                        v-model="name.value.value"
                        :error-messages="name.errorMessage.value"
                        label="Имя"
                        variant="underlined"
                    />

                    <v-text-field
                        v-model="lastName.value.value"
                        :error-messages="lastName.errorMessage.value"
                        label="Фамилия"
                        variant="underlined"
                    />

                    <v-text-field
                        v-model="email.value.value"
                        :error-messages="email.errorMessage.value"
                        label="E-mail"
                        variant="underlined"
                    />

                    <v-text-field
                        v-model="password.value.value"
                        :error-messages="password.errorMessage.value"
                        label="Пароль"
                        variant="underlined"
                    />

                    <v-card-actions class="d-flex justify-center">
                        <v-btn
                            class="me-4"
                            color="black"
                            variant="tonal"
                            @click="userSignUp()"
                        >
                            <v-icon icon="mdi-check"/>
                            Сохранить
                        </v-btn>

                        <v-btn @click="handleReset(); signUpError = false">
                            <v-icon icon="mdi-close"/>
                            Очистить
                        </v-btn>
                    </v-card-actions>
                </form>
            </div>

            <div class="text-center">
                <button
                    @click="toggleForm"
                    class="change-form-type"
                >
                    {{ isLogin ? 'Нет аккаунта? Присоединиться' : 'Есть аккаунт? Войти' }}
                </button>
            </div>
        </v-card>
    </div>
</template>

<script setup>
import {ref} from 'vue';
import {useField, useForm} from 'vee-validate'
import axios from "axios";
import router from "../router/router";
import {userAuthorized} from "../utils/variables.js";
import AlertContainer from "../components/AlertContainer.vue";
import {authBackendUrl, loginBackendUrl} from "../utils/urls";

const isLogin = ref(true);
const signUpSuccess = ref(false);
const logInError = ref(false);
const signUpError = ref(false);

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

const userLogIn = async () => {
    logInError.value = false;
    const user = {
        email: email.value.value,
        password: password.value.value,
    }

    await axios
        .post(loginBackendUrl, user)
        .catch(() => {
            logInError.value = true
        })
        .then(async (res) => {
            if (logInError.value === false){
                localStorage.setItem('token', res.data.token)
                localStorage.setItem('firstName', res.data.firstName)
                localStorage.setItem('secondName', res.data.secondName)

                userAuthorized.value = !userAuthorized.value

                await router.push('/')
                location.reload()
            }
        })
}

const userSignUp = handleSubmit(async () => {
    signUpError.value = false;
    const user = {
        firstName: name.value.value,
        secondName: lastName.value.value,
        email: email.value.value,
        password: password.value.value,
    }

    await axios
        .post(authBackendUrl, user)
        .catch(() => {
            signUpError.value = true
        })
        .then(() => {
            if (signUpError.value === false) {
                signUpSuccess.value = true
                setTimeout(() => {
                    signUpSuccess.value = false;
                }, 2000);

                toggleForm()
            }
        })
})

const toggleForm = () => {
    isLogin.value = !isLogin.value;
    logInError.value = false
    signUpError.value = false
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
