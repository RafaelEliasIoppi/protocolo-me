import api from './clienteHttpService';

export const exameService = {
  listar: async (protocoloId) => {
    if (!protocoloId) {
      return [];
    }
    const response = await api.get(`/api/exames-me/protocolo/${protocoloId}`);
    return response.data;
  },

  listarPorProtocolo: async (protocoloId) => {
    const response = await api.get(`/api/exames-me/protocolo/${protocoloId}`);
    return response.data;
  },

  obter: async (id) => {
    const response = await api.get(`/api/exames-me/${id}`);
    return response.data;
  },

  criar: async (exame) => {
    const response = await api.post('/api/exames-me', exame);
    return response.data;
  },

  atualizar: async (id, exame) => {
    const response = await api.put(`/api/exames-me/${id}`, exame);
    return response.data;
  },

  deletar: async (id) => {
    const response = await api.delete(`/api/exames-me/${id}`);
    return response.data;
  },

  atualizarResultado: async (id, resultadoPositivo, responsavel = '') => {
    // O backend espera 'resultado' (String) e 'resultado_positivo' (Boolean)
    const resultado = resultadoPositivo ? 'POSITIVO' : 'NEGATIVO';

    const response = await api.post(
      `/api/exames-me/${id}/resultado`,
      {},
      {
        params: {
          resultado: resultado,
          resultado_positivo: resultadoPositivo,
          responsavel: responsavel
        }
      }
    );
    return response.data;
  },

  obterPorPaciente: async (pacienteId) => {
    const protocolosResponse = await api.get('/api/protocolos-me');
    const protocolosDoPaciente = protocolosResponse.data.filter((protocolo) => {
      const id = protocolo?.paciente?.id ?? protocolo?.pacienteId;
      return String(id) === String(pacienteId);
    });

    if (protocolosDoPaciente.length === 0) {
      return [];
    }

    const examesPorProtocolo = await Promise.all(
      protocolosDoPaciente.map((protocolo) => api.get(`/api/exames-me/protocolo/${protocolo.id}`))
    );

    return examesPorProtocolo.flatMap((response) => response.data || []);
  }
};

export default exameService;
