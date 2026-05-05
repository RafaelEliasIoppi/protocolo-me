import api from './clienteHttpService';

export const pacienteService = {
  listar: async (filtros = {}) => {
    if (filtros.busca) {
      return pacienteService.buscarPorNome(filtros.busca);
    }

    if (filtros.status && filtros.hospitalId) {
      return pacienteService.listarPorHospitalEStatus(filtros.hospitalId, filtros.status);
    }

    if (filtros.status) {
      return pacienteService.listarPorStatus(filtros.status);
    }

    if (filtros.hospitalId) {
      return pacienteService.listarPorHospital(filtros.hospitalId);
    }

    const response = await api.get('/api/pacientes');
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
    const response = await api.get(`/api/pacientes/status/${status}`);
    return response.data;
  },

  buscarPorNome: async (nome) => {
    const response = await api.get(`/api/pacientes/buscar?nome=${encodeURIComponent(nome)}`);
    return response.data;
  },

  listarPorHospitalEStatus: async (hospitalId, status) => {
    const response = await api.get(`/api/pacientes/hospital/${hospitalId}/status/${status}`);
    return response.data;
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
