<template>
  <Pie
    :data="chartData"
    :options="chartOptions"
    :height="height"
  />
</template>

<script setup>
import { ref, computed } from 'vue';
import { Pie } from 'vue-chartjs';
import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  ArcElement,
  CategoryScale
} from 'chart.js';

// Đăng ký các thành phần cần thiết
ChartJS.register(
  Title,
  Tooltip,
  Legend,
  ArcElement,
  CategoryScale
);

const props = defineProps({
  chartData: {
    type: Object,
    required: true
  },
  options: {
    type: Object,
    default: () => ({})
  },
  height: {
    type: Number,
    default: 200
  }
});

// Tùy chọn mặc định
const defaultOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      position: 'right',
    },
    tooltip: {
      callbacks: {
        label: function(context) {
          let label = context.label || '';
          if (label) {
            label += ': ';
          }
          
          const value = context.parsed;
          const total = context.dataset.data.reduce((a, b) => a + b, 0);
          const percentage = Math.round((value * 100) / total);
          
          if (value !== null) {
            label += new Intl.NumberFormat('vi-VN', {
              style: 'currency',
              currency: 'VND'
            }).format(value) + ` (${percentage}%)`;
          }
          return label;
        }
      }
    }
  }
};

// Kết hợp tùy chọn mặc định với tùy chọn từ props
const chartOptions = computed(() => {
  return {
    ...defaultOptions,
    ...props.options
  };
});
</script> 