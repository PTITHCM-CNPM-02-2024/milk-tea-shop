import '@fortawesome/fontawesome-free/css/all.css'
import 'material-design-icons-iconfont/dist/material-design-icons.css'
import Vue from 'vue'
import {createVuetify} from 'vuetify'

export default createVuetify({
    theme:{
        defaultTheme: 'light',
        themes:{
            light : {
                background: '#EEEEEE',
            }
        }
    },
    icons :{
        sets : {
            mdi : 'mdi',
            fa : 'fa'
        }
    }
})