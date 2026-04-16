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
    if (!usuario) return null;

    try {
      return JSON.parse(usuario);
    } catch (e) {
      localStorage.removeItem('usuario');
      return null;
    }
  },

  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('usuario');
  },

  isAutenticado: () => {
    const token = localStorage.getItem('token');
    const usuario = autenticarService.obterUsuarioAtual();
    return !!token && !!usuario?.role;
  },
};

export default autenticarService;
