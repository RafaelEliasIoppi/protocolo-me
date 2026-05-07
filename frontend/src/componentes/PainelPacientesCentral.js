import { useEffect, useState } from "react";
import {
    formatarResultadoExame,
    formatarStatusEntrevista,
    obterCidadeHospitalNotificante,
    obterCorStatus,
    obterExamesConcluidos,
    obterExamesPendentes,
    obterExamesRealizadosDetalhados,
    obterNomeHospital,
    obterResumoStatusExames,
} from "../services/centralDashboardService";
import exameService from "../services/exameService";
import protocoloService from "../services/protocoloService";
import { formatarCpf } from "../utils/cpf";
import GerenciadorAnexos from "./GerenciadorAnexos";

const cabecalhoSecao = "## [SECAO] ";

const parseSecoesRelatorio = (texto) => {
  const conteudo = String(texto || "").trim();

  if (!conteudo) {
    return [{ titulo: "Conclusao", conteudo: "" }];
  }

  const linhas = conteudo.split("\n");
  const secoes = [];
  let secaoAtual = null;

  linhas.forEach((linha) => {
    if (linha.startsWith(cabecalhoSecao)) {
      if (secaoAtual) {
        secaoAtual.conteudo = secaoAtual.conteudo.join("\n").trim();
        secoes.push(secaoAtual);
      }

      secaoAtual = {
        titulo: linha.replace(cabecalhoSecao, "").trim() || "Secao sem titulo",
        conteudo: []
      };
      return;
    }

    if (!secaoAtual) {
      secaoAtual = { titulo: "Conclusao", conteudo: [] };
    }

    secaoAtual.conteudo.push(linha);
  });

  if (secaoAtual) {
    secaoAtual.conteudo = secaoAtual.conteudo.join("\n").trim();
    secoes.push(secaoAtual);
  }

  return secoes.length > 0 ? secoes : [{ titulo: "Conclusao", conteudo: "" }];
};

const serializarSecoesRelatorio = (secoes) => {
  const lista = Array.isArray(secoes) ? secoes : [];
  return lista
    .map((secao, indice) => {
      const titulo = String(secao?.titulo || `Secao ${indice + 1}`).trim() || `Secao ${indice + 1}`;
      const conteudo = String(secao?.conteudo || "").trim();
      return `${cabecalhoSecao}${titulo}\n${conteudo}`.trim();
    })
    .filter(Boolean)
    .join("\n\n");
};

const formatarStatusValidacaoExame = (status) => {
  switch (status) {
    case "VALIDADO":
      return "Validado";
    case "REJEITADO":
      return "Rejeitado";
    case "REALIZADO":
      return "Realizado";
    case "PENDENTE":
      return "Pendente";
    default:
      return status || "Indefinido";
  }
};

// ✅ Função para retornar classe CSS dinâmica baseada no status
const obterClasseBadgeStatusValidacao = (status) => {
  switch (status) {
    case "VALIDADO":
      return "badge-exame badge-exame-validado";
    case "REJEITADO":
      return "badge-exame badge-exame-rejeitado";
    case "REALIZADO":
      return "badge-exame badge-exame-realizado";
    case "PENDENTE":
    default:
      return "badge-exame badge-exame-pendente";
  }
};

function PainelPacientesCentral({
  modoTelao,
  pacientes,
  pacientesFiltrados,
  pacientesExibidos,
  paginaAtualTelao,
  totalPaginasTelao,
  ultimaAtualizacao,
  cidadesHospitaisPorNome,
  carregando,
  abrirVisualizacaoSomenteLeitura,
  pacienteSelecionado,
  setPacienteSelecionado,
  carregandoRelatorio,
  carregarRelatorioFinalPaciente,
  relatorioFinalPaciente,
  exportarRelatorioCSV,
  exportarRelatorioPDF,
  relatorioTextoPorProtocolo,
  setRelatorioTextoPorProtocolo,
  salvarConclusaoProtocolo,
  onAtualizacao,
}) {
  const [secoesPorProtocolo, setSecoesPorProtocolo] = useState({});
  const [mostraModalValidacao, setMostraModalValidacao] = useState(false);
  const [dadosValidacao, setDadosValidacao] = useState({ protocolo: null, pendencia: null, exameId: null, exameLabel: null });
  const [validadoPor, setValidadoPor] = useState('');
  const [observacoes, setObservacoes] = useState('');
  const [carregandoValidacao, setCarregandoValidacao] = useState(false);
  const [examesDoProtocolo, setExamesDoProtocolo] = useState([]);
  const [carregandoExames, setCarregandoExames] = useState(false);
  const [exameEmEdicao, setExameEmEdicao] = useState(null);
  const [salvandoExame, setSalvandoExame] = useState(false);

  useEffect(() =>{
    if (!relatorioFinalPaciente?.protocolos) {
      setSecoesPorProtocolo({});
      return;
    }

    const mapa = {};
    relatorioFinalPaciente.protocolos.forEach((protocoloResumo) => {
      const protocoloId = protocoloResumo.protocoloId;
      const textoAtual = relatorioTextoPorProtocolo?.[protocoloId] ?? protocoloResumo.relatorioFinalEditavel ?? "";
      mapa[protocoloId] = parseSecoesRelatorio(textoAtual);
    });
    setSecoesPorProtocolo(mapa);
  }, [relatorioFinalPaciente, relatorioTextoPorProtocolo]);

  const carregarExamesDoProtocolo = async (protocoloId, ativoRef = { current: true }) => {
    if (!protocoloId) {
      setExamesDoProtocolo([]);
      setExameEmEdicao(null);
      return;
    }

    setCarregandoExames(true);
    try {
      const dados = await exameService.listarPorProtocolo(protocoloId);
      if (!ativoRef.current) {
        return;
      }

      setExamesDoProtocolo(Array.isArray(dados) ? dados : []);
    } catch (e) {
      if (ativoRef.current) {
        setErro("Não foi possível carregar os exames do protocolo.");
      }
    } finally {
      if (ativoRef.current) {
        setCarregandoExames(false);
      }
    }
  };

  useEffect(() => {
    const protocoloId = pacienteSelecionado?.protocolo?.id;
    // ✅ Usar useRef corretamente para evitar race condition
    const ativoRef = { current: true };

    carregarExamesDoProtocolo(protocoloId, ativoRef);

    return () => {
      ativoRef.current = false;
    };
  }, [pacienteSelecionado?.protocolo?.id]);

  const atualizarSecoesDoProtocolo = (protocoloId, atualizador) => {
    setSecoesPorProtocolo((prev) => {
      const base = Array.isArray(prev[protocoloId])
        ? prev[protocoloId]
        : parseSecoesRelatorio(relatorioTextoPorProtocolo?.[protocoloId] || "");
      const proximo = atualizador(base);
      const textoSerializado = serializarSecoesRelatorio(proximo);

      setRelatorioTextoPorProtocolo((estadoAnterior) => ({
        ...estadoAnterior,
        [protocoloId]: textoSerializado
      }));

      return {
        ...prev,
        [protocoloId]: proximo
      };
    });
  };

  const adicionarSecao = (protocoloId) => {
    atualizarSecoesDoProtocolo(protocoloId, (secoesAtuais) => ([
      ...secoesAtuais,
      {
        titulo: `Nova Secao ${secoesAtuais.length + 1}`,
        conteudo: ""
      }
    ]));
  };

  const removerSecao = (protocoloId, indiceRemocao) => {
    atualizarSecoesDoProtocolo(protocoloId, (secoesAtuais) => {
      const atualizado = secoesAtuais.filter((_, indice) => indice !== indiceRemocao);
      return atualizado.length > 0 ? atualizado : [{ titulo: "Conclusao", conteudo: "" }];
    });
  };

  const atualizarCampoSecao = (protocoloId, indiceSecao, campo, valor) => {
    atualizarSecoesDoProtocolo(protocoloId, (secoesAtuais) =>
      secoesAtuais.map((secao, indice) =>
        indice === indiceSecao
          ? {
              ...secao,
              [campo]: valor
            }
          : secao
      )
    );
  };

  const abrirModalValidacao = (protocolo, pendencia) => {
    setDadosValidacao({ protocolo, pendencia, exameId: null, exameLabel: pendencia });
    setValidadoPor('');
    setObservacoes('');
    setCarregandoValidacao(false);
    setMostraModalValidacao(true);
  };

  const abrirValidacaoExame = (protocolo, exame) => {
    // ✅ Validar se exame tem resultado antes de permitir validação
    if (!exame?.resultado) {
      alert("Este exame ainda não possui resultado. Preencha o resultado antes de validar.");
      return;
    }

    setDadosValidacao({
      protocolo,
      pendencia: exame?.descricao || exame?.tipoExame || "Exame",
      exameId: exame?.id || null,
      exameLabel: exame?.descricao || exame?.tipoExame || "Exame",
    });
    // Abrir o exame em edição para que o validador da Central possa revisar anexos e textos
    setExameEmEdicao({
      id: exame.id,
      protocoloId: protocolo?.id,
      categoria: exame.categoria,
      tipoExame: exame.tipoExame,
      descricao: exame.descricao || "",
      responsavel: exame.responsavel || "",
      observacoes: exame.observacoes || "",
      resultadoPositivo: exame.resultadoPositivo ?? false,
      resultado: exame.resultado || null,
    });
    setValidadoPor('');
    setObservacoes('');
    setCarregandoValidacao(false);
    setMostraModalValidacao(true);
  };

  const confirmarValidacao = async () => {
    if (!validadoPor.trim()) {
      alert('Por favor, informe quem está validando.');
      return;
    }

    setCarregandoValidacao(true);
    try {
      const { protocolo, pendencia, exameId } = dadosValidacao;
      const pendenciaLimpa = String(pendencia || "").replace(/\s*\(.*\)$/, '');

      if (exameId) {
        await protocoloService.validarExame(protocolo.id, exameId, true, validadoPor, observacoes);
      } else if (pendenciaLimpa.includes('Teste clínico 1')) {
        await protocoloService.validarTesteClinico1(protocolo.id, validadoPor, observacoes);
      } else if (pendenciaLimpa.includes('Teste clínico 2')) {
        await protocoloService.validarTesteClinico2(protocolo.id, validadoPor, observacoes);
      } else if (pendenciaLimpa.includes('Apneia')) {
        await protocoloService.validarApneia(protocolo.id, validadoPor, observacoes);
      } else if (pendenciaLimpa.includes('Exames complementares')) {
        await protocoloService.validarTestesComplementares(protocolo.id, validadoPor, observacoes);
      } else {
        alert('Tipo de pendência não suportado.');
        setCarregandoValidacao(false);
        return;
      }

      alert('Validação registrada com sucesso!');
      setMostraModalValidacao(false);
      setDadosValidacao({ protocolo: null, pendencia: null, exameId: null, exameLabel: null });
      await carregarExamesDoProtocolo(protocolo.id);
      if (typeof onAtualizacao === "function") {
        await onAtualizacao();
      }
    } catch (err) {
      console.error(err);
      alert('Erro ao registrar validação: ' + (err.response?.data?.message || err.message));
    } finally {
      setCarregandoValidacao(false);
    }
  };

  const abrirEdicaoExame = (exame) => {
    // ✅ Avisar se há estações de edição em progresso
    if (exameEmEdicao && exameEmEdicao.id !== exame.id) {
      if (!confirm("Há edições em progresso em outro exame. Deseja descartá-las e abrir este novo?")) {
        return;
      }
    }

    setExameEmEdicao({
      id: exame.id,
      protocoloId: pacienteSelecionado?.protocolo?.id,
      categoria: exame.categoria,
      tipoExame: exame.tipoExame,
      descricao: exame.descricao || "",
      responsavel: exame.responsavel || "",
      observacoes: exame.observacoes || "",
      resultadoPositivo: exame.resultadoPositivo ?? false,
      resultado: exame.resultado || null,
    });
  };

  const atualizarCampoEdicaoExame = (campo, valor) => {
    setExameEmEdicao((atual) => ({
      ...atual,
      [campo]: valor
    }));
  };

  const salvarEdicaoExame = async (e) => {
    e.preventDefault();

    if (!exameEmEdicao?.id || !exameEmEdicao?.protocoloId) {
      return;
    }

    if (!String(exameEmEdicao.descricao || "").trim()) {
      setErro("A descrição do exame é obrigatória.");
      return;
    }

    setSalvandoExame(true);
    setErro("");

    try {
      await exameService.atualizar(exameEmEdicao.id, {
        protocoloId: exameEmEdicao.protocoloId,
        categoria: exameEmEdicao.categoria,
        tipoExame: exameEmEdicao.tipoExame,
        descricao: exameEmEdicao.descricao.trim(),
        resultadoPositivo: Boolean(exameEmEdicao.resultadoPositivo ?? false),
        responsavel: exameEmEdicao.responsavel || "",
        observacoes: exameEmEdicao.observacoes || "",
      });

      setExameEmEdicao(null);
      await carregarExamesDoProtocolo(exameEmEdicao.protocoloId);

      if (typeof onAtualizacao === "function") {
        await onAtualizacao();
      }
    } catch (err) {
      console.error(err);
      setErro(err?.response?.data?.message || "Erro ao salvar edição do exame.");
    } finally {
      setSalvandoExame(false);
    }
  };

  const imprimirRelatorioProtocolo = (protocoloResumo, secoes) => {
    const janela = window.open("", "_blank");
    if (!janela) {
      return;
    }

    const secoesHtml = (Array.isArray(secoes) ? secoes : []).map((secao) => `
      <section style="margin-bottom: 14px;">
        <h3 style="margin: 0 0 6px 0; color: #1f2937; font-size: 15px;">${secao.titulo || "Secao"}</h3>
        <p style="margin: 0; white-space: pre-wrap; line-height: 1.5; color: #111827;">${secao.conteudo || "(sem conteudo)"}</p>
      </section>
    `).join("");

    janela.document.write(`
      <!doctype html>
      <html lang="pt-BR">
        <head>
          <meta charset="UTF-8" />
          <title>Relatorio Final - ${protocoloResumo.numeroProtocolo || protocoloResumo.protocoloId}</title>
        </head>
        <body style="font-family: Arial, sans-serif; padding: 18px; color: #0f172a;">
          <h1 style="margin: 0 0 12px 0; font-size: 20px;">Relatorio Final - Protocolo ME</h1>
          <p style="margin: 0 0 8px 0;"><strong>Protocolo:</strong> ${protocoloResumo.numeroProtocolo || protocoloResumo.protocoloId}</p>
          <p style="margin: 0 0 8px 0;"><strong>Status:</strong> ${protocoloResumo.statusProtocolo || "-"}</p>
          <p style="margin: 0 0 16px 0;"><strong>Exames:</strong> ${protocoloResumo.examesRealizados || 0}/${protocoloResumo.totalExames || 0}</p>
          ${secoesHtml || "<p>Nenhuma secao cadastrada.</p>"}
        </body>
      </html>
    `);

    janela.document.close();
    janela.focus();
    janela.print();
  };

  return (
    <>
      {/* Modal de Validação */}
      {mostraModalValidacao && (
        <div className="modal-overlay-validacao" onClick={() => setMostraModalValidacao(false)}>
          <div className="modal-validacao" onClick={(e) => e.stopPropagation()}>
            <div className="modal-validacao-header">
              <h3>Validar Exame/Teste</h3>
              <button
                className="modal-validacao-close"
                onClick={() => setMostraModalValidacao(false)}
                disabled={carregandoValidacao}
              >
                ✕
              </button>
            </div>
            <div className="modal-validacao-body">
              <div className="validacao-info">
                <p>
                  <strong>Pendência:</strong> {dadosValidacao.pendencia}
                </p>
              </div>
              <div className="form-group">
                <label htmlFor="validadoPor">Validado por *</label>
                <input
                  id="validadoPor"
                  type="text"
                  placeholder="Nome completo ou matrícula"
                  value={validadoPor}
                  onChange={(e) => setValidadoPor(e.target.value)}
                  disabled={carregandoValidacao}
                />
              </div>
              <div className="form-group">
                <label htmlFor="observacoes">Observações (opcional)</label>
                <textarea
                  id="observacoes"
                  placeholder="Adicione observações sobre a validação"
                  value={observacoes}
                  onChange={(e) => setObservacoes(e.target.value)}
                  disabled={carregandoValidacao}
                  rows="3"
                />
              </div>
            </div>
            <div className="modal-validacao-footer">
              <button
                className="btn-cancel"
                onClick={() => setMostraModalValidacao(false)}
                disabled={carregandoValidacao}
              >
                Cancelar
              </button>
              <button
                className="btn-save"
                onClick={confirmarValidacao}
                disabled={carregandoValidacao}
              >
                {carregandoValidacao ? '⏳ Validando...' : '✅ Confirmar Validação'}
              </button>
            </div>
          </div>
        </div>
      )}

      <div className="panel pacientes-painel">
        <header className="painel-header">
          <div>
            <h2>👥 Pacientes com Protocolo de ME Iniciado ({pacientes.length})</h2>
            <p className="note">
              {ultimaAtualizacao
                ? `🔄 Atualizado em: ${ultimaAtualizacao.toLocaleTimeString("pt-BR")}`
                : "Carregando..."}
            </p>
            <p className="note central-note-readonly">Status clínico em tempo real para apoio à decisão da Central.</p>
            {modoTelao && pacientesFiltrados.length > 0 && (
              <p className="note telao-page-indicator">
                Página {paginaAtualTelao + 1}/{totalPaginasTelao} • {pacientesExibidos.length} paciente(s) nesta tela
              </p>
            )}
          </div>
        </header>

        {carregando ? (
          <p className="loading-message">⏳ Atualizando painel em tempo real...</p>
        ) : pacientesFiltrados.length === 0 ? (
          <p className="no-data-message">✓ Nenhum paciente em protocolo de ME no momento.</p>
        ) : (
          <div className="pacientes-tabela-wrapper">
            <table className="tabela-pacientes">
              <thead>
                <tr>
                  <th className="col-nome">Paciente</th>
                  <th className="col-cpf">CPF do Paciente</th>
                  <th className="col-hospital">Hospital Notificante</th>
                  <th className="col-cidade">Cidade do Hospital</th>
                  <th className="col-data">Notificação</th>
                  <th className="col-exames">Exames Concluídos</th>
                  <th className="col-status-exames">Resumo dos Exames</th>
                  <th className="col-faltantes">Pendências de Exames</th>
                  <th className="col-status">Status do Protocolo</th>
                </tr>
              </thead>
              <tbody>
                {pacientesExibidos.map((paciente) => {
                  const protocolo = paciente.protocolosME?.[0];
                  const examesPendentes = obterExamesPendentes(protocolo);
                  const examesConcluidos = obterExamesConcluidos(protocolo);
                  const resumoStatusExames = obterResumoStatusExames(protocolo);
                  return (
                    <tr
                      key={paciente.id}
                      className={`row-status-${obterCorStatus(protocolo?.status)}`}
                      onClick={() => !modoTelao && abrirVisualizacaoSomenteLeitura(paciente, protocolo)}
                      style={{ cursor: protocolo && !modoTelao ? "pointer" : "default" }}
                    >
                      <td className="col-nome" data-label="Paciente">
                        <strong>{paciente.nome}</strong>
                      </td>
                      <td className="col-cpf" data-label="CPF do Paciente">{formatarCpf(paciente.cpf)}</td>
                      <td className="col-hospital" data-label="Hospital Notificante">{obterNomeHospital(paciente, protocolo)}</td>
                      <td className="col-cidade" data-label="Cidade do Hospital">
                        {obterCidadeHospitalNotificante(paciente, protocolo, cidadesHospitaisPorNome)}
                      </td>
                      <td className="col-data" data-label="Notificação">
                        {protocolo?.dataNotificacao
                          ? new Date(protocolo.dataNotificacao).toLocaleDateString("pt-BR")
                          : "N/A"}
                      </td>
                      <td className="col-exames" data-label="Exames Concluídos">
                        <strong>{examesConcluidos}/4</strong>
                      </td>
                      <td className="col-status-exames" data-label="Resumo dos Exames">
                        <div className="status-exames-resumo">
                          <span className="badge-exame badge-validado">✅ {resumoStatusExames.validados}</span>
                          <span className="badge-exame badge-aguardando">⏳ {resumoStatusExames.aguardandoValidacao}</span>
                          <span className="badge-exame badge-nao-realizado">❌ {resumoStatusExames.naoRealizados}</span>
                        </div>
                      </td>
                      <td className="col-faltantes" data-label="Pendências de Exames">
                        {examesPendentes.length === 0 ? (
                          <span className="pendencia-ok">Todos concluídos</span>
                        ) : (
                          <ul className="lista-faltantes">
                            {examesPendentes.map((item) => (
                              <li key={`${paciente.id}-${item}`} className="pendencia-item">
                                <span>{item}</span>
                                <button
                                  className="btn-small btn-validar"
                                  onClick={(e) => {
                                    e.stopPropagation();
                                    abrirModalValidacao(protocolo, item);
                                  }}
                                >
                                  Validar
                                </button>
                              </li>
                            ))}
                          </ul>
                        )}
                      </td>
                      <td className="col-status" data-label="Status do Protocolo">
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

      {!modoTelao && pacienteSelecionado && (
        <div className="modal-overlay" onClick={() => setPacienteSelecionado(null)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>Verificação de Exames (Somente Leitura)</h2>
              <button className="modal-close" onClick={() => setPacienteSelecionado(null)}>✕</button>
            </div>
            <div className="modal-body">
              <div className="readonly-alert">
                🔒 Perfil Central: usa esta tela para validar exames um a um e ajustar os dados do exame quando necessário.
              </div>

              <div className="readonly-grid">
                <div>
                  <strong>Paciente:</strong> {pacienteSelecionado.nome}
                </div>
                <div>
                  <strong>CPF:</strong> {formatarCpf(pacienteSelecionado.cpf)}
                </div>
                <div>
                  <strong>Hospital:</strong> {pacienteSelecionado.hospital}
                </div>
                <div>
                  <strong>Cidade (Hospital Notificante):</strong> {pacienteSelecionado.cidade}
                </div>
                <div>
                  <strong>Status:</strong>{" "}
                  <span className={`status-badge status-${obterCorStatus(pacienteSelecionado.protocolo?.status)}`}>
                    {pacienteSelecionado.protocolo?.status?.replace(/_/g, " ") || "SEM STATUS"}
                  </span>
                </div>
                <div>
                  <strong>Exames Concluídos:</strong> {obterExamesConcluidos(pacienteSelecionado.protocolo)}/4
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
                <button
                  className="secondary-button"
                  onClick={() => window.scrollTo({ top: document.body.scrollHeight, behavior: 'smooth' })}
                >
                  Ir para Relatório
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
                    <div><strong>CPF:</strong> {formatarCpf(relatorioFinalPaciente.cpf)}</div>
                    <div><strong>Status Paciente:</strong> {relatorioFinalPaciente.statusPaciente}</div>
                    <div><strong>Status Final Protocolo:</strong> {relatorioFinalPaciente.statusFinalProtocolo}</div>
                    <div><strong>Total Protocolos:</strong> {relatorioFinalPaciente.totalProtocolos}</div>
                    <div><strong>Entrevista:</strong> {formatarStatusEntrevista(relatorioFinalPaciente.statusEntrevistaFamiliar)}</div>
                  </div>
                  <div style={{ marginTop: '0.75rem' }}>
                    <h4>Anexos do Relatório Final</h4>
                    <GerenciadorAnexos tipoAnexo="ENTREVISTA" idExameOuProtocolo={pacienteSelecionado.id} titulo="Anexos do Relatório Final" />
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
                          <div style={{ marginTop: "0.5rem" }}>
                            <div className="relatorio-secoes-header">
                              <strong>Partes do relatório final</strong>
                              <div className="action-row relatorio-secoes-acoes">
                                <button
                                  className="modal-report-button"
                                  type="button"
                                  onClick={() => adicionarSecao(protocoloResumo.protocoloId)}
                                >
                                  + Adicionar parte
                                </button>
                                <button
                                  className="modal-report-button"
                                  type="button"
                                  onClick={() => imprimirRelatorioProtocolo(
                                    protocoloResumo,
                                    secoesPorProtocolo[protocoloResumo.protocoloId]
                                  )}
                                >
                                  Imprimir protocolo
                                </button>
                              </div>
                            </div>

                            {(secoesPorProtocolo[protocoloResumo.protocoloId] || []).map((secao, indiceSecao) => (
                              <div key={`secao-${protocoloResumo.protocoloId}-${indiceSecao}`} className="relatorio-secao-item">
                                <div className="relatorio-secao-meta">
                                  <input
                                    className="relatorio-secao-titulo"
                                    value={secao.titulo || ""}
                                    onChange={(e) => atualizarCampoSecao(
                                      protocoloResumo.protocoloId,
                                      indiceSecao,
                                      "titulo",
                                      e.target.value
                                    )}
                                    placeholder="Titulo da parte"
                                  />
                                  <button
                                    className="secondary-button relatorio-secao-remover"
                                    type="button"
                                    onClick={() => removerSecao(protocoloResumo.protocoloId, indiceSecao)}
                                  >
                                    Remover
                                  </button>
                                </div>
                                <textarea
                                  value={secao.conteudo || ""}
                                  onChange={(e) => atualizarCampoSecao(
                                    protocoloResumo.protocoloId,
                                    indiceSecao,
                                    "conteudo",
                                    e.target.value
                                  )}
                                  placeholder="Descreva esta parte do relatório"
                                  rows={4}
                                  style={{ width: "100%" }}
                                />
                              </div>
                            ))}
                            <button
                              className="modal-report-button"
                              onClick={() => salvarConclusaoProtocolo(protocoloResumo.protocoloId)}
                            >
                              Salvar relatório
                            </button>
                          </div>
                          {Array.isArray(protocoloResumo.anexos) && protocoloResumo.anexos.length > 0 && (
                            <ul className="lista-faltantes" style={{ marginTop: "0.5rem" }}>
                              {protocoloResumo.anexos.map((anexo) => (
                                <li key={`anexo-relatorio-${anexo.id}`}>
                                  {anexo.nomeArquivo} ({anexo.tipoAnexo || "ANEXO"})
                                </li>
                              ))}
                            </ul>
                          )}
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

              <div className="readonly-pendencias">
                <h3>Exames do Protocolo</h3>
                {carregandoExames ? (
                  <p className="note">Carregando exames do protocolo...</p>
                ) : examesDoProtocolo.length === 0 ? (
                  <p className="note">Nenhum exame registrado para este protocolo.</p>
                ) : (
                  <ul className="lista-faltantes exames-lista-central">
                    {examesDoProtocolo.map((exame) => (
                      <li key={`exame-central-${exame.id}`} className="exame-item-central">
                        <div className="exame-item-conteudo">
                          <strong>{exame.descricao || exame.tipoExame}</strong>
                          <span className="exame-meta">{exame.tipoExame}</span>
                          <span className={obterClasseBadgeStatusValidacao(exame.statusValidacao)}>
                            Validação: {formatarStatusValidacaoExame(exame.statusValidacao)}
                          </span>
                          <span className="exame-meta">Resultado: {formatarResultadoExame(exame)}</span>
                          {exame.responsavel && <span className="exame-meta">Responsável: {exame.responsavel}</span>}
                          {exame.dataRealizacao && (
                            <span className="exame-meta">Realizado em: {new Date(exame.dataRealizacao).toLocaleString("pt-BR")}</span>
                          )}
                          {exame.observacoes && (
                            <span className="exame-meta exame-observacao">Obs.: {exame.observacoes}</span>
                          )}
                        </div>
                        <div className="exame-item-acoes">
                          <button
                            className="btn-small btn-validar"
                            onClick={() => abrirValidacaoExame(pacienteSelecionado.protocolo, exame)}
                          >
                            Validar
                          </button>
                          <button
                            className="btn-small btn-editar"
                            onClick={() => abrirEdicaoExame(exame)}
                          >
                            Editar
                          </button>
                        </div>
                        {exameEmEdicao?.id === exame.id && (
                          <form className="exame-edicao-form" onSubmit={salvarEdicaoExame}>
                            <div className="form-group">
                              <label htmlFor={`descricao-${exame.id}`}>Descrição</label>
                              <input
                                id={`descricao-${exame.id}`}
                                type="text"
                                value={exameEmEdicao.descricao}
                                onChange={(e) => atualizarCampoEdicaoExame("descricao", e.target.value)}
                                disabled={salvandoExame}
                              />
                            </div>
                            <div className="form-group">
                              <label htmlFor={`responsavel-${exame.id}`}>Responsável</label>
                              <input
                                id={`responsavel-${exame.id}`}
                                type="text"
                                value={exameEmEdicao.responsavel}
                                onChange={(e) => atualizarCampoEdicaoExame("responsavel", e.target.value)}
                                disabled={salvandoExame}
                              />
                            </div>
                            <div className="form-group">
                              <label htmlFor={`observacoes-${exame.id}`}>Observações</label>
                              <textarea
                                id={`observacoes-${exame.id}`}
                                rows="3"
                                value={exameEmEdicao.observacoes}
                                onChange={(e) => atualizarCampoEdicaoExame("observacoes", e.target.value)}
                                disabled={salvandoExame}
                              />
                            </div>
                            <div className="form-group">
                              <h4>Anexos do Exame</h4>
                              <GerenciadorAnexos tipoAnexo="EXAME" idExameOuProtocolo={exame.id} titulo="Anexos do Exame" />
                            </div>
                            <div className="action-row exame-edicao-acoes">
                              <button type="button" className="secondary-button" onClick={() => setExameEmEdicao(null)} disabled={salvandoExame}>
                                Cancelar
                              </button>
                              <button type="submit" className="secondary-button" disabled={salvandoExame}>
                                {salvandoExame ? "Salvando..." : "Salvar edição"}
                              </button>
                            </div>
                          </form>
                        )}
                      </li>
                    ))}
                  </ul>
                )}
              </div>
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
    </>
  );
}

export default PainelPacientesCentral;
