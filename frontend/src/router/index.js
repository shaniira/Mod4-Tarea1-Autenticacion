import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
const routes = [{ path: '/login', component: () => import('@/views/LoginView.vue'), meta: { public: true } }, { path: '/', component: () => import('@/layouts/AppLayout.vue'), children: [{ path: '', redirect: '/dashboard' }, { path: 'dashboard', component: () => import('@/views/DashboardView.vue') }, { path: 'clientes', component: () => import('@/views/ClientesView.vue') }, { path: 'tarifas', component: () => import('@/views/TarifasView.vue'), meta: { roles: ['ADMIN', 'ACTUARIO'] } }, { path: 'cotizaciones', component: () => import('@/views/CotizacionesView.vue') }, { path: 'polizas', component: () => import('@/views/PolizasView.vue') }, { path: 'renovaciones', component: () => import('@/views/RenovacionesView.vue') }] }];
const router = createRouter({ history: createWebHistory(), routes });
router.beforeEach(to => { const a = useAuthStore(); if (!to.meta.public && !a.isAuthenticated)
    return '/login'; if (to.path === '/login' && a.isAuthenticated)
    return '/dashboard'; const roles = to.meta.roles; if (roles && a.role && !roles.includes(a.role))
    return '/dashboard'; });
export default router;
