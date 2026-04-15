import api from './apiClient';

export const hospitalService = {
  listar: async () => {
    const response = await api.get('/api/hospitais');
    return response.data;
  },

  obter: async (id) => {
    const response = await api.get(`/api/hospitais/${id}`);
    return response.data;
  },

  criar: async (hospital) => {
    const response = await api.post('/api/hospitais', hospital);
    return response.data;
  },

  atualizar: async (id, hospital) => {
    const response = await api.put(`/api/hospitais/${id}`, hospital);
    return response.data;
  },

  deletar: async (id) => {
    const response = await api.delete(`/api/hospitais/${id}`);
    return response.data;
  },

  obterEstatisticas: async (id) => {
    const response = await api.get(`/api/hospitais/${id}/estatisticas`);
    return response.data;
  }
};

export default hospitalService;
