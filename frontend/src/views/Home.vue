<script setup>

import Sidebar from "@/components/Sidebar.vue";
</script>

<template>
<v-app :style="{background : $vuetify.theme.dark.background}">
<Sidebar/>
  <v-container>
    <v-toolbar color="rgb(0,0,0,0)" flat>
      <title class="mt-n3">Chọn danh mục</title>
      <v-spacer></v-spacer>
      <v-text-field label="Chọn danh mục hoặc thực đơn"
                    class="mt-5"
                    color="brown"
                    append-icon="mdi-magnify"/>
    </v-toolbar>

  </v-container>
</v-app>
</template>

<script>
import Sidebar from "@/components/Sidebar.vue";

export default {
  name: 'Home',
  components: {
    Sidebar
  }
}
</script>
<style scoped>

</style>