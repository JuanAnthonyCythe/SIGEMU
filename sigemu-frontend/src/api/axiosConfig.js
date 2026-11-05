import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

api.interceptors.response.use(
  (response) => response,
  (error) => {
    const isLoginEndpoint = error.config?.url?.includes('/auth/login');
    const status = error.response ? error.response.status : null;

    // IMPORTANTE: No cerrar sesión ante errores 400 (Bad Request) ni 403 (Forbidden)
    // Solo cerrar sesión si es 401 (No autorizado por token expirado/inválido) en endpoints protegidos
    if (status === 401 && !isLoginEndpoint) {
      const mensaje = error.response?.data?.mensaje || '';
      const tokenGuardado = localStorage.getItem('token');

      // Solo redirigir si el token falta o la respuesta indica expiración/token no válido
      if (
        !tokenGuardado ||
        mensaje.toLowerCase().includes('expir') ||
        mensaje.toLowerCase().includes('inválid') ||
        mensaje.toLowerCase().includes('faltante')
      ) {
        console.warn('Sesión no autorizada o token expirado. Redirigiendo a /login...');
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        if (!window.location.pathname.includes('/login')) {
          window.location.href = '/login';
        }
      }
    }

    return Promise.reject(error);
  }
);

export default api;
