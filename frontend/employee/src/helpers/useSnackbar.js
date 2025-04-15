import { ref } from 'vue';

// Sử dụng một biến duy nhất để lưu trạng thái snackbar
const snackbar = ref({
  show: false,
  message: '',
  color: 'success',
  timeout: 3000,
  icon: 'mdi-check-circle'
});

export function useSnackbar() {
  /**
   * Hiển thị thông báo snackbar
   * @param {string} message - Nội dung thông báo
   * @param {string} color - Màu sắc (success, error, info, warning)
   * @param {number} timeout - Thời gian hiển thị (ms)
   * @param {string} icon - Icon hiển thị (mdi-*)
   */
  const showSnackbar = (message, color = 'success', timeout = 3000, icon = null) => {
    // Chọn icon mặc định dựa trên màu sắc nếu không được cung cấp
    let defaultIcon = 'mdi-check-circle';
    
    if (color === 'error') defaultIcon = 'mdi-alert-circle';
    else if (color === 'info') defaultIcon = 'mdi-information';
    else if (color === 'warning') defaultIcon = 'mdi-alert';
    
    snackbar.value.message = message;
    snackbar.value.color = color;
    snackbar.value.timeout = timeout;
    snackbar.value.icon = icon || defaultIcon;
    snackbar.value.show = true;
  };

  /**
   * Hiển thị thông báo thành công
   * @param {string} message - Nội dung thông báo
   * @param {number} timeout - Thời gian hiển thị (ms)
   */
  const showSuccess = (message, timeout = 3000) => {
    showSnackbar(message, 'success', timeout, 'mdi-check-circle');
  };

  /**
   * Hiển thị thông báo lỗi
   * @param {string} message - Nội dung thông báo
   * @param {number} timeout - Thời gian hiển thị (ms)
   */
  const showError = (message, timeout = 5000) => {
    showSnackbar(message, 'error', timeout, 'mdi-alert-circle');
  };

  /**
   * Hiển thị thông báo cảnh báo
   * @param {string} message - Nội dung thông báo
   * @param {number} timeout - Thời gian hiển thị (ms)
   */
  const showWarning = (message, timeout = 4000) => {
    showSnackbar(message, 'warning', timeout, 'mdi-alert');
  };

  /**
   * Hiển thị thông báo thông tin
   * @param {string} message - Nội dung thông báo
   * @param {number} timeout - Thời gian hiển thị (ms)
   */
  const showInfo = (message, timeout = 3000) => {
    showSnackbar(message, 'info', timeout, 'mdi-information');
  };

  return {
    snackbar,
    showSnackbar,
    showSuccess,
    showError,
    showWarning,
    showInfo
  };
} 