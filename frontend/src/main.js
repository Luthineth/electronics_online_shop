import { createApp } from 'vue'
import App from './app.vue'
import router from './router/router.js';
import './styles/style.scss';

import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'

const vuetify = createVuetify({
    components,
    directives,
})

const app = createApp(App)

app
    .use(vuetify)
    .use(router)
    .mount('#app');