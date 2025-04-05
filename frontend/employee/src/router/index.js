import {createRouter, createWebHistory} from "vue-router";

// Sử dụng import động để tải lazy các components
const PosView = () => import('../views/PosView.vue')
const OrdersView = () => import('../views/OrdersView.vue')
const ReportsView = () => import('../views/ReportsView.vue')

const routes = [
    {
        path : '/',
        name : 'pos',
        component : () => import('../views/PosView.vue')
    },
    {
        path : '/orders',
        name : 'orders',
        component : () => import('../views/OrdersView.vue')
    },
    {
        path : '/reports',
        name : 'reports',
        component : () => import('../views/ReportsView.vue')
    },
    // Thêm fallback route
    {
        path: '/:pathMatch(.*)*',
        redirect: '/'
    }
]

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL || '/'),
    routes
})

export default router