<template>
    <div class="authorization-container">
        <v-card variant="outlined" class="authorization-card">
            <div v-if="isLogin">
                <form @submit.prevent="submit">
                    <v-text-field
                        v-model="name.value.value"
                        :counter="10"
                        :error-messages="name.errorMessage.value"
                        label="Name"
                    ></v-text-field>

                    <v-text-field
                        v-model="name.value.value"
                        :counter="10"
                        :error-messages="name.errorMessage.value"
                        label="Last Name"
                    ></v-text-field>

                    <v-text-field
                        v-model="email.value.value"
                        :error-messages="email.errorMessage.value"
                        label="E-mail"
                    ></v-text-field>

                    <v-text-field
                        v-model="name.value.value"
                        :counter="10"
                        :error-messages="name.errorMessage.value"
                        label="Password"
                    ></v-text-field>

                    <v-btn
                        class="me-4"
                        type="submit"
                    >
                        submit
                    </v-btn>

                    <v-btn @click="handleReset">
                        clear
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

            return 'Name needs to be at least 2 characters.'
        },
        phone (value) {
            if (value?.length > 9 && /[0-9-]+/.test(value)) return true

            return 'Phone number needs to be at least 9 digits.'
        },
        email (value) {
            if (/^[a-z.-]+@[a-z.-]+\.[a-z]+$/i.test(value)) return true

            return 'Must be a valid e-mail.'
        },
        select (value) {
            if (value) return true

            return 'Select an item.'
        },
        checkbox (value) {
            if (value === '1') return true

            return 'Must be checked.'
        },
    },
})
const name = useField('name')
const phone = useField('phone')
const email = useField('email')
const select = useField('select')
const checkbox = useField('checkbox')

const items = ref([
    'Item 1',
    'Item 2',
    'Item 3',
    'Item 4',
])

const submit = handleSubmit(values => {
    alert(JSON.stringify(values, null, 2))
})
const isLogin = ref(true);
const loginEmail = ref('');
const loginPassword = ref('');

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
