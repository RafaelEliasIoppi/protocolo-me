import api from './apiClient';

export const exameService = {
  listar: async () => {
    return api.get('/api/exames-me');
  },

  obter: async (id) => {
    return api.get(`/api/exames-me/${id}`);
  },

  criar: async (exame) => {
    return api.post('/api/exames-me', exame);
  },

  atualizar: async (id, exame) => {
    return api.put(`/api/exames-me/${id}`, exame);
  },

  deletar: async (id) => {
    return api.delete(`/api/exames-me/${id}`);
  },

  obterPorPaciente: async (pacienteId) => {
    return api.get(`/api/exames-me/paciente/${pacienteId}`);
  }
};

export default exameService;
