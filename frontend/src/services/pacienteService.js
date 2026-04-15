import api from './apiClient';

export const pacienteService = {
  listar: async (filtros = {}) => {
    let url = '/api/pacientes';
    const params = new URLSearchParams();
    if (filtros.busca) params.append('nome', filtros.busca);
    if (filtros.status) params.append('status', filtros.status);
    if (filtros.hospitalId) params.append('hospital', filtros.hospitalId);
    if (params.toString()) url += '?' + params.toString();
    return api.get(url);
  },

  obter: async (id) => {
    return api.get(`/api/pacientes/${id}`);
  },

  criar: async (paciente) => {
    return api.post('/api/pacientes', paciente);
  },

  atualizar: async (id, paciente) => {
    return api.put(`/api/pacientes/${id}`, paciente);
  },

  deletar: async (id) => {
    return api.delete(`/api/pacientes/${id}`);
  },

  atualizarStatus: async (id, status) => {
    return api.patch(`/api/pacientes/${id}/status`, { status });
  },

  obterEstatisticas: async () => {
    return api.get('/api/pacientes/estatisticas/resumo');
  },

  obterPorCpf: async (cpf) => {
    return api.get(`/api/pacientes/cpf/${cpf}`);
  },

  listarPorHospital: async (hospitalId) => {
    return api.get(`/api/pacientes/hospital/${hospitalId}`);
  },

  listarPorStatus: async (status) => {
    return api.get(`/api/pacientes/status/${status}`);
  }
};

export default pacienteService;
