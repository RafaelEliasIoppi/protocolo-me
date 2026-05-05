import api from './clienteHttpService';

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

  atualizarStatus: async (id, status) => {
    const response = await api.patch(`/api/hospitais/${id}/status`, {}, {
      params: { status }
    });
    return response.data;
  },

  deletar: async (id) => {
    const response = await api.delete(`/api/hospitais/${id}`);
    return response.data;
  },

  obterEstatisticas: async (id) => {
    if (id != null) {
      const response = await api.get(`/api/hospitais/${id}`);
      return {
        total: 1,
        ativos: response.data?.status === 'ATIVO' ? 1 : 0,
        inativos: response.data?.status === 'INATIVO' ? 1 : 0,
        manutencao: response.data?.status === 'MANUTENCAO' ? 1 : 0,
        suspensao: response.data?.status === 'SUSPENSAO' ? 1 : 0,
        hospital: response.data
      };
    }

    const response = await api.get('/api/hospitais');
    const hospitais = Array.isArray(response.data) ? response.data : [];
    return {
      total: hospitais.length,
      ativos: hospitais.filter((h) => h?.status === 'ATIVO').length,
      inativos: hospitais.filter((h) => h?.status === 'INATIVO').length,
      manutencao: hospitais.filter((h) => h?.status === 'MANUTENCAO').length,
      suspensao: hospitais.filter((h) => h?.status === 'SUSPENSAO').length
    };
  }
};

export default hospitalService;
