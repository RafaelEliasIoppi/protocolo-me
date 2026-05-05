import api from "./clienteHttpService";

export const STATUS_ATIVOS = [
  "NOTIFICADO",
  "EM_PROCESSO",
  "MORTE_CEREBRAL_CONFIRMADA",
  "ENTREVISTA_FAMILIAR",
  "DOACAO_AUTORIZADA",
  "FAMILIA_RECUSOU",
  "CONTRAINDICADO",
  "FINALIZADO"
];

export const normalizarChaveHospital = (nomeHospital) =>
  String(nomeHospital || "")
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .replace(/\s+/g, " ")
    .trim()
    .toLowerCase();

export const obterNomeHospital = (paciente, protocolo) => {
  return (
    protocolo?.hospitalOrigem ||
    paciente?.hospitalOrigem ||
    paciente?.hospital?.nomeHospital ||
    paciente?.hospital?.nome ||
    "N/A"
  );
};

export const obterCidadeHospitalNotificante = (paciente, protocolo, cidadesHospitaisPorNome = {}) => {
  const nomeHospitalNotificante = obterNomeHospital(paciente, protocolo);
  const chaveHospital = normalizarChaveHospital(nomeHospitalNotificante);

  if (chaveHospital && cidadesHospitaisPorNome[chaveHospital]) {
    return cidadesHospitaisPorNome[chaveHospital];
  }

  return "N/A";
};

export const construirMapaCidadesHospitais = (hospitais) => {
  return (Array.isArray(hospitais) ? hospitais : []).reduce((acc, hospital) => {
    const chave = normalizarChaveHospital(hospital?.nome || hospital?.nomeHospital);
    if (chave && hospital?.cidade) {
      acc[chave] = hospital.cidade;
    }
    return acc;
  }, {});
};

export const mapearProtocolosParaPacientes = (protocolos, statusAtivos = STATUS_ATIVOS) => {
  if (!Array.isArray(protocolos)) {
    return [];
  }

  return protocolos
    .filter((protocolo) => protocolo?.paciente?.id)
    .filter((protocolo) => statusAtivos.includes(protocolo?.status))
    .map((protocolo) => {
      const paciente = protocolo.paciente;
      return {
        ...paciente,
        protocolosME: [protocolo]
      };
    });
};

export const obterResultadoPositivo = (exame) => {
  if (typeof exame?.resultadoPositivo === "boolean") {
    return exame.resultadoPositivo;
  }

  if (typeof exame?.resultado_positivo === "boolean") {
    return exame.resultado_positivo;
  }

  return null;
};

// ✅ REVISADO: Agora verifica VALIDAÇÃO, não apenas realização
export const obterExamesPendentes = (protocolo) => {
  if (!protocolo) {
    return [
      "Teste clínico 1 (VALIDAÇÃO PENDENTE)",
      "Teste clínico 2 (VALIDAÇÃO PENDENTE)",
      "Apneia (VALIDAÇÃO PENDENTE)",
      "Exames complementares (VALIDAÇÃO PENDENTE)"
    ];
  }

  // Usar status de validação do protocolo como source of truth
  const pendentes = [];

  if (!protocolo.testeClinico1Validado) {
    pendentes.push(
      protocolo.testeClinico1Realizado
        ? "Teste clínico 1 (AGUARDANDO VALIDAÇÃO)"
        : "Teste clínico 1 (NÃO REALIZADO)"
    );
  }

  if (!protocolo.testeClinico2Validado) {
    pendentes.push(
      protocolo.testeClinico2Realizado
        ? "Teste clínico 2 (AGUARDANDO VALIDAÇÃO)"
        : "Teste clínico 2 (NÃO REALIZADO)"
    );
  }

  if (!protocolo.apneiaValidada) {
    pendentes.push(
      "Apneia (AGUARDANDO VALIDAÇÃO ou NÃO REALIZADO)"
    );
  }

  if (!protocolo.testesComplementaresValidados) {
    pendentes.push(
      protocolo.testesComplementaresRealizados
        ? "Exames complementares (AGUARDANDO VALIDAÇÃO)"
        : "Exames complementares (NÃO REALIZADO)"
    );
  }

  return pendentes;
};

// ✅ NOVO: Contar exames VALIDADOS (não apenas realizados)
// ✅ CORRIGIDO: Agora inclui apneia (total = 4, não 3)
export const obterExamesConcluidos = (protocolo) => {
  if (!protocolo) return 0;

  // Source of truth: campos de protocolo (não exames individuais)
  let validados = 0;
  if (protocolo.testeClinico1Validado) validados += 1;
  if (protocolo.testeClinico2Validado) validados += 1;
  if (protocolo.apneiaValidada) validados += 1;
  if (protocolo.testesComplementaresValidados) validados += 1;

  return validados;
};

// ✅ NOVO: Exames que estão à espera de validação
export const obterExamesAguardandoValidacao = (protocolo) => {
  if (!protocolo) return [];

  const aguardando = [];

  if (protocolo.testeClinico1Realizado && !protocolo.testeClinico1Validado) {
    aguardando.push("Teste clínico 1");
  }

  if (protocolo.testeClinico2Realizado && !protocolo.testeClinico2Validado) {
    aguardando.push("Teste clínico 2");
  }

  // Apneia é tratada internamente
  if (!protocolo.apneiaValidada) {
    aguardando.push("Apneia");
  }

  if (protocolo.testesComplementaresRealizados && !protocolo.testesComplementaresValidados) {
    aguardando.push("Exames complementares");
  }

  return aguardando;
};

// ✅ NOVO: Verificar se todos os 4 exames obrigatórios estão VALIDADOS
export const todoExamesObrigatoriosValidados = (protocolo) => {
  if (!protocolo) return false;

  return Boolean(
    protocolo.testeClinico1Validado &&
    protocolo.testeClinico2Validado &&
    protocolo.apneiaValidada &&
    protocolo.testesComplementaresValidados
  );
};

export const obterExamesRealizadosDetalhados = (protocolo) => {
  if (!protocolo || !Array.isArray(protocolo.exames)) {
    return [];
  }

  return protocolo.exames.filter((exame) => {
    const temResultadoTexto = exame?.resultado && exame.resultado.trim() !== "";
    const resultadoPositivo = obterResultadoPositivo(exame);
    const temResultadoBooleano = resultadoPositivo !== null;
    const temData = !!exame?.dataRealizacao;
    return temResultadoTexto || temResultadoBooleano || temData;
  });
};

// ✅ REVISADO: Agora mostra status de validação, não apenas resultado
export const obterResumoStatusExames = (protocolo) => {
  if (!protocolo) {
    return {
      validados: 0,
      aguardandoValidacao: 0,
      naoRealizados: 4,
      total: 4
    };
  }

  let validados = 0;
  let aguardandoValidacao = 0;
  let naoRealizados = 0;

  // Teste Clínico 1
  if (protocolo.testeClinico1Validado) validados += 1;
  else if (protocolo.testeClinico1Realizado) aguardandoValidacao += 1;
  else naoRealizados += 1;

  // Teste Clínico 2
  if (protocolo.testeClinico2Validado) validados += 1;
  else if (protocolo.testeClinico2Realizado) aguardandoValidacao += 1;
  else naoRealizados += 1;

  // Apneia
  if (protocolo.apneiaValidada) validados += 1;
  else aguardandoValidacao += 1;

  // Exames Complementares
  if (protocolo.testesComplementaresValidados) validados += 1;
  else if (protocolo.testesComplementaresRealizados) aguardandoValidacao += 1;
  else naoRealizados += 1;

  return {
    validados,
    aguardandoValidacao,
    naoRealizados,
    total: 4
  };
};

export const formatarResultadoExame = (exame) => {
  if (exame?.resultado && exame.resultado.trim() !== "") {
    return exame.resultado;
  }

  const resultadoPositivo = obterResultadoPositivo(exame);

  if (resultadoPositivo === true) {
    return "Positivo";
  }

  if (resultadoPositivo === false) {
    return "Negativo";
  }

  return "Sem resultado informado";
};

export const obterCorStatus = (status) => {
  switch (status) {
    case "NOTIFICADO":
      return "warning";
    case "EM_PROCESSO":
      return "processing";
    case "MORTE_CEREBRAL_CONFIRMADA":
      return "confirmed";
    case "ENTREVISTA_FAMILIAR":
      return "interview";
    case "DOACAO_AUTORIZADA":
      return "authorized";
    case "FAMILIA_RECUSOU":
      return "rejected";
    case "FINALIZADO":
      return "finalized";
    default:
      return "default";
  }
};

export const formatarStatusEntrevista = (status) => {
  const mapa = {
    NAO_INICIADA: "Não iniciada",
    EM_ANDAMENTO: "Em andamento",
    AUTORIZADA: "Autorizada",
    RECUSADA: "Recusada"
  };

  return mapa[status] || status || "Não iniciada";
};

export const centralDashboardService = {
  carregarDadosPainel: async () => {
    const [protocolosResp, estatisticasResp, hospitaisResp] = await Promise.all([
      api.get("/api/protocolos-me"),
      api.get("/api/centrais-transplantes/estatisticas/doadores-receptores"),
      api.get("/api/hospitais")
    ]);

    return {
      protocolos: protocolosResp.data,
      estatisticas: estatisticasResp.data,
      hospitais: hospitaisResp.data
    };
  },

  obterRelatorioFinalPaciente: async (pacienteId) => {
    const response = await api.get(`/api/pacientes/${pacienteId}/relatorio-final`);
    return response.data;
  },

  salvarRelatorioFinalProtocolo: async (protocoloId, payload) => {
    const response = await api.patch(`/api/protocolos-me/${protocoloId}/relatorio-final`, payload);
    return response.data;
  }
};

export default centralDashboardService;
