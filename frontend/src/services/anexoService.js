import api from './clienteHttpService';

export const anexoService = {
  /**
   * Upload de arquivo para um exame
   */
  uploadAnexoExame: async (exameMEId, arquivo, descricao = '', uploadPor = '') => {
    const formData = new FormData();
    formData.append('arquivo', arquivo);
    if (descricao) formData.append('descricao', descricao);
    if (uploadPor) formData.append('uploadPor', uploadPor);

    const response = await api.post(
      `/api/anexos/exame/${exameMEId}`,
      formData,
      {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      }
    );
    return response.data;
  },

  /**
   * Upload de arquivo para entrevista familiar
   */
  uploadAnexoEntrevista: async (protocoloMEId, arquivo, descricao = '', uploadPor = '') => {
    const formData = new FormData();
    formData.append('arquivo', arquivo);
    if (descricao) formData.append('descricao', descricao);
    if (uploadPor) formData.append('uploadPor', uploadPor);

    const response = await api.post(
      `/api/anexos/entrevista/${protocoloMEId}`,
      formData,
      {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      }
    );
    return response.data;
  },

  /**
   * Listar anexos de um exame
   */
  listarAnexosExame: async (exameMEId) => {
    const response = await api.get(`/api/anexos/exame/${exameMEId}`);
    return response.data;
  },

  /**
   * Listar anexos de uma entrevista
   */
  listarAnexosEntrevista: async (protocoloMEId) => {
    const response = await api.get(`/api/anexos/entrevista/${protocoloMEId}`);
    return response.data;
  },

  /**
   * Obter detalhes de um anexo
   */
  obterAnexo: async (anexoId) => {
    const response = await api.get(`/api/anexos/${anexoId}`);
    return response.data;
  },

  /**
   * Download de arquivo
   */
  downloadAnexo: async (anexoId) => {
    const response = await api.get(`/api/anexos/${anexoId}/download`, {
      responseType: 'blob',
    });
    return response.data;
  },

  /**
   * Deletar anexo
   */
  deletarAnexo: async (anexoId) => {
    await api.delete(`/api/anexos/${anexoId}`);
  },

  /**
   * Deletar todos os anexos de um exame
   */
  limparAnexosExame: async (exameMEId) => {
    await api.delete(`/api/anexos/exame/${exameMEId}/limpar`);
  },

  /**
   * Helper para fazer download de arquivo
   */
  efetuarDownload: (blob, nomeArquivo) => {
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', nomeArquivo);
    document.body.appendChild(link);
    link.click();
    link.parentNode.removeChild(link);
    window.URL.revokeObjectURL(url);
  },

  /**
   * Formatar tamanho de arquivo em bytes para exibição
   */
  formatarTamanho: (bytes) => {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
  },

  /**
   * Validar arquivo antes de upload
   */
  validarArquivo: (arquivo) => {
    const EXTENSOES_PERMITIDAS = ['pdf', 'doc', 'docx', 'xls', 'xlsx', 'ppt', 'pptx', 'jpg', 'jpeg', 'png', 'gif', 'bmp', 'txt', 'csv', 'zip', 'rar'];
    const TAMANHO_MAXIMO = 20 * 1024 * 1024; // 20MB

    if (!arquivo) {
      return { valido: false, erro: 'Nenhum arquivo selecionado' };
    }

    if (arquivo.size === 0) {
      return { valido: false, erro: 'Arquivo vazio' };
    }

    if (arquivo.size > TAMANHO_MAXIMO) {
      return { valido: false, erro: 'Arquivo excede 20MB' };
    }

    const nomeArquivo = arquivo.name;
    const extensao = nomeArquivo.substring(nomeArquivo.lastIndexOf('.') + 1).toLowerCase();
    const extensaoValida = EXTENSOES_PERMITIDAS.includes(extensao);

    if (!extensaoValida) {
      return { valido: false, erro: `Tipo de arquivo não permitido: ${extensao}` };
    }

    return { valido: true };
  }
};

export default anexoService;
