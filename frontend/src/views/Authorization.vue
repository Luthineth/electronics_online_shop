<template>
    <div class="authorization-container">
        <v-card variant="outlined" class="authorization-card">
            <div v-if="isLogin">
                <form @submit.prevent="submit">
                    <v-text-field
                        v-model="name.value.value"
                        :counter="10"
                        :error-messages="name.errorMessage.value"
                        label="Имя"
                        variant="underlined"
                    ></v-text-field>

                    <v-text-field
                        v-model="lastName.value.value"
                        :counter="10"
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
                        :counter="10"
                        :error-messages="password.errorMessage.value"
                        label="Password"
                        variant="underlined"
                    ></v-text-field>

                    <v-btn
                        class="me-4"
                        type="submit"
                    >
                        Сохранить
                    </v-btn>

                    <v-btn @click="handleReset">
                        Очистить
                    </v-btn>
                </form>
            </div>
            <div v-else>
                <h2>Sign Up</h2>
                <!-- Signup form -->
            </div>
            <button @click="toggleForm">{{ isLogin ? 'Switch to Sign Up' : 'Switch to Login' }}</button>
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
            if (/^[a-z.-]+@[a-z.-]+\.[a-z]+$/i.test(value)) return true

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
    display: flex;
    justify-content: center;
    align-items: center;
}
.authorization-card{
    width: 50vw;
    max-width: 500px;
    padding: 20px;
}
</style>
