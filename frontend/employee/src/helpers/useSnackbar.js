import { ref } from 'vue';

const snackbar = ref({
  show: false,
  message: '',
  color: 'success',
  timeout: 3000
});

export function useSnackbar() {
  const showSnackbar = (message, color = 'success', timeout = 3000) => {
    snackbar.value.message = message;
    snackbar.value.color = color;
    snackbar.value.timeout = timeout;
    snackbar.value.show = true;
  };

  return {
    snackbar,
    showSnackbar
  };
} 