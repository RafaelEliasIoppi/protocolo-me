import api from './apiClient';

export const hospitalService = {
  listar: async () => {
    return api.get('/api/hospitais');
  },

  obter: async (id) => {
    return api.get(`/api/hospitais/${id}`);
  },

  criar: async (hospital) => {
    return api.post('/api/hospitais', hospital);
  },

  atualizar: async (id, hospital) => {
    return api.put(`/api/hospitais/${id}`, hospital);
  },

  deletar: async (id) => {
    return api.delete(`/api/hospitais/${id}`);
  },

  obterEstatisticas: async (id) => {
    return api.get(`/api/hospitais/${id}/estatisticas`);
  }
};

export default hospitalService;
