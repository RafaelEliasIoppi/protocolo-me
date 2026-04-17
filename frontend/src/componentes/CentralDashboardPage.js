import React, { useEffect, useState } from "react";
import apiClient from "../services/apiClient";
import "../styles/CentralDashboardPage.css";

function CentralDashboardPage() {
  const [pacientes, setPacientes] = useState([]);
  const [carregando, setCarregando] = useState(false);
  const [erro, setErro] = useState("");
  const [ultimaAtualizacao, setUltimaAtualizacao] = useState(null);
  const [pacienteSelecionado, setPacienteSelecionado] = useState(null);

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
      const response = await apiClient.get("/api/protocolos-me");
      const dados = mapearProtocolosParaPacientes(response.data);
      setPacientes(dados);
      setUltimaAtualizacao(new Date());
      setErro("");
    } catch (e) {
      if (e.response?.status === 403) {
        setErro("Sem permissão para alterar dados. A Central possui acesso somente para verificação.");
      } else {
        setErro("Erro ao atualizar painel da central.");
      }
      setPacientes([]);
    } finally {
      setCarregando(false);
    }
  };

  useEffect(() => {
    carregarPacientesDoEstado();
    const intervalo = setInterval(carregarPacientesDoEstado, 5000);
    return () => clearInterval(intervalo);
  }, []);

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
                      <td className="col-nome">
                        <strong>{paciente.nome}</strong>
                      </td>
                      <td className="col-cpf">{paciente.cpf}</td>
                      <td className="col-hospital">{obterNomeHospital(paciente, protocolo)}</td>
                      <td className="col-cidade">{paciente.hospital?.cidade || "N/A"}</td>
                      <td className="col-data">
                        {protocolo?.dataCriacao
                          ? new Date(protocolo.dataCriacao).toLocaleDateString("pt-BR")
                          : "N/A"}
                      </td>
                      <td className="col-exames">
                        <strong>{examesConcluidos}/3</strong>
                      </td>
                      <td className="col-faltantes">
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
                      <td className="col-status">
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
