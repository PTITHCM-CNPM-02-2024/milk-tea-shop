import '@mdi/font/css/materialdesignicons.css'
import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import { aliases, mdi } from 'vuetify/iconsets/mdi'
// Định nghĩa bảng màu tùy chỉnh cho POS
const myCustomLightTheme = {
  dark: false,
  colors: {
    primary: '#1976D2',      // Màu chủ đạo - xanh dương đậm
    secondary: '#424242',    // Màu phụ - xám đậm
    accent: '#FF5722',       // Màu nhấn - cam
    success: '#4CAF50',      // Màu thành công - xanh lá
    info: '#2196F3',         // Màu thông tin - xanh dương nhạt
    warning: '#FB8C00',      // Màu cảnh báo - cam
    error: '#FF5252',        // Màu lỗi - đỏ
    background: '#F5F5F5',   // Màu nền - xám nhạt
    surface: '#FFFFFF',      // Màu bề mặt - trắng
  },
}

const vuetify = createVuetify({
    components,
    directives,
    themes: {
      myCustomLightTheme
    },
    icons: {
        defaultSet: 'mdi',
        aliases,
        sets: {
            mdi,
        },
    },
})

export default vuetify