import api from './apiClient';

export const centralTransplantesService = {
  listar: async () => {
    return api.get('/api/centrais-transplantes');
  },

  obter: async (id) => {
    return api.get(`/api/centrais-transplantes/${id}`);
  },

  criar: async (central) => {
    return api.post('/api/centrais-transplantes', central);
  },

  atualizar: async (id, central) => {
    return api.put(`/api/centrais-transplantes/${id}`, central);
  },

  deletar: async (id) => {
    return api.delete(`/api/centrais-transplantes/${id}`);
  },

  obterEstatisticas: async () => {
    const response = await api.get('/api/centrais-transplantes');
    const centrais = Array.isArray(response.data) ? response.data : [];
    return {
      total: centrais.length,
      ativas: centrais.filter((c) => c?.statusOperacional === 'ATIVO').length,
      inativas: centrais.filter((c) => c?.statusOperacional === 'INATIVO').length,
      plantao: centrais.filter((c) => c?.statusOperacional === 'PLANTAO').length,
      manutencao: centrais.filter((c) => c?.statusOperacional === 'MANUTENCAO').length
    };
  }
};

export default centralTransplantesService;
