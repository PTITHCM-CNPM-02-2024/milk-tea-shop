import { createApp } from 'vue'
import App from './App.vue'

// Vuetify
import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import '@mdi/font/css/materialdesignicons.css'

const vuetify = createVuetify({
    components,
    directives,
    theme: {
        defaultTheme: 'light',
        themes: {
            light: {
                colors: {
                    primary: '#8e44ad',
                    secondary: '#9b59b6',
                    accent: '#f39c12',
                    error: '#e74c3c',
                    success: '#2ecc71',
                    warning: '#f1c40f',
                    info: '#3498db',
                }
            }
        }
    }
})

createApp(App)
    .use(vuetify)
    .mount('#app')