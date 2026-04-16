import api from './apiClient';

export const centralTransplantesService = {
  listar: async () => {
    const response = await api.get('/api/centrais-transplantes');
    return response.data;
  },

  obter: async (id) => {
    const response = await api.get(`/api/centrais-transplantes/${id}`);
    return response.data;
  },

  criar: async (central) => {
    const response = await api.post('/api/centrais-transplantes', central);
    return response.data;
  },

  atualizar: async (id, central) => {
    const response = await api.put(`/api/centrais-transplantes/${id}`, central);
    return response.data;
  },

  deletar: async (id) => {
    const response = await api.delete(`/api/centrais-transplantes/${id}`);
    return response.data;
  },

  obterEstatisticas: async () => {
    const response = await api.get('/api/centrais-transplantes/estatisticas');
    return response.data;
  }
};

export default centralTransplantesService;
