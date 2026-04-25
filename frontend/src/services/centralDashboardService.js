import api from "./apiClient";

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

export const obterExamesPendentes = (protocolo) => {
  if (!protocolo) {
    return ["Teste clínico 1", "Teste clínico 2", "Exames complementares"];
  }

  if (Array.isArray(protocolo.exames) && protocolo.exames.length > 0) {
    const clinicosRealizados = protocolo.exames.filter(
      (e) => e?.categoria === "CLINICO" && !!e?.dataRealizacao,
    ).length;
    const complementaresRealizados = protocolo.exames.filter(
      (e) => e?.categoria === "COMPLEMENTAR" && !!e?.dataRealizacao,
    ).length;

    const pendentes = [];
    if (clinicosRealizados < 1) pendentes.push("Teste clínico 1");
    if (clinicosRealizados < 2) pendentes.push("Teste clínico 2");
    if (complementaresRealizados < 1) pendentes.push("Exames complementares");
    return pendentes;
  }

  const pendentes = [];
  if (!protocolo.testeClinico1Realizado) pendentes.push("Teste clínico 1");
  if (!protocolo.testeClinico2Realizado) pendentes.push("Teste clínico 2");
  if (!protocolo.testesComplementaresRealizados) pendentes.push("Exames complementares");
  return pendentes;
};

export const obterExamesConcluidos = (protocolo) => {
  if (!protocolo) return 0;

  if (Array.isArray(protocolo.exames) && protocolo.exames.length > 0) {
    let concluidos = 0;
    const clinicosRealizados = protocolo.exames.filter(
      (e) => e?.categoria === "CLINICO" && !!e?.dataRealizacao,
    ).length;
    const complementaresRealizados = protocolo.exames.filter(
      (e) => e?.categoria === "COMPLEMENTAR" && !!e?.dataRealizacao,
    ).length;

    if (clinicosRealizados >= 1) concluidos += 1;
    if (clinicosRealizados >= 2) concluidos += 1;
    if (complementaresRealizados >= 1) concluidos += 1;
    return concluidos;
  }

  let concluidos = 0;
  if (protocolo.testeClinico1Realizado) concluidos += 1;
  if (protocolo.testeClinico2Realizado) concluidos += 1;
  if (protocolo.testesComplementaresRealizados) concluidos += 1;
  return concluidos;
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

export const obterResumoStatusExames = (protocolo) => {
  const exames = Array.isArray(protocolo?.exames) ? protocolo.exames : [];
  const positivos = exames.filter((exame) => obterResultadoPositivo(exame) === true).length;
  const negativos = exames.filter((exame) => obterResultadoPositivo(exame) === false).length;
  const semResultado = exames.filter((exame) => {
    const resultadoPositivo = obterResultadoPositivo(exame);
    const temResultadoTexto = exame?.resultado && exame.resultado.trim() !== "";
    return resultadoPositivo === null && !temResultadoTexto;
  }).length;

  const concluidos = obterExamesConcluidos(protocolo);
  const pendentes = Math.max(3 - concluidos, 0);

  return {
    positivos,
    negativos,
    semResultado,
    concluidos,
    pendentes
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
