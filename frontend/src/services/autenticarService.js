import api from './apiClient';

export const autenticarService = {
  login: async (email, senha) => {
    const response = await api.post('/api/usuarios/login', { email, senha });
    if (response.data.token) {
      localStorage.setItem('token', response.data.token);
      localStorage.setItem('usuario', JSON.stringify(response.data.usuario));
    }
    return response.data;
  },

  registrar: async (usuario) => {
    const response = await api.post('/api/usuarios', usuario);
    return response.data;
  },

  obterUsuarioAtual: () => {
    const usuario = localStorage.getItem('usuario');
    return usuario ? JSON.parse(usuario) : null;
  },

  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('usuario');
  },

  isAutenticado: () => {
    return !!localStorage.getItem('token');
  },
};

export default autenticarService;
