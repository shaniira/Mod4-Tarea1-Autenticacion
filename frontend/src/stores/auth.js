import { defineStore } from 'pinia';
import api from '@/services/api';
function parseJwt(token) {
    try {
        const payload = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
        return JSON.parse(decodeURIComponent(escape(atob(payload))));
    }
    catch {
        return {};
    }
}
export const useAuthStore = defineStore('auth', {
    state: () => ({
        token: localStorage.getItem('token') || '',
        role: (localStorage.getItem('role') || ''),
        username: localStorage.getItem('username') || ''
    }),
    getters: {
        isAuthenticated: state => Boolean(state.token)
    },
    actions: {
        async login(username, password) {
            const { data } = await api.post('/auth/login', { username, password });
            this.setSession(data.token, username);
        },
        async loginWithGoogle(idToken) {
            const { data } = await api.post('/auth/google', { idToken });
            this.setSession(data.token, parseJwt(data.token).sub || '');
        },
        setSession(token, username) {
            const payload = parseJwt(token);
            this.token = token;
            this.role = (payload.rol || payload.role || payload.roles?.[0]?.replace('ROLE_', '') || '');
            this.username = username;
            localStorage.setItem('token', this.token);
            localStorage.setItem('role', this.role);
            localStorage.setItem('username', username);
        },
        logout() {
            this.token = '';
            this.role = '';
            this.username = '';
            localStorage.removeItem('token');
            localStorage.removeItem('role');
            localStorage.removeItem('username');
        }
    }
});
