<template>
  <v-app>
    <v-main>
      <router-view/>
    </v-main>
  </v-app>
</template>

<script>
import Home from "@/views/Home.vue";

export default {
  name: 'App',
  data: () => ({
    //
  }),
}
</script>
