<script setup>

</script>

<template>
<v-navigation-drawer
    v-model="drawer" app color="white"
>
  <v-avatar class="d-block text-center mx-a mt-4" size="40">
    <v-icon color="#704232">fas fa-coffee</v-icon>
  </v-avatar>
  <v-list flat class="mt-4">
    <v-list-group v-model="selectedItem">
      <v-list-item v-for="(item, i) in items" :key="i" active-class="border" :ripple="false">
        <v-icon v-text="item.icon"></v-icon>
        <v-list-item-subtitle align="center" v-text="item.text" class="mt-3 text-capitalize"></v-list-item-subtitle>
      </v-list-item>
    </v-list-group>
  </v-list>
</v-navigation-drawer>
</template>

<script>
export default {
  data: () => ({
    drawer: true,
    selectedItem: null,
    items: [
      {icon: 'fas fa-home', text: 'Trang chủ'},
      {icon: 'fas fa-hamburger', text: 'Quản lý đơn hàng'},
      {icon: 'fas fa-coffee', text: 'Quản lý bàn'},
      {icon: 'fas fa-utensils', text: 'Báo cáo doanh thu'}
    ]
  })
}
</script>

<style scoped>

</style>