import { afterEach, describe, expect, it, vi } from 'vitest';
import { facebookLoginUrl, facebookSessionUrl } from './facebookAuth';

afterEach(() => {
  vi.unstubAllEnvs();
});

describe('facebookAuth urls', () => {
  it('usa la URL configurada por variable de entorno', () => {
    vi.stubEnv('VITE_FACEBOOK_API_URL', 'https://backend.andina.pe/api');

    expect(facebookLoginUrl()).toBe('https://backend.andina.pe/api/auth/facebook');
    expect(facebookSessionUrl()).toBe('https://backend.andina.pe/api/auth/facebook/session');
  });

  it('usa la API desplegada como valor por defecto', () => {
    vi.stubEnv('VITE_FACEBOOK_API_URL', '');

    expect(facebookLoginUrl()).toBe('https://mod4-tarea1-autenticacion.onrender.com/api/auth/facebook');
    expect(facebookSessionUrl()).toBe('https://mod4-tarea1-autenticacion.onrender.com/api/auth/facebook/session');
  });
});
