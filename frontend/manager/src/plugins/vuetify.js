// Styles
import '@mdi/font/css/materialdesignicons.css'
import 'vuetify/styles'

// Vuetify
import { createVuetify } from 'vuetify'

// Định nghĩa theme màu sắc giống Materio
const lightTheme = {
  dark: false,
  colors: {
    primary: '#7367F0',      // Màu tím chính của Materio
    secondary: '#A8AAAE',    // Màu xám phụ
    accent: '#FF9F43',       // Màu cam nhấn
    error: '#EA5455',        // Màu đỏ lỗi
    info: '#00CFE8',         // Màu xanh info
    success: '#28C76F',      // Màu xanh lá success
    warning: '#FF9F43',      // Màu cam cảnh báo
    background: '#F8F7FA',   // Màu nền sáng
    surface: '#FFFFFF',      // Màu bề mặt
    'on-surface': '#4B4B4B', // Màu text trên bề mặt
    'sidebar': '#FFFFFF',    // Màu sidebar
    'card': '#FFFFFF',       // Màu card
  }
}

const darkTheme = {
  dark: true,
  colors: {
    primary: '#7367F0',      // Giữ nguyên màu tím chính
    secondary: '#A8AAAE',    // Màu xám phụ
    accent: '#FF9F43',       // Màu cam nhấn
    error: '#EA5455',        // Màu đỏ lỗi
    info: '#00CFE8',         // Màu xanh info
    success: '#28C76F',      // Màu xanh lá success
    warning: '#FF9F43',      // Màu cam cảnh báo
    background: '#25293C',   // Màu nền tối giống Materio
    surface: '#2F3349',      // Màu bề mặt tối
    'on-surface': '#E0E0E0', // Màu chữ trên bề mặt tối
    'sidebar': '#2F3349',    // Màu sidebar tối
    'card': '#2F3349',       // Màu card tối
  }
}

export default createVuetify({
  theme: {
    defaultTheme: 'light',
    themes: {
      light: lightTheme,
      dark: darkTheme
    }
  },
  defaults: {
    VBtn: {
      rounded: 'lg',
      variant: 'flat',
    },
    VCard: {
      rounded: 'lg',
      elevation: 2,
    },
    VNavigationDrawer: {
      elevation: 3,
    },
    VAppBar: {
      elevation: 0
    },
    VTextField: {
      variant: 'outlined',
      density: 'comfortable',
    },
    VSelect: {
      variant: 'outlined',
      density: 'comfortable',
    },
    VCheckbox: {
      color: 'primary',
    },
    VRadio: {
      color: 'primary',
    },
    VSwitch: {
      color: 'primary',
    },
    VAutocomplete: {
      variant: 'outlined',
      density: 'comfortable',
    }
  }
})
