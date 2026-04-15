import api from './apiClient';

export const protocoloService = {
  listar: async () => {
    const response = await api.get('/api/protocolos-me');
    return response.data;
  },

  obter: async (id) => {
    const response = await api.get(`/api/protocolos-me/${id}`);
    return response.data;
  },

  criar: async (protocolo) => {
    const response = await api.post('/api/protocolos-me', protocolo);
    return response.data;
  },

  atualizar: async (id, protocolo) => {
    const response = await api.put(`/api/protocolos-me/${id}`, protocolo);
    return response.data;
  },

  deletar: async (id) => {
    const response = await api.delete(`/api/protocolos-me/${id}`);
    return response.data;
  },

  obterPorPaciente: async (pacienteId) => {
    const response = await api.get(`/api/protocolos-me/paciente/${pacienteId}`);
    return response.data;
  },

  adicionarTesteClinico: async (protocoloId, testeClinico) => {
    const response = await api.post(`/api/protocolos-me/${protocoloId}/teste-clinico`, testeClinico);
    return response.data;
  }
};

export default protocoloService;
