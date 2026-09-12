// Centraliza la URL base del backend de OAuth con Facebook (sin secretos, solo endpoints públicos).
function apiBase() {
    return import.meta.env.VITE_FACEBOOK_API_URL || 'https://mod4-tarea1-autenticacion.onrender.com/api';
}
export function facebookLoginUrl() {
    return `${apiBase()}/auth/facebook`;
}
export function facebookSessionUrl() {
    return `${apiBase()}/auth/facebook/session`;
}
