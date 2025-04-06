<template>
  <v-card>
    <v-toolbar color="primary" dark density="compact">
      <v-toolbar-title>
        <v-icon class="mr-2">mdi-receipt</v-icon>
        Hóa đơn
      </v-toolbar-title>
      <v-spacer></v-spacer>
      <v-btn icon @click="print">
        <v-icon>mdi-printer</v-icon>
      </v-btn>
      <v-btn icon @click="close">
        <v-icon>mdi-close</v-icon>
      </v-btn>
    </v-toolbar>
    
    <v-card-text class="pa-4">
      <div id="bill-content" class="bill-container" v-html="billHtml"></div>
    </v-card-text>
    
    <v-card-actions class="pa-4">
      <v-spacer></v-spacer>
      <v-btn color="grey" variant="text" @click="close">Đóng</v-btn>
      <v-btn color="primary" @click="print">
        <v-icon class="mr-2">mdi-printer</v-icon>
        In hóa đơn
      </v-btn>
    </v-card-actions>
  </v-card>
</template>

<script setup>
import { ref } from 'vue';

const props = defineProps({
  billHtml: {
    type: String,
    default: ''
  }
});

const emit = defineEmits(['close']);

function close() {
  emit('close');
}

function print() {
  // Tạo style cho bản in
  const printStyles = `
    @page {
      size: 80mm auto;
      margin: 0mm;
    }
    body {
      font-family: 'Arial', sans-serif;
      font-size: 12px;
      margin: 0;
      padding: 8px;
    }
    table {
      width: 100%;
      border-collapse: collapse;
    }
    .text-center {
      text-align: center;
    }
    .text-right {
      text-align: right;
    }
    hr {
      border: none;
      border-top: 1px dashed #000;
    }
  `;

  // Tạo một iframe ẩn để in
  const printIframe = document.createElement('iframe');
  printIframe.style.position = 'absolute';
  printIframe.style.top = '-999px';
  printIframe.style.left = '-999px';
  printIframe.style.width = '0';
  printIframe.style.height = '0';
  printIframe.style.border = '0';
  document.body.appendChild(printIframe);
  
  // Lấy nội dung cần in
  const printContent = document.getElementById('bill-content').innerHTML;
  
  // Ghi nội dung vào iframe
  const frameDoc = printIframe.contentWindow || printIframe.contentDocument.document || printIframe.contentDocument;
  frameDoc.document.open();
  frameDoc.document.write(`
    <!DOCTYPE html>
    <html>
      <head>
        <title>Hóa đơn</title>
        <style>${printStyles}</style>
      </head>
      <body>
        ${printContent}
      </body>
    </html>
  `);
  frameDoc.document.close();
  
  // Chờ iframe load xong
  printIframe.onload = function() {
    try {
      // In iframe
      frameDoc.focus();
      frameDoc.print();
      
      // Xóa iframe sau khi in
      setTimeout(() => {
        document.body.removeChild(printIframe);
      }, 500);
    } catch (error) {
      console.error('Lỗi khi in:', error);
      // Xóa iframe ngay lập tức nếu có lỗi
      document.body.removeChild(printIframe);
    }
  };
}
</script>

<style scoped>
.bill-container {
  font-family: 'Arial', sans-serif;
  min-height: 300px;
  max-height: 600px;
  overflow-y: auto;
  background-color: white;
  padding: 0.5rem;
  border: 1px solid #eee;
}
</style>
