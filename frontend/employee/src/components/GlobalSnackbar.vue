<template>
  <div class="snackbar-container">
    <Transition name="fade">
      <div 
        v-if="snackbar.show" 
        class="snackbar" 
        :class="[
          `snackbar--${snackbar.color}`,
          `snackbar--${snackbar.location}`
        ]"
      >
        <div class="snackbar__content">
          <div class="snackbar__message">{{ snackbar.message }}</div>
          <button class="snackbar__close" @click="snackbar.hideMessage">
            <span>&times;</span>
          </button>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script>
import { useSnackbarStore } from '@/stores/snackbarStore';
import { mapState, mapActions } from 'pinia';

export default {
  name: 'GlobalSnackbar',
  computed: {
    ...mapState(useSnackbarStore, ['show', 'message', 'color', 'location']),
    snackbar() {
      return useSnackbarStore();
    }
  },
  methods: {
    ...mapActions(useSnackbarStore, [
      'showMessage',
      'showSuccess',
      'showError',
      'showWarning',
      'hideMessage'
    ])
  }
}
</script>

<style scoped>
.snackbar-container {
  position: fixed;
  z-index: 9999;
  pointer-events: none;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  overflow: hidden;
}

.snackbar {
  position: absolute;
  pointer-events: auto;
  min-width: 250px;
  max-width: 80%;
  border-radius: 4px;
  padding: 12px;
  margin: 16px;
  box-shadow: 0 3px 5px rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease;
}

.snackbar--bottom {
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
}

.snackbar--top {
  top: 0;
  left: 50%;
  transform: translateX(-50%);
}

.snackbar--left {
  left: 0;
  top: 50%;
  transform: translateY(-50%);
}

.snackbar--right {
  right: 0;
  top: 50%;
  transform: translateY(-50%);
}

.snackbar__content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.snackbar__message {
  flex: 1;
}

.snackbar__close {
  background: transparent;
  border: none;
  color: white;
  font-size: 20px;
  cursor: pointer;
  margin-left: 12px;
  opacity: 0.8;
}

.snackbar__close:hover {
  opacity: 1;
}

.snackbar--info {
  background-color: #2196F3;
  color: white;
}

.snackbar--success {
  background-color: #4CAF50;
  color: white;
}

.snackbar--warning {
  background-color: #FFC107;
  color: #333;
}

.snackbar--error {
  background-color: #F44336;
  color: white;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s, transform 0.3s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(30px) translateX(-50%);
}
</style> 