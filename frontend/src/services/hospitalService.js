import api from './clienteHttpService';

const normalizarListaHospitais = (dados) => {
  if (Array.isArray(dados)) return dados;
  if (Array.isArray(dados?.content)) return dados.content;
  if (Array.isArray(dados?.data)) return dados.data;
  return [];
};

export const hospitalService = {
  listar: async () => {
    try {
      const response = await api.get('/api/hospitais');
      const resultado = normalizarListaHospitais(response.data);
      return resultado;
    } catch (error) {
      console.error('[hospitalService.listar] Erro ao fazer requisição:', error?.message || error);
      throw error;
    }
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
    const hospitais = normalizarListaHospitais(response.data);
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
