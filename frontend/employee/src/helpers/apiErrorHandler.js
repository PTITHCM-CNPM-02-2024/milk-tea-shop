import { useSnackbarStore } from '../stores/snackbarStore';

/**
 * Xử lý lỗi từ API và trả về thông báo lỗi phù hợp
 * @param {Error} error - Đối tượng lỗi
 * @param {string} defaultMessage - Thông báo mặc định khi không thể xác định lỗi
 * @param {boolean} showSnackbar - Có hiển thị snackbar hay không
 * @returns {string} Thông báo lỗi đã xử lý
 */
export const handleApiError = (error, defaultMessage = 'Có lỗi xảy ra', showSnackbar = true) => {
  let errorMessage = defaultMessage;
  
  if (error.response) {
    // Lỗi có phản hồi từ server
    if (error.response.data) {
      if (typeof error.response.data === 'string') {
        errorMessage = error.response.data;
      } else if (error.response.data.message) {
        errorMessage = error.response.data.message;
      } else if (error.response.data.error) {
        errorMessage = error.response.data.error;
      }
    }
    
    // Thêm mã lỗi HTTP vào thông báo nếu đang ở chế độ development
    if (import.meta.env.DEV) {
      errorMessage += ` (HTTP ${error.response.status})`;
    }
  } else if (error.request) {
    // Lỗi không nhận được phản hồi
    errorMessage = 'Không nhận được phản hồi từ máy chủ';
  } else {
    // Lỗi khi thiết lập request
    errorMessage = error.message || defaultMessage;
  }
  
  // Log lỗi vào console
  console.error('API Error:', error);
  
  // Hiển thị snackbar nếu cần
  if (showSnackbar) {
    try {
      const snackbar = useSnackbarStore();
      snackbar.showError(errorMessage);
    } catch (e) {
      console.error('Không thể hiển thị snackbar:', e);
    }
  }
  
  return errorMessage;
};

/**
 * Thực hiện gọi API an toàn với xử lý lỗi tự động
 * @param {Function} apiCall - Hàm gọi API để thực hiện
 * @param {Object} options - Tùy chọn xử lý
 * @param {Function} options.onSuccess - Callback khi thành công
 * @param {Function} options.onError - Callback khi có lỗi
 * @param {string} options.errorMessage - Thông báo lỗi mặc định
 * @param {boolean} options.showSnackbar - Có hiển thị snackbar hay không
 * @returns {Promise} Kết quả từ API hoặc lỗi đã được xử lý
 */
export const safeApiCall = async (apiCall, options = {}) => {
  const {
    onSuccess,
    onError,
    errorMessage = 'Có lỗi xảy ra',
    showSnackbar = true
  } = options;
  
  try {
    const response = await apiCall();
    if (onSuccess && typeof onSuccess === 'function') {
      onSuccess(response);
    }
    return response;
  } catch (error) {
    const processedError = handleApiError(error, errorMessage, showSnackbar);
    
    if (onError && typeof onError === 'function') {
      onError(error, processedError);
    }
    
    return Promise.reject(error);
  }
}; 