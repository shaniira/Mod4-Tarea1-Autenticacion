import { defineStore } from 'pinia';
import api from '@/services/api';
import type { Role } from '@/types';

function parseJwt(token: string): Record<string, any> {
  try {
    const payload = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
    return JSON.parse(decodeURIComponent(escape(atob(payload))));
  } catch {
    return {};
  }
}

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    role: (localStorage.getItem('role') || '') as Role | '',
    username: localStorage.getItem('username') || ''
  }),
  getters: {
    isAuthenticated: state => Boolean(state.token)
  },
  actions: {
    async login(username: string, password: string) {
      const { data } = await api.post('/auth/login', { username, password });
      const payload = parseJwt(data.token);

      this.token = data.token;
      this.role = (payload.rol || payload.role || payload.roles?.[0]?.replace('ROLE_', '') || '') as Role;
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
