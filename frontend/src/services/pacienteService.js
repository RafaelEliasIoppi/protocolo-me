import api from './apiClient';

export const pacienteService = {
  listar: async (filtros = {}) => {
    let url = '/api/pacientes';
    const params = new URLSearchParams();
    if (filtros.busca) params.append('nome', filtros.busca);
    if (filtros.status) params.append('status', filtros.status);
    if (filtros.hospitalId) params.append('hospital', filtros.hospitalId);
    if (params.toString()) url += '?' + params.toString();
    const response = await api.get(url);
    return response.data;
  },

  obter: async (id) => {
    const response = await api.get(`/api/pacientes/${id}`);
    return response.data;
  },

  criar: async (paciente) => {
    const response = await api.post('/api/pacientes', paciente);
    return response.data;
  },

  atualizar: async (id, paciente) => {
    const response = await api.put(`/api/pacientes/${id}`, paciente);
    return response.data;
  },

  deletar: async (id) => {
    const response = await api.delete(`/api/pacientes/${id}`);
    return response.data;
  },

  atualizarStatus: async (id, status) => {
    const response = await api.patch(`/api/pacientes/${id}/status`, { status });
    return response.data;
  },

  obterEstatisticas: async () => {
    const response = await api.get('/api/pacientes/estatisticas/resumo');
    return response.data;
  },

  obterPorCpf: async (cpf) => {
    const response = await api.get(`/api/pacientes/cpf/${cpf}`);
    return response.data;
  },

  listarPorHospital: async (hospitalId) => {
    const response = await api.get(`/api/pacientes/hospital/${hospitalId}`);
    return response.data;
  },

  listarPorStatus: async (status) => {
    return api.get(`/api/pacientes/status/${status}`);
  },

  listarEmProtocoloME: async () => {
    const response = await api.get('/api/pacientes/em-protocolo-me');
    return response.data;
  },

  listarEmProtocoloMEPorHospital: async (hospitalId) => {
    const response = await api.get(`/api/pacientes/em-protocolo-me/hospital/${hospitalId}`);
    return response.data;
  }
};

export default pacienteService;
