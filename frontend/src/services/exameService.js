import api from './apiClient';

export const exameService = {
  listar: async () => {
    const response = await api.get('/api/exames-me');
    return response.data;
  },

  obter: async (id) => {
    const response = await api.get(`/api/exames-me/${id}`);
    return response.data;
  },

  criar: async (exame) => {
    const response = await api.post('/api/exames-me', exame);
    return response.data;
  },

  atualizar: async (id, exame) => {
    const response = await api.put(`/api/exames-me/${id}`, exame);
    return response.data;
  },

  deletar: async (id) => {
    const response = await api.delete(`/api/exames-me/${id}`);
    return response.data;
  },

  obterPorPaciente: async (pacienteId) => {
    const response = await api.get(`/api/exames-me/paciente/${pacienteId}`);
    return response.data;
  }
};

export default exameService;
