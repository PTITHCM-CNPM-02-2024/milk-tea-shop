import '@mdi/font/css/materialdesignicons.css'
import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import { aliases, mdi } from 'vuetify/iconsets/mdi'


// Định nghĩa bảng màu tùy chỉnh cho POS
const lightTheme = {
  dark: false,
  colors: {
    primary: '#1976D2',      // Màu chủ đạo - xanh dương đậm
    'primary-rgb': '25, 118, 210', // Giá trị RGB tương ứng
    secondary: '#424242',    // Màu phụ - xám đậm
    'secondary-rgb': '66, 66, 66',
    accent: '#FF5722',       // Màu nhấn - cam
    'accent-rgb': '255, 87, 34',
    success: '#4CAF50',      // Màu thành công - xanh lá
    'success-rgb': '76, 175, 80',
    info: '#2196F3',         // Màu thông tin - xanh dương nhạt
    'info-rgb': '33, 150, 243',
    warning: '#FB8C00',      // Màu cảnh báo - cam
    'warning-rgb': '251, 140, 0',
    error: '#FF5252',        // Màu lỗi - đỏ
    'error-rgb': '255, 82, 82',
    background: '#F5F5F5',   // Màu nền - xám nhạt
    'background-rgb': '245, 245, 245',
    surface: '#FFFFFF',      // Màu bề mặt - trắng
    'surface-rgb': '255, 255, 255',
    'on-surface': '#333333', // Màu chữ trên surface
    'on-surface-rgb': '51, 51, 51',
    'border-opacity': '0, 0, 0', // Màu viền
    'shadow-opacity': '0, 0, 0'  // Màu bóng
  },
}

// Định nghĩa bảng màu tối cho POS
const darkTheme = {
  dark: true,
  colors: {
    primary: '#2196F3',      // Màu chủ đạo - xanh dương
    'primary-rgb': '33, 150, 243', // Giá trị RGB tương ứng
    secondary: '#757575',    // Màu phụ - xám
    'secondary-rgb': '117, 117, 117',
    accent: '#FF9800',       // Màu nhấn - cam nhạt
    'accent-rgb': '255, 152, 0',
    success: '#66BB6A',      // Màu thành công - xanh lá
    'success-rgb': '102, 187, 106',
    info: '#42A5F5',         // Màu thông tin - xanh dương
    'info-rgb': '66, 165, 245',
    warning: '#FFA726',      // Màu cảnh báo - cam nhạt
    'warning-rgb': '255, 167, 38',
    error: '#EF5350',        // Màu lỗi - đỏ nhạt
    'error-rgb': '239, 83, 80',
    background: '#121212',   // Màu nền - đen
    'background-rgb': '18, 18, 18',
    surface: '#1E1E1E',      // Màu bề mặt - xám đen
    'surface-rgb': '30, 30, 30',
    'on-surface': '#EEEEEE', // Màu chữ trên surface
    'on-surface-rgb': '238, 238, 238',
    'border-opacity': '255, 255, 255', // Màu viền (trắng trong dark mode)
    'shadow-opacity': '255, 255, 255'  // Màu bóng (trắng trong dark mode)
  },
}

const vuetify = createVuetify({
    components,
    directives,
    theme: {
      defaultTheme: 'lightTheme',
      themes: {
        lightTheme,
        darkTheme
      }
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