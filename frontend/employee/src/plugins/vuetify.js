import '@fortawesome/fontawesome-free/css/all.css'
import 'material-design-icons-iconfont/dist/material-design-icons.css'
import Vue from 'vue'
import {createVuetify} from 'vuetify'
import {components, directives} from 'vuetify/dist/vuetify.js'

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

export default vuetify