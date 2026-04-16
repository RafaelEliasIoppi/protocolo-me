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
    const response = await api.get(`/api/protocolos-me?paciente=${pacienteId}`);
    return response.data;
  },

  registrarTesteClinico1: async (protocoloId) => {
    const response = await api.post(`/api/protocolos-me/${protocoloId}/teste-clinico-1`);
    return response.data;
  },

  registrarTesteClinico2: async (protocoloId) => {
    const response = await api.post(`/api/protocolos-me/${protocoloId}/teste-clinico-2`);
    return response.data;
  }
};

export default protocoloService;
