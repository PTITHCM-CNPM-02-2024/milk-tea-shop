<template>
  <v-dialog
    :model-value="modelValue"
    @update:model-value="$emit('update:modelValue', $event)"
    max-width="400px"
    persistent
    transition="dialog-bottom-transition"
  >
    <v-card>
      <v-card-title class="text-h5">
        {{ title || 'Xác nhận' }}
      </v-card-title>

      <v-card-text>
        {{ message }}
      </v-card-text>

      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn
          color="grey darken-1"
          variant="text"
          @click="$emit('cancel'); $emit('update:modelValue', false)"
        >
          Hủy bỏ
        </v-btn>
        <v-btn
          color="primary"
          variant="elevated"
          @click="$emit('confirm'); $emit('update:modelValue', false)"
        >
          Xác nhận
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script setup>
defineProps({
  modelValue: Boolean, // Controls visibility
  title: String,
  message: {
    type: String,
    required: true
  }
});

defineEmits(['update:modelValue', 'confirm', 'cancel']);
</script>

<style scoped>
/* Add any specific styling if needed */
</style> 