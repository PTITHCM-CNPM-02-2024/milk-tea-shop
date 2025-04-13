<template>
  <div>
    <!-- Thông báo chào mừng và thống kê tổng quan -->
    <v-row>
      <!-- Thông báo chào mừng -->
      <v-col cols="12" md="6" lg="4">
        <v-card class="congratulation-card">
          <v-card-text>
            <div class="d-flex justify-space-between">
              <div>
                <div class="text-h6 font-weight-bold mb-1">Congratulations John! 🎉</div>
                <div class="text-subtitle-2 text-medium-emphasis mb-2">Best seller of the month</div>
                
                <div class="text-h4 font-weight-bold primary--text mb-1">$42.8k</div>
                <div class="text-body-2 mb-3">78% of target 🚀</div>
                
                <v-btn color="primary" variant="flat" prepend-icon="mdi-eye">
                  View Sales
                </v-btn>
              </div>
              <div class="d-none d-sm-flex align-self-end">
                <v-img src="@/assets/images/trophy.png" width="110"></v-img>
              </div>
            </div>
          </v-card-text>
        </v-card>
      </v-col>
      
      <!-- Thống kê giao dịch -->
      <v-col cols="12" md="6" lg="8">
        <v-card>
          <v-card-text>
            <div class="d-flex justify-space-between align-center mb-4">
              <div>
                <div class="text-h6 font-weight-bold">Transactions</div>
                <div class="d-flex align-center">
                  <span class="text-body-1">Total 48.5% Growth</span>
                  <v-icon color="amber" class="ml-1">mdi-emoticon</v-icon>
                  <span class="text-body-2 ml-1">this month</span>
                </div>
              </div>
              <v-btn icon="mdi-dots-vertical" variant="text"></v-btn>
            </div>
            
            <v-row>
              <v-col cols="6" sm="3">
                <statistics-card 
                  icon="mdi-chart-timeline-variant" 
                  title="Sales" 
                  value="245k" 
                  color="primary"
                />
              </v-col>
              <v-col cols="6" sm="3">
                <statistics-card 
                  icon="mdi-account-multiple" 
                  title="Customers" 
                  value="12.5k" 
                  color="success"
                />
              </v-col>
              <v-col cols="6" sm="3">
                <statistics-card 
                  icon="mdi-laptop" 
                  title="Products" 
                  value="1.54k" 
                  color="warning"
                />
              </v-col>
              <v-col cols="6" sm="3">
                <statistics-card 
                  icon="mdi-currency-usd" 
                  title="Revenue" 
                  value="$88k" 
                  color="info"
                />
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Biểu đồ & Thống kê -->
    <v-row class="mt-6">
      <!-- Tổng quan hàng tuần -->
      <v-col cols="12" md="4">
        <v-card height="350">
          <v-card-text>
            <div class="d-flex justify-space-between align-center mb-4">
              <div class="text-h6 font-weight-bold">Weekly Overview</div>
              <v-btn icon="mdi-dots-vertical" variant="text"></v-btn>
            </div>
            <!-- Giả lập biểu đồ cột -->
            <div class="chart-container">
              <div class="chart-placeholder d-flex align-end">
                <div v-for="(height, i) in [30, 45, 25, 60, 20, 35, 45]" :key="i" class="chart-bar mx-1" :style="{height: `${height}%`}"></div>
              </div>
            </div>
            <div class="mt-6">
              <div class="text-h4 font-weight-bold mb-1">45%</div>
              <div class="d-flex align-center">
                <span class="text-body-2">Your sales performance is</span>
                <span class="text-success font-weight-medium ml-1">45%</span>
                <v-icon color="amber" size="small" class="ml-1">mdi-emoticon</v-icon>
                <span class="text-body-2 ml-1">better compared to last month</span>
              </div>
              <v-btn block color="primary" variant="flat" class="mt-4">Details</v-btn>
            </div>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- Tổng thu nhập -->
      <v-col cols="12" md="4">
        <v-card height="350">
          <v-card-text>
            <div class="d-flex justify-space-between align-center mb-4">
              <div class="text-h6 font-weight-bold">Total Earning</div>
              <v-btn icon="mdi-dots-vertical" variant="text"></v-btn>
            </div>
            
            <div class="d-flex align-start mb-2">
              <div class="text-h3 font-weight-bold">$24,895</div>
              <div class="d-flex align-center ml-2">
                <v-icon color="success" size="small">mdi-chevron-up</v-icon>
                <span class="text-success text-caption font-weight-medium">10%</span>
              </div>
            </div>
            <div class="text-subtitle-2 text-medium-emphasis mb-4">Compared to $84,325 last year</div>
            
            <!-- Client list -->
            <div class="client-list">
              <div class="d-flex align-center justify-space-between mb-4">
                <div class="d-flex align-center">
                  <v-avatar color="primary" size="40" class="mr-3">
                    <v-icon color="white">mdi-triangle</v-icon>
                  </v-avatar>
                  <div>
                    <div class="text-subtitle-2 font-weight-medium">Zipcar</div>
                    <div class="text-caption text-medium-emphasis">Vuejs, React & HTML</div>
                  </div>
                </div>
                <div class="text-subtitle-2 font-weight-medium">$24,895.65</div>
              </div>
              
              <div class="d-flex align-center justify-space-between mb-4">
                <div class="d-flex align-center">
                  <v-avatar color="warning" size="40" class="mr-3">
                    <v-icon color="white">mdi-lightning-bolt</v-icon>
                  </v-avatar>
                  <div>
                    <div class="text-subtitle-2 font-weight-medium">Bitbank</div>
                    <div class="text-caption text-medium-emphasis">Sketch, Figma & XD</div>
                  </div>
                </div>
                <div class="text-subtitle-2 font-weight-medium">$8,650.20</div>
              </div>
              
              <div class="d-flex align-center justify-space-between mb-4">
                <div class="d-flex align-center">
                  <v-avatar color="info" size="40" class="mr-3">
                    <v-icon color="white">mdi-cog</v-icon>
                  </v-avatar>
                  <div>
                    <div class="text-subtitle-2 font-weight-medium">Aviato</div>
                    <div class="text-caption text-medium-emphasis">HTML & Angular</div>
                  </div>
                </div>
                <div class="text-subtitle-2 font-weight-medium">$1,245.80</div>
              </div>
            </div>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- Total Profit -->
      <v-col cols="12" md="4">
        <v-row>
          <v-col cols="12">
            <v-card height="170">
              <v-card-text>
                <div class="d-flex justify-space-between align-start mb-2">
                  <div>
                    <div class="text-subtitle-2 mb-1">Total Profit</div>
                    <div class="d-flex align-center">
                      <div class="text-h4 font-weight-bold">$25.6k</div>
                      <div class="d-flex align-center ml-2">
                        <v-icon color="success" size="small">mdi-chevron-up</v-icon>
                        <span class="text-success text-caption font-weight-medium">42%</span>
                      </div>
                    </div>
                    <div class="text-caption text-medium-emphasis mt-1">Weekly Project</div>
                  </div>
                  <v-avatar rounded color="grey-lighten-3" size="48">
                    <v-icon color="grey">mdi-clock-outline</v-icon>
                  </v-avatar>
                </div>
                
                <!-- Giả lập mini chart -->
                <div class="mini-chart-placeholder mt-4">
                  <svg width="100%" height="40">
                    <polyline
                      fill="none"
                      stroke="rgba(115, 103, 240, 0.8)"
                      stroke-width="2"
                      points="0,30 20,25 40,28 60,20 80,15 100,5 120,15"
                    />
                    <circle cx="120" cy="15" r="4" fill="#7367F0" />
                  </svg>
                </div>
              </v-card-text>
            </v-card>
          </v-col>
          
          <v-col cols="12">
            <v-card height="170">
              <v-card-text>
                <div class="d-flex justify-space-between align-start mb-2">
                  <div>
                    <div class="text-subtitle-2 mb-1">Sessions</div>
                    <div class="text-h4 font-weight-bold">2,856</div>
                    <div class="text-caption text-medium-emphasis mt-1">Yearly Project</div>
                  </div>
                  <v-avatar color="primary-lighten-4" size="48">
                    <v-icon color="primary">mdi-file-document-outline</v-icon>
                  </v-avatar>
                </div>
                <!-- Giả lập biểu đồ cột nhỏ -->
                <div class="d-flex align-end justify-space-between mt-4" style="height: 40px">
                  <div class="chart-bar-sm" style="height: 70%; background-color: #EA5455;"></div>
                  <div class="chart-bar-sm" style="height: 90%; background-color: #7367F0;"></div>
                  <div class="chart-bar-sm" style="height: 60%; background-color: #7367F0;"></div>
                  <div class="chart-bar-sm" style="height: 80%; background-color: #EA5455;"></div>
                </div>
              </v-card-text>
            </v-card>
          </v-col>
        </v-row>
      </v-col>
    </v-row>
  </div>
</template>

<script setup>
import StatisticsCard from '@/components/dashboard/StatisticsCard.vue'
import { ref } from 'vue'
</script>

<style scoped>
.chart-container {
  height: 150px;
  margin-bottom: 20px;
}

.chart-placeholder {
  height: 100%;
  width: 100%;
}

.chart-bar {
  width: 12%;
  border-radius: 4px;
  background-color: #7367F0;
  transition: height 0.5s;
}

.chart-bar:nth-child(4) {
  background-color: #7367F0;
}

.chart-bar:not(:nth-child(4)) {
  background-color: rgba(115, 103, 240, 0.5);
}

.chart-bar-sm {
  width: 20%;
  border-radius: 4px;
  margin: 0 4px;
}

.congratulation-card {
  background: linear-gradient(118deg, #7367F0, rgba(115, 103, 240, 0.7));
  color: white;
}

.congratulation-card .text-medium-emphasis {
  color: rgba(255, 255, 255, 0.7) !important;
}

.congratulation-card .v-btn {
  background-color: white !important;
  color: #7367F0 !important;
}
</style> 