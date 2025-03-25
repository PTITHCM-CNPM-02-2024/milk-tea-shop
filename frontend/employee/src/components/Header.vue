<template>
  <v-app-bar color="primary" density="comfortable">
    <div class="d-flex align-center">
      <v-img
          src="/images/logo.png"
          alt="Logo trà sữa"
          class="me-3"
          max-height="40"
          max-width="40"
          contain
      ></v-img>
      <v-app-bar-title>POS Trà Sữa</v-app-bar-title>
    </div>

    <v-spacer></v-spacer>

    <div class="d-flex align-center">
      <span class="text-caption me-4">{{ currentDateTime }}</span>
      <span class="text-body-2 me-2">{{ employeeName }}</span>
      <v-avatar color="secondary" size="32">
        <v-icon color="white">mdi-account</v-icon>
      </v-avatar>
    </div>
  </v-app-bar>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';

const props = defineProps({
  employeeId: {
    type: Number,
    required: true
  },
  employeeName: {
    type: String,
    required: true
  }
});

const currentDateTime = ref('');
let timer = null;

function updateDateTime() {
  const now = new Date();
  const options = {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  };
  currentDateTime.value = now.toLocaleDateString('vi-VN', options);
}

onMounted(() => {
  updateDateTime();
  timer = setInterval(updateDateTime, 1000);
});

onBeforeUnmount(() => {
  if (timer) clearInterval(timer);
});
</script>