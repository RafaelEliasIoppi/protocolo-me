import api from './apiClient';

const extrairPacienteId = (protocolo) => {
  if (!protocolo) return null;
  if (protocolo.paciente?.id != null) return String(protocolo.paciente.id);
  if (protocolo.pacienteId != null) return String(protocolo.pacienteId);
  return null;
};

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
    const response = await api.get('/api/protocolos-me');
    const alvo = String(pacienteId);
    return response.data.filter((protocolo) => extrairPacienteId(protocolo) === alvo);
  },

  adicionarTesteClinico: async (protocoloId, testeClinico) => {
    const indicador = typeof testeClinico === 'number'
      ? String(testeClinico)
      : String(
          testeClinico?.numero ||
          testeClinico?.ordem ||
          testeClinico?.tipo ||
          testeClinico ||
          '1'
        );

    const endpoint = indicador.includes('2')
      ? `/api/protocolos-me/${protocoloId}/teste-clinico-2`
      : `/api/protocolos-me/${protocoloId}/teste-clinico-1`;

    const response = await api.post(endpoint);
    return response.data;
  }
};

export default protocoloService;
