<template>
  <v-container>
    <h2 class="mb-5">Báo cáo</h2>
    
    <v-row>
      <v-col cols="12" md="6" lg="3">
        <v-card class="mb-4" variant="outlined">
          <v-card-text class="d-flex align-center">
            <div>
              <div class="text-overline">Hôm nay</div>
              <div class="text-h5 mb-1">1,200,000đ</div>
              <div class="text-caption">15 đơn hàng</div>
            </div>
            <v-spacer></v-spacer>
            <v-avatar color="primary" size="56">
              <v-icon color="white" size="28">mdi-cash-register</v-icon>
            </v-avatar>
          </v-card-text>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="6" lg="3">
        <v-card class="mb-4" variant="outlined">
          <v-card-text class="d-flex align-center">
            <div>
              <div class="text-overline">Tuần này</div>
              <div class="text-h5 mb-1">5,400,000đ</div>
              <div class="text-caption">82 đơn hàng</div>
            </div>
            <v-spacer></v-spacer>
            <v-avatar color="success" size="56">
              <v-icon color="white" size="28">mdi-chart-timeline-variant</v-icon>
            </v-avatar>
          </v-card-text>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="6" lg="3">
        <v-card class="mb-4" variant="outlined">
          <v-card-text class="d-flex align-center">
            <div>
              <div class="text-overline">Tháng này</div>
              <div class="text-h5 mb-1">20,500,000đ</div>
              <div class="text-caption">320 đơn hàng</div>
            </div>
            <v-spacer></v-spacer>
            <v-avatar color="info" size="56">
              <v-icon color="white" size="28">mdi-calendar-month</v-icon>
            </v-avatar>
          </v-card-text>
        </v-card>
      </v-col>
      
      <v-col cols="12" md="6" lg="3">
        <v-card class="mb-4" variant="outlined">
          <v-card-text class="d-flex align-center">
            <div>
              <div class="text-overline">Sản phẩm bán chạy</div>
              <div class="text-h5 mb-1">Trà sữa trân châu</div>
              <div class="text-caption">150 cốc</div>
            </div>
            <v-spacer></v-spacer>
            <v-avatar color="warning" size="56">
              <v-icon color="white" size="28">mdi-trophy-variant</v-icon>
            </v-avatar>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
    
    <div class="text-center mt-8">
      <p class="text-body-1">Chức năng báo cáo chi tiết đang được phát triển</p>
    </div>
  </v-container>
</template>

<script setup>
// Không cần logic phức tạp
</script> 