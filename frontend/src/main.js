import { createApp } from 'vue'
import App from './app.vue'
import router from './router/router.js';
import './styles/style.scss';

import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import '@mdi/font/css/materialdesignicons.css'
import store from "./stores/store.js";

const vuetify = createVuetify({
    components,
    directives,
    icons: {
        defaultSet: 'mdi'
    },
})



const app = createApp(App)

app
    .use(vuetify)
    .use(store)
    .use(router)
    .mount('#app');