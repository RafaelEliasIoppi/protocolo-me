import api from './apiClient';

export const protocoloService = {
  listar: async () => {
    return api.get('/api/protocolos-me');
  },

  obter: async (id) => {
    return api.get(`/api/protocolos-me/${id}`);
  },

  criar: async (protocolo) => {
    return api.post('/api/protocolos-me', protocolo);
  },

  atualizar: async (id, protocolo) => {
    return api.put(`/api/protocolos-me/${id}`, protocolo);
  },

  deletar: async (id) => {
    return api.delete(`/api/protocolos-me/${id}`);
  },

  obterPorPaciente: async (pacienteId) => {
    return api.get(`/api/protocolos-me/paciente/${pacienteId}`);
  },

  adicionarTesteClinico: async (protocoloId, testeClinico) => {
    return api.post(`/api/protocolos-me/${protocoloId}/teste-clinico`, testeClinico);
  }
};

export default protocoloService;
