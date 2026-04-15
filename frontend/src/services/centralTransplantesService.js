import api from './apiClient';

export const centralTransplantesService = {
  listar: async () => {
    return api.get('/api/centrais-transplantes');
  },

  obter: async (id) => {
    return api.get(`/api/centrais-transplantes/${id}`);
  },

  criar: async (central) => {
    return api.post('/api/centrais-transplantes', central);
  },

  atualizar: async (id, central) => {
    return api.put(`/api/centrais-transplantes/${id}`, central);
  },

  deletar: async (id) => {
    return api.delete(`/api/centrais-transplantes/${id}`);
  },

  obterEstatisticas: async () => {
    return api.get('/api/centrais-transplantes/estatisticas');
  }
};

export default centralTransplantesService;
