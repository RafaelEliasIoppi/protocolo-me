import React, { useEffect, useState } from "react";
import apiClient from "../services/apiClient";
import CentralPacienteEditForm from "./CentralPacienteEditForm";
import "../styles/CentralDashboardPage.css";

const CHAVE_CONFIG_ESTATISTICA_CENTRAL = "central_dashboard_estatisticas_campos";

const obterConfiguracaoCamposPadrao = () => ({
  doadoresEmAvaliacao: true,
  doadoresAutorizados: true,
  receptoresAptos: true,
  taxaAutorizacao: true,
  totalProtocolos: true,
  recusasFamiliares: true,
  protocolosContraindicados: true,
  protocolosFinalizados: true,
  receptoresNaoAptos: true,
  tabelaOrgaosTecidos: true
});

const carregarConfiguracaoCampos = () => {
  const padrao = obterConfiguracaoCamposPadrao();

  try {
    const conteudo = localStorage.getItem(CHAVE_CONFIG_ESTATISTICA_CENTRAL);
    if (!conteudo) {
      return padrao;
    }

    const configuracao = JSON.parse(conteudo);
    if (!configuracao || typeof configuracao !== "object") {
      return padrao;
    }

    const resultado = { ...padrao };
    Object.keys(resultado).forEach((chave) => {
      if (typeof configuracao[chave] === "boolean") {
        resultado[chave] = configuracao[chave];
      }
    });

    return resultado;
  } catch (_) {
    return padrao;
  }
};

function CentralDashboardPage() {
  const estatisticasIniciais = {
    totalProtocolos: 0,
    doadoresEmAvaliacao: 0,
    doadoresAutorizados: 0,
    recusasFamiliares: 0,
    protocolosContraindicados: 0,
    protocolosFinalizados: 0,
    receptoresAptos: 0,
    receptoresNaoAptos: 0,
    orgaosTecidos: []
  };

  const [pacientes, setPacientes] = useState([]);
  const [carregando, setCarregando] = useState(false);
  const [erro, setErro] = useState("");
  const [estatisticas, setEstatisticas] = useState(estatisticasIniciais);
  const [ultimaAtualizacao, setUltimaAtualizacao] = useState(null);
  const [pacienteSelecionado, setPacienteSelecionado] = useState(null);
  const [relatorioFinalPaciente, setRelatorioFinalPaciente] = useState(null);
  const [carregandoRelatorio, setCarregandoRelatorio] = useState(false);
  const [mostrarConfigEstatistica, setMostrarConfigEstatistica] = useState(false);
  const [camposVisiveis, setCamposVisiveis] = useState(() => carregarConfiguracaoCampos());
  const [mostrarEditarPaciente, setMostrarEditarPaciente] = useState(false);
  const [pacienteParaEditar, setPacienteParaEditar] = useState(null);

  const opcoesPrincipaisEstatistica = [
    { chave: "doadoresEmAvaliacao", label: "Doadores em avaliação" },
    { chave: "doadoresAutorizados", label: "Doadores autorizados" },
    { chave: "receptoresAptos", label: "Receptores aptos" },
    { chave: "taxaAutorizacao", label: "Taxa de autorização" }
  ];

  const opcoesSecundariasEstatistica = [
    { chave: "totalProtocolos", label: "Total de protocolos" },
    { chave: "recusasFamiliares", label: "Recusas familiares" },
    { chave: "protocolosContraindicados", label: "Contraindicados" },
    { chave: "protocolosFinalizados", label: "Finalizados" },
    { chave: "receptoresNaoAptos", label: "Receptores não aptos" },
    { chave: "tabelaOrgaosTecidos", label: "Tabela de órgãos e tecidos" }
  ];

  const statusAtivos = [
    "NOTIFICADO",
    "EM_PROCESSO",
    "MORTE_CEREBRAL_CONFIRMADA",
    "ENTREVISTA_FAMILIAR",
    "DOACAO_AUTORIZADA",
    "FAMILIA_RECUSOU",
    "CONTRAINDICADO",
    "FINALIZADO"
  ];

  const obterNomeHospital = (paciente, protocolo) => {
    return (
      paciente?.hospital?.nomeHospital ||
      paciente?.hospital?.nome ||
      protocolo?.hospitalOrigem ||
      paciente?.hospitalOrigem ||
      "N/A"
    );
  };

  const mapearProtocolosParaPacientes = (protocolos) => {
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

  const carregarPacientesDoEstado = async () => {
    try {
      setCarregando(true);
      const [responseProtocolos, responseEstatisticas] = await Promise.all([
        apiClient.get("/api/protocolos-me"),
        apiClient.get("/api/centrais-transplantes/estatisticas/doadores-receptores")
      ]);

      const dados = mapearProtocolosParaPacientes(responseProtocolos.data);
      setPacientes(dados);
      setEstatisticas(responseEstatisticas.data || estatisticasIniciais);
      setUltimaAtualizacao(new Date());
      setErro("");
    } catch (e) {
      if (e.response?.status === 403) {
        setErro("Sem permissão para alterar dados. A Central possui acesso somente para verificação.");
      } else {
        setErro("Erro ao atualizar painel da central.");
      }
      setPacientes([]);
      setEstatisticas(estatisticasIniciais);
    } finally {
      setCarregando(false);
    }
  };

  const calcularTaxaAutorizacao = () => {
    const total = Number(estatisticas.doadoresAutorizados || 0) + Number(estatisticas.recusasFamiliares || 0);
    if (total === 0) {
      return "0%";
    }
    const taxa = (Number(estatisticas.doadoresAutorizados || 0) / total) * 100;
    return `${taxa.toFixed(1)}%`;
  };

  const alternarCampoVisivel = (chave) => {
    setCamposVisiveis((anterior) => ({
      ...anterior,
      [chave]: !anterior[chave]
    }));
  };

  const marcarTodosCamposEstatistica = () => {
    setCamposVisiveis((anterior) => {
      const atualizado = { ...anterior };
      Object.keys(atualizado).forEach((chave) => {
        atualizado[chave] = true;
      });
      return atualizado;
    });
  };

  const limparTodosCamposEstatistica = () => {
    setCamposVisiveis((anterior) => {
      const atualizado = { ...anterior };
      Object.keys(atualizado).forEach((chave) => {
        atualizado[chave] = false;
      });
      return atualizado;
    });
  };

  useEffect(() => {
    carregarPacientesDoEstado();
    const intervalo = setInterval(carregarPacientesDoEstado, 5000);
    return () => clearInterval(intervalo);
  }, []);

  useEffect(() => {
    localStorage.setItem(CHAVE_CONFIG_ESTATISTICA_CENTRAL, JSON.stringify(camposVisiveis));
  }, [camposVisiveis]);

  const obterExamesPendentes = (protocolo) => {
    if (!protocolo) {
      return ["Teste clínico 1", "Teste clínico 2", "Exames complementares"];
    }

    // Quando os exames vierem no payload, calcula pendências pela execução real.
    if (Array.isArray(protocolo.exames) && protocolo.exames.length > 0) {
      const clinicosRealizados = protocolo.exames.filter(
        (e) => e?.categoria === "CLINICO" && !!e?.dataRealizacao,
      ).length;
      const complementaresRealizados = protocolo.exames.filter(
        (e) => e?.categoria === "COMPLEMENTAR" && !!e?.dataRealizacao,
      ).length;

      const pendentes = [];
      // Mesma regra usada no backend para status automático.
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

  const obterExamesConcluidos = (protocolo) => {
    if (!protocolo) return 0;

    if (Array.isArray(protocolo.exames) && protocolo.exames.length > 0) {
      let concluidos = 0;
      const clinicosRealizados = protocolo.exames.filter(
        (e) => e?.categoria === "CLINICO" && !!e?.dataRealizacao,
      ).length;
      const complementaresRealizados = protocolo.exames.filter(
        (e) => e?.categoria === "COMPLEMENTAR" && !!e?.dataRealizacao,
      ).length;

      // Mesma regra usada no backend para status automático.
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

  const obterExamesRealizadosDetalhados = (protocolo) => {
    if (!protocolo || !Array.isArray(protocolo.exames)) {
      return [];
    }

    return protocolo.exames.filter((exame) => {
      const temResultadoTexto = exame?.resultado && exame.resultado.trim() !== "";
      const temResultadoBooleano = exame?.resultado_positivo !== null && exame?.resultado_positivo !== undefined;
      const temData = !!exame?.dataRealizacao;
      return temResultadoTexto || temResultadoBooleano || temData;
    });
  };

  const formatarResultadoExame = (exame) => {
    if (exame?.resultado && exame.resultado.trim() !== "") {
      return exame.resultado;
    }

    if (exame?.resultado_positivo === true) {
      return "Positivo";
    }

    if (exame?.resultado_positivo === false) {
      return "Negativo";
    }

    return "Sem resultado informado";
  };

  const obterCorStatus = (status) => {
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

  const formatarStatusEntrevista = (status) => {
    const mapa = {
      NAO_INICIADA: "Não iniciada",
      EM_ANDAMENTO: "Em andamento",
      AUTORIZADA: "Autorizada",
      RECUSADA: "Recusada"
    };

    return mapa[status] || status || "Não iniciada";
  };

  const abrirVisualizacaoSomenteLeitura = (paciente, protocolo) => {
    if (!protocolo) return;
    setRelatorioFinalPaciente(null);
    setPacienteSelecionado({
      id: paciente.id,
      nome: paciente.nome,
      cpf: paciente.cpf,
      hospital: obterNomeHospital(paciente, protocolo),
      cidade: paciente.hospital?.cidade || "N/A",
      statusEntrevistaFamiliar: paciente.statusEntrevistaFamiliar,
      protocolo
    });
  };

  const abrirEditarPaciente = (paciente) => {
    setPacienteParaEditar(paciente.id);
    setMostrarEditarPaciente(true);
  };

  const fecharEditarPaciente = () => {
    setMostrarEditarPaciente(false);
    setPacienteParaEditar(null);
    carregarPacientesDoEstado();
  };

  const carregarRelatorioFinalPaciente = async (pacienteId) => {
    if (!pacienteId) return;
    try {
      setCarregandoRelatorio(true);
      const response = await apiClient.get(`/api/pacientes/${pacienteId}/relatorio-final`);
      setRelatorioFinalPaciente(response.data);
    } catch (e) {
      setErro("Não foi possível carregar o relatório final do paciente.");
    } finally {
      setCarregandoRelatorio(false);
    }
  };

  const gerarNomeArquivoRelatorio = (relatorio, extensao) => {
    const nome = (relatorio?.nomePaciente || "paciente").toLowerCase().replace(/[^a-z0-9]+/g, "-");
    const id = relatorio?.pacienteId || "sem-id";
    return `relatorio-final-${nome}-${id}.${extensao}`;
  };

  const baixarBlob = (conteudo, tipoMime, nomeArquivo) => {
    const blob = new Blob([conteudo], { type: tipoMime });
    const url = URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = url;
    link.download = nomeArquivo;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(url);
  };

  const exportarRelatorioCSV = () => {
    if (!relatorioFinalPaciente) return;

    const linhas = [];
    linhas.push(["Paciente ID", relatorioFinalPaciente.pacienteId]);
    linhas.push(["Paciente", relatorioFinalPaciente.nomePaciente]);
    linhas.push(["CPF", relatorioFinalPaciente.cpf]);
    linhas.push(["Hospital", relatorioFinalPaciente.hospital || ""]);
    linhas.push(["Status Paciente", relatorioFinalPaciente.statusPaciente || ""]);
    linhas.push(["Status Entrevista", relatorioFinalPaciente.statusEntrevistaFamiliar || ""]);
    linhas.push(["Status Final Protocolo", relatorioFinalPaciente.statusFinalProtocolo || ""]);
    linhas.push(["Conclusao Final", relatorioFinalPaciente.conclusaoFinal || ""]);
    linhas.push([]);
    linhas.push([
      "Protocolo ID",
      "Numero",
      "Status",
      "Data Notificacao",
      "Data Confirmacao ME",
      "Total Exames",
      "Realizados",
      "Pendentes",
      "Clinicos",
      "Complementares",
      "Laboratoriais",
      "Familia Notificada",
      "Autopsia Autorizada"
    ]);

    (relatorioFinalPaciente.protocolos || []).forEach((p) => {
      linhas.push([
        p.protocoloId,
        p.numeroProtocolo || "",
        p.statusProtocolo || "",
        p.dataNotificacao || "",
        p.dataConfirmacaoME || "",
        p.totalExames,
        p.examesRealizados,
        p.examesPendentes,
        p.examesClinicosRealizados,
        p.examesComplementaresRealizados,
        p.examesLaboratoriaisRealizados,
        p.familiaNotificada ? "SIM" : "NAO",
        p.autopsiaAutorizada ? "SIM" : "NAO"
      ]);
    });

    const csv = linhas
      .map((colunas) =>
        (colunas || []).map((valor) => {
          const texto = String(valor ?? "").replace(/"/g, '""');
          return `"${texto}"`;
        }).join(";")
      )
      .join("\n");

    baixarBlob(csv, "text/csv;charset=utf-8", gerarNomeArquivoRelatorio(relatorioFinalPaciente, "csv"));
  };

  const exportarRelatorioPDF = () => {
    if (!relatorioFinalPaciente) return;

    const protocolosHtml = (relatorioFinalPaciente.protocolos || []).map((p) => `
      <tr>
        <td>${p.protocoloId ?? ""}</td>
        <td>${p.numeroProtocolo ?? ""}</td>
        <td>${p.statusProtocolo ?? ""}</td>
        <td>${p.examesRealizados ?? 0}/${p.totalExames ?? 0}</td>
        <td>${p.examesClinicosRealizados ?? 0}</td>
        <td>${p.examesComplementaresRealizados ?? 0}</td>
        <td>${p.examesLaboratoriaisRealizados ?? 0}</td>
      </tr>
    `).join("");

    const janela = window.open("", "_blank", "width=1024,height=768");
    if (!janela) return;

    janela.document.write(`
      <html>
        <head>
          <title>Relatorio Final - ${relatorioFinalPaciente.nomePaciente || "Paciente"}</title>
          <style>
            body { font-family: Arial, sans-serif; margin: 24px; color: #1f2937; }
            h1 { margin: 0 0 8px 0; }
            h2 { margin-top: 24px; }
            .meta { margin: 4px 0; }
            table { width: 100%; border-collapse: collapse; margin-top: 12px; }
            th, td { border: 1px solid #d1d5db; padding: 8px; text-align: left; font-size: 12px; }
            th { background: #f3f4f6; }
            .conclusao { padding: 12px; background: #eef2ff; border-left: 4px solid #4f46e5; margin-top: 12px; }
          </style>
        </head>
        <body>
          <h1>Relatorio Final do Paciente</h1>
          <div class="meta"><strong>Paciente:</strong> ${relatorioFinalPaciente.nomePaciente || ""}</div>
          <div class="meta"><strong>CPF:</strong> ${relatorioFinalPaciente.cpf || ""}</div>
          <div class="meta"><strong>Hospital:</strong> ${relatorioFinalPaciente.hospital || ""}</div>
          <div class="meta"><strong>Status Paciente:</strong> ${relatorioFinalPaciente.statusPaciente || ""}</div>
          <div class="meta"><strong>Status Final Protocolo:</strong> ${relatorioFinalPaciente.statusFinalProtocolo || ""}</div>
          <div class="meta"><strong>Status Entrevista:</strong> ${relatorioFinalPaciente.statusEntrevistaFamiliar || ""}</div>
          <div class="conclusao"><strong>Conclusao:</strong> ${relatorioFinalPaciente.conclusaoFinal || ""}</div>

          <h2>Protocolos</h2>
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Numero</th>
                <th>Status</th>
                <th>Exames</th>
                <th>Clinicos</th>
                <th>Complementares</th>
                <th>Laboratoriais</th>
              </tr>
            </thead>
            <tbody>
              ${protocolosHtml}
            </tbody>
          </table>
        </body>
      </html>
    `);
    janela.document.close();
    janela.focus();
    janela.print();
  };

  return (
    <section className="central-dashboard">
      <div className="brand-bar dashboard-header">
        <div>
          <h1>🏥 Painel Central de Monitoramento ME</h1>
          <p>Visão por paciente: nome, hospital e exames que ainda faltam em cada protocolo ME</p>
        </div>
        <div className="action-row">
          <button className="secondary-button" onClick={carregarPacientesDoEstado}>
            🔄 Atualizar
          </button>
        </div>
      </div>

      {erro && <div className="mensagem erro">{erro}</div>}

      <div className="panel estatisticas-central-panel">
        <header className="painel-header">
          <div>
            <h2>📊 Estatísticas da Central: Doadores e Receptores</h2>
            <p className="note">Resumo consolidado de doação por órgãos e tecidos.</p>
          </div>
          <div className="estatistica-config-actions">
            <button
              className="secondary-button estatistica-config-button"
              onClick={() => setMostrarConfigEstatistica((aberto) => !aberto)}
            >
              {mostrarConfigEstatistica ? "Ocultar opções" : "Escolher dados"}
            </button>
          </div>
        </header>

        {mostrarConfigEstatistica && (
          <div className="estatistica-config-panel">
            <h3>Selecionar dados da estatística</h3>
            <p className="note">Marque somente os indicadores que devem aparecer no painel.</p>
            <div className="estatistica-config-grid">
              {opcoesPrincipaisEstatistica.map((opcao) => (
                <label key={opcao.chave} className="estatistica-opcao">
                  <input
                    type="checkbox"
                    checked={!!camposVisiveis[opcao.chave]}
                    onChange={() => alternarCampoVisivel(opcao.chave)}
                  />
                  <span>{opcao.label}</span>
                </label>
              ))}
              {opcoesSecundariasEstatistica.map((opcao) => (
                <label key={opcao.chave} className="estatistica-opcao">
                  <input
                    type="checkbox"
                    checked={!!camposVisiveis[opcao.chave]}
                    onChange={() => alternarCampoVisivel(opcao.chave)}
                  />
                  <span>{opcao.label}</span>
                </label>
              ))}
            </div>
            <div className="action-row">
              <button className="secondary-button" onClick={marcarTodosCamposEstatistica}>Marcar todos</button>
              <button className="secondary-button" onClick={limparTodosCamposEstatistica}>Limpar todos</button>
            </div>
          </div>
        )}

        {!Object.values(camposVisiveis).some(Boolean) && (
          <p className="note">Nenhum dado selecionado. Use Escolher dados para montar sua estatística.</p>
        )}

        <div className="estatisticas-grid">
          {camposVisiveis.doadoresEmAvaliacao && (
            <article className="estatistica-card destaque-doador">
              <h3>Doadores em avaliação</h3>
              <p className="valor-estatistica">{estatisticas.doadoresEmAvaliacao || 0}</p>
              <span className="note">Protocolos com processo ainda em andamento</span>
            </article>
          )}
          {camposVisiveis.doadoresAutorizados && (
            <article className="estatistica-card destaque-autorizado">
              <h3>Doadores autorizados</h3>
              <p className="valor-estatistica">{estatisticas.doadoresAutorizados || 0}</p>
              <span className="note">Autorização familiar confirmada</span>
            </article>
          )}
          {camposVisiveis.receptoresAptos && (
            <article className="estatistica-card destaque-receptor">
              <h3>Receptores aptos</h3>
              <p className="valor-estatistica">{estatisticas.receptoresAptos || 0}</p>
              <span className="note">Pacientes com status APTO_TRANSPLANTE</span>
            </article>
          )}
          {camposVisiveis.taxaAutorizacao && (
            <article className="estatistica-card destaque-recusas">
              <h3>Taxa de autorização</h3>
              <p className="valor-estatistica">{calcularTaxaAutorizacao()}</p>
              <span className="note">Com base em autorizadas x recusas familiares</span>
            </article>
          )}
        </div>

        <div className="estatisticas-grid secundario">
          {camposVisiveis.totalProtocolos && (
            <article className="estatistica-card mini">
              <h3>Total de protocolos</h3>
              <p className="valor-estatistica">{estatisticas.totalProtocolos || 0}</p>
            </article>
          )}
          {camposVisiveis.recusasFamiliares && (
            <article className="estatistica-card mini">
              <h3>Recusas familiares</h3>
              <p className="valor-estatistica">{estatisticas.recusasFamiliares || 0}</p>
            </article>
          )}
          {camposVisiveis.protocolosContraindicados && (
            <article className="estatistica-card mini">
              <h3>Contraindicados</h3>
              <p className="valor-estatistica">{estatisticas.protocolosContraindicados || 0}</p>
            </article>
          )}
          {camposVisiveis.protocolosFinalizados && (
            <article className="estatistica-card mini">
              <h3>Finalizados</h3>
              <p className="valor-estatistica">{estatisticas.protocolosFinalizados || 0}</p>
            </article>
          )}
          {camposVisiveis.receptoresNaoAptos && (
            <article className="estatistica-card mini">
              <h3>Receptores não aptos</h3>
              <p className="valor-estatistica">{estatisticas.receptoresNaoAptos || 0}</p>
            </article>
          )}
        </div>

        {camposVisiveis.tabelaOrgaosTecidos && (
          <div className="orgaos-tecidos-section">
            <h3>Órgãos e Tecidos para Doação (todos os itens)</h3>
            <div className="pacientes-tabela-wrapper orgaos-tecidos-wrapper">
              <table className="tabela-pacientes tabela-orgaos-tecidos">
                <thead>
                  <tr>
                    <th>Órgão / Tecido</th>
                    <th>Total de Protocolos com Indicação</th>
                  </tr>
                </thead>
                <tbody>
                  {(estatisticas.orgaosTecidos || []).map((item) => (
                    <tr key={`orgao-tecido-${item.nome}`}>
                      <td data-label="Órgão / Tecido">{item.nome}</td>
                      <td data-label="Total">{item.total}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}
      </div>

      {/* Lista de Pacientes */}
      <div className="panel pacientes-painel">
        <header className="painel-header">
          <div>
            <h2>👥 Pacientes com Protocolo de ME Iniciado ({pacientes.length})</h2>
            <p className="note">
              {ultimaAtualizacao
                ? `🔄 Atualizado em: ${ultimaAtualizacao.toLocaleTimeString("pt-BR")}`
                : "Carregando..."}
            </p>
            <p className="note central-note-readonly">🔒 Central em modo somente leitura (apenas verificação)</p>
          </div>
        </header>

        {carregando ? (
          <p className="loading-message">⏳ Atualizando painel em tempo real...</p>
        ) : pacientes.length === 0 ? (
          <p className="no-data-message">✓ Nenhum paciente em protocolo de ME no momento.</p>
        ) : (
          <div className="pacientes-tabela-wrapper">
            <table className="tabela-pacientes">
              <thead>
                <tr>
                  <th className="col-nome">Nome</th>
                  <th className="col-cpf">CPF</th>
                  <th className="col-hospital">Hospital</th>
                  <th className="col-cidade">Cidade</th>
                  <th className="col-data">Data Notificação</th>
                  <th className="col-exames">Exames (Concluídos)</th>
                  <th className="col-faltantes">Exames Faltantes</th>
                  <th className="col-status">Status</th>
                </tr>
              </thead>
              <tbody>
                {pacientes.map((paciente) => {
                  const protocolo = paciente.protocolosME?.[0];
                  const examesPendentes = obterExamesPendentes(protocolo);
                  const examesConcluidos = obterExamesConcluidos(protocolo);
                  return (
                    <tr 
                      key={paciente.id} 
                      className={`row-status-${obterCorStatus(protocolo?.status)}`}
                      onClick={() => abrirVisualizacaoSomenteLeitura(paciente, protocolo)}
                      style={{ cursor: protocolo ? "pointer" : "default" }}
                    >
                      <td className="col-nome" data-label="Nome">
                        <strong>{paciente.nome}</strong>
                      </td>
                      <td className="col-cpf" data-label="CPF">{paciente.cpf}</td>
                      <td className="col-hospital" data-label="Hospital">{obterNomeHospital(paciente, protocolo)}</td>
                      <td className="col-cidade" data-label="Cidade">{paciente.hospital?.cidade || "N/A"}</td>
                      <td className="col-data" data-label="Data Notificação">
                        {protocolo?.dataNotificacao
                          ? new Date(protocolo.dataNotificacao).toLocaleDateString("pt-BR")
                          : "N/A"}
                      </td>
                      <td className="col-exames" data-label="Exames (Concluídos)">
                        <strong>{examesConcluidos}/3</strong>
                      </td>
                      <td className="col-faltantes" data-label="Exames Faltantes">
                        {examesPendentes.length === 0 ? (
                          <span className="pendencia-ok">Todos concluídos</span>
                        ) : (
                          <ul className="lista-faltantes">
                            {examesPendentes.map((item) => (
                              <li key={`${paciente.id}-${item}`}>{item}</li>
                            ))}
                          </ul>
                        )}
                      </td>
                      <td className="col-status" data-label="Status">
                        <span
                          className={`status-badge status-${obterCorStatus(protocolo?.status)}`}
                        >
                          {protocolo?.status?.replace(/_/g, " ") || "SEM STATUS"}
                        </span>
                        <div className="status-subinfo">
                          Entrevista: {formatarStatusEntrevista(paciente.statusEntrevistaFamiliar)}
                        </div>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Modal de Detalhes do Protocolo */}
      {pacienteSelecionado && (
        <div className="modal-overlay" onClick={() => setPacienteSelecionado(null)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>Verificação de Exames (Somente Leitura)</h2>
              <button className="modal-close" onClick={() => setPacienteSelecionado(null)}>✕</button>
            </div>
            <div className="modal-body">
              <div className="readonly-alert">
                🔒 Perfil Central: sem permissão para alterar resultados, status ou entrevista.
              </div>

              <div className="readonly-grid">
                <div>
                  <strong>Paciente:</strong> {pacienteSelecionado.nome}
                </div>
                <div>
                  <strong>CPF:</strong> {pacienteSelecionado.cpf}
                </div>
                <div>
                  <strong>Hospital:</strong> {pacienteSelecionado.hospital}
                </div>
                <div>
                  <strong>Cidade:</strong> {pacienteSelecionado.cidade}
                </div>
                <div>
                  <strong>Status:</strong>{" "}
                  <span className={`status-badge status-${obterCorStatus(pacienteSelecionado.protocolo?.status)}`}>
                    {pacienteSelecionado.protocolo?.status?.replace(/_/g, " ") || "SEM STATUS"}
                  </span>
                </div>
                <div>
                  <strong>Exames Concluídos:</strong> {obterExamesConcluidos(pacienteSelecionado.protocolo)}/3
                </div>
                <div>
                  <strong>Resumo da Entrevista:</strong> {formatarStatusEntrevista(pacienteSelecionado.statusEntrevistaFamiliar)}
                </div>
              </div>

              <div className="action-row" style={{ justifyContent: "flex-start", margin: "1rem 0" }}>
                <button
                  className="secondary-button"
                  onClick={() => carregarRelatorioFinalPaciente(pacienteSelecionado.id)}
                  disabled={carregandoRelatorio}
                >
                  {carregandoRelatorio ? "Gerando relatório..." : "Gerar Relatório Final"}
                </button>
              </div>

              {relatorioFinalPaciente && (
                <div className="readonly-pendencias">
                  <h3>Relatório Final do Paciente</h3>
                  <div className="action-row relatorio-actions">
                    <button className="modal-report-button" onClick={exportarRelatorioCSV}>Exportar CSV</button>
                    <button className="modal-report-button" onClick={exportarRelatorioPDF}>Exportar PDF</button>
                  </div>
                  <div className="readonly-grid">
                    <div><strong>Paciente:</strong> {relatorioFinalPaciente.nomePaciente}</div>
                    <div><strong>CPF:</strong> {relatorioFinalPaciente.cpf}</div>
                    <div><strong>Status Paciente:</strong> {relatorioFinalPaciente.statusPaciente}</div>
                    <div><strong>Status Final Protocolo:</strong> {relatorioFinalPaciente.statusFinalProtocolo}</div>
                    <div><strong>Total Protocolos:</strong> {relatorioFinalPaciente.totalProtocolos}</div>
                    <div><strong>Entrevista:</strong> {formatarStatusEntrevista(relatorioFinalPaciente.statusEntrevistaFamiliar)}</div>
                  </div>
                  <p className="note" style={{ marginTop: "0.75rem" }}>
                    <strong>Conclusão:</strong> {relatorioFinalPaciente.conclusaoFinal}
                  </p>
                  {Array.isArray(relatorioFinalPaciente.protocolos) && relatorioFinalPaciente.protocolos.length > 0 && (
                    <ul className="lista-faltantes">
                      {relatorioFinalPaciente.protocolos.map((protocoloResumo) => (
                        <li key={`relatorio-protocolo-${protocoloResumo.protocoloId}`}>
                          <strong>{protocoloResumo.numeroProtocolo || `Protocolo ${protocoloResumo.protocoloId}`}</strong>
                          {` - ${protocoloResumo.statusProtocolo}`}
                          {` | Exames: ${protocoloResumo.examesRealizados}/${protocoloResumo.totalExames}`}
                          {` | Clínicos: ${protocoloResumo.examesClinicosRealizados}`}
                          {` | Complementares: ${protocoloResumo.examesComplementaresRealizados}`}
                          {` | Laboratoriais: ${protocoloResumo.examesLaboratoriaisRealizados}`}
                        </li>
                      ))}
                    </ul>
                  )}
                </div>
              )}

              <div className="readonly-checklist">
                <h3>Checklist de Exames</h3>
                <ul>
                  <li>
                    {pacienteSelecionado.protocolo?.testeClinico1Realizado ? "✅" : "⏳"} Teste clínico 1
                  </li>
                  <li>
                    {pacienteSelecionado.protocolo?.testeClinico2Realizado ? "✅" : "⏳"} Teste clínico 2
                  </li>
                  <li>
                    {pacienteSelecionado.protocolo?.testesComplementaresRealizados ? "✅" : "⏳"} Exames complementares
                  </li>
                </ul>
              </div>

              <div className="readonly-pendencias">
                <h3>Exames Faltantes</h3>
                {obterExamesPendentes(pacienteSelecionado.protocolo).length === 0 ? (
                  <p className="pendencia-ok">Todos concluídos.</p>
                ) : (
                  <ul className="lista-faltantes">
                    {obterExamesPendentes(pacienteSelecionado.protocolo).map((item) => (
                      <li key={`pendente-${pacienteSelecionado.id}-${item}`}>{item}</li>
                    ))}
                  </ul>
                )}
              </div>

              <div className="readonly-pendencias">
                <h3>Exames Realizados (com resultado)</h3>
                {obterExamesRealizadosDetalhados(pacienteSelecionado.protocolo).length === 0 ? (
                  <p className="note">Nenhum exame com resultado registrado até o momento.</p>
                ) : (
                  <ul className="lista-faltantes">
                    {obterExamesRealizadosDetalhados(pacienteSelecionado.protocolo).map((exame) => (
                      <li key={`realizado-${pacienteSelecionado.id}-${exame.id}`}>
                        <strong>{exame.descricao || exame.tipoExame}</strong>
                        {` - ${formatarResultadoExame(exame)}`}
                        {exame.responsavel ? ` (Resp.: ${exame.responsavel})` : ""}
                        {exame.dataRealizacao
                          ? ` - ${new Date(exame.dataRealizacao).toLocaleString("pt-BR")}`
                          : ""}
                      </li>
                    ))}
                  </ul>
                )}
              </div>
            </div>
          </div>
        </div>
      )}
    </section>
  );
}

export default CentralDashboardPage;
