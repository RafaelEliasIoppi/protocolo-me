import React, { useState, useEffect } from "react";
import apiClient from "../services/apiClient";
import ExameMEManager from "./ExameMEManager";
import "../styles/MedicoProtocoloME.css";

function MedicoProtocoloME() {
  const [pacientesProtocolo, setPacientesProtocolo] = useState([]);
  const [pacientesDisponiveis, setPacientesDisponiveis] = useState([]);
  const [carregando, setCarregando] = useState(false);
  const [erro, setErro] = useState("");
  const [sucesso, setSucesso] = useState("");
  const [protocoloSelecionado, setProtocoloSelecionado] = useState(null);
  const [mostraFormularioProtocolo, setMostraFormularioProtocolo] = useState(false);
  const [pacienteSelecionado, setPacienteSelecionado] = useState("");
  const [diagnostico, setDiagnostico] = useState("");
  const [mostraExames, setMostraExames] = useState(false);

  const statusAtivos = [
    "NOTIFICADO",
    "EM_PROCESSO",
    "MORTE_CEREBRAL_CONFIRMADA",
    "ENTREVISTA_FAMILIAR",
    "DOACAO_AUTORIZADA",
    "FAMILIA_RECUSOU"
  ];

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

  // Carregar pacientes em protocolo e disponíveis
  useEffect(() => {
    carregarPacientesProtocolo();
    carregarPacientesDisponiveis();
  }, []);

  const carregarPacientesProtocolo = async () => {
    try {
      const response = await apiClient.get("/api/protocolos-me");
      setPacientesProtocolo(mapearProtocolosParaPacientes(response.data));
      setErro("");
    } catch (e) {
      console.error("Erro ao carregar pacientes em protocolo:", e);
      setErro("Não foi possível carregar os protocolos de ME.");
    }
  };

  const atualizarPainelAposExame = async () => {
    await carregarPacientesProtocolo();
    if (!protocoloSelecionado?.id) {
      return;
    }

    try {
      const response = await apiClient.get(`/api/protocolos-me/${protocoloSelecionado.id}`);
      setProtocoloSelecionado(response.data);
    } catch (e) {
      console.error("Erro ao atualizar protocolo após exame:", e);
    }
  };

  const carregarPacientesDisponiveis = async () => {
    try {
      const response = await apiClient.get("/api/pacientes/status/INTERNADO");
      const lista = Array.isArray(response.data) ? response.data : [];
      const semProtocolo = lista.filter((p) => !Array.isArray(p.protocolosME) || p.protocolosME.length === 0);
      setPacientesDisponiveis(semProtocolo);
    } catch (e) {
      console.error("Erro ao carregar pacientes disponíveis:", e);
      setErro("Não foi possível carregar os pacientes internados para iniciar protocolo.");
    }
  };

  const iniciarProtocoloME = async (e) => {
    e.preventDefault();
    setErro("");
    setSucesso("");

    if (!pacienteSelecionado || !diagnostico.trim()) {
      setErro("Selecione um paciente e preencha o diagnóstico");
      return;
    }

    try {
      setCarregando(true);
      const response = await apiClient.post("/api/protocolos-me", {
        pacienteId: parseInt(pacienteSelecionado),
        diagnosticoBasico: diagnostico,
        status: "NOTIFICADO"
      });

      setSucesso("✅ Protocolo ME iniciado com sucesso! Status: NOTIFICADO");
      setMostraFormularioProtocolo(false);
      setPacienteSelecionado("");
      setDiagnostico("");
      
      // Recarregar listas
      await carregarPacientesProtocolo();
      await carregarPacientesDisponiveis();
      
      setTimeout(() => setSucesso(""), 3000);
    } catch (e) {
      setErro(e.response?.data?.mensagem || "Erro ao iniciar protocolo ME");
    } finally {
      setCarregando(false);
    }
  };

  const obterBadgeStatus = (status) => {
    const statusMap = {
      "NOTIFICADO": { cor: "notificado", label: "🔵 NOTIFICADO" },
      "EM_PROCESSO": { cor: "em-processo", label: "🟡 EM PROCESSO" },
      "MORTE_CEREBRAL_CONFIRMADA": { cor: "confirmado", label: "🟠 ME CONFIRMADA" },
      "ENTREVISTA_FAMILIAR": { cor: "entrevista", label: "🟢 ENTREVISTA" },
      "DOACAO_AUTORIZADA": { cor: "autorizado", label: "✅ DOAÇÃO AUTORIZADA" },
      "FAMILIA_RECUSOU": { cor: "recusado", label: "❌ DOAÇÃO RECUSADA" }
    };
    return statusMap[status] || { cor: "default", label: status };
  };

  const obterExamesRealizados = (protocolo) => {
    let exames = 0;
    if (protocolo.testeClinico1Realizado) exames++;
    if (protocolo.testeClinico2Realizado) exames++;
    if (protocolo.testesComplementaresRealizados) exames++;
    return exames;
  };

  return (
    <section className="medico-protocolo-me">
      <div className="brand-bar">
        <div>
          <h1>🏥 Meu Protocolo de Morte Encefálica (ME)</h1>
          <p>Gerencia pacientes em protocolo ME, adiciona exames e acompanha o status</p>
        </div>
        <button 
          className="btn-primary"
          onClick={() => setMostraFormularioProtocolo(!mostraFormularioProtocolo)}
        >
          {mostraFormularioProtocolo ? "❌ Fechar" : "➕ Iniciar Protocolo ME"}
        </button>
      </div>

      {erro && <div className="mensagem erro">📛 {erro}</div>}
      {sucesso && <div className="mensagem sucesso">{sucesso}</div>}

      {/* Formulário para iniciar novo protocolo */}
      {mostraFormularioProtocolo && (
        <div className="panel formulario-protocolo">
          <h2>Iniciar Novo Protocolo de ME</h2>
          <form onSubmit={iniciarProtocoloME}>
            <div className="form-row">
              <div className="form-group">
                <label>Paciente *</label>
                <select
                  value={pacienteSelecionado}
                  onChange={(e) => setPacienteSelecionado(e.target.value)}
                  required
                >
                  <option value="">-- Selecione um paciente --</option>
                  {pacientesDisponiveis.map((p) => (
                    <option key={p.id} value={p.id}>
                      {p.nome} ({p.cpf}) - {p.hospital?.nomeHospital}
                    </option>
                  ))}
                </select>
                {pacientesDisponiveis.length === 0 && (
                  <small style={{ color: "orange" }}>⚠️ Nenhum paciente disponível</small>
                )}
              </div>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label>Diagnóstico Inicial *</label>
                <textarea
                  value={diagnostico}
                  onChange={(e) => setDiagnostico(e.target.value)}
                  placeholder="Ex: Traumatismo craniano severo com suspeita de morte cerebral"
                  rows="3"
                  required
                />
              </div>
            </div>

            <div className="form-actions">
              <button type="submit" className="btn-save" disabled={carregando}>
                {carregando ? "⏳ Iniciando..." : "✅ Iniciar Protocolo"}
              </button>
              <button 
                type="button"
                className="btn-cancel"
                onClick={() => setMostraFormularioProtocolo(false)}
              >
                Cancelar
              </button>
            </div>

            <div className="form-info">
              <strong>ℹ️ Informação importante:</strong>
              <ul>
                <li>O protocolo será criado com status <strong>NOTIFICADO</strong></li>
                <li>Você terá acesso para adicionar exames clínicos e complementares</li>
                <li>O status será atualizado automaticamente conforme os exames são registrados</li>
                <li>Quando todos os exames estiverem completos, a central de transplantes será notificada</li>
              </ul>
            </div>
          </form>
        </div>
      )}

      {/* Lista de pacientes em protocolo */}
      <div className="panel pacientes-em-protocolo">
        <h2>Pacientes em Protocolo ME (Total: {pacientesProtocolo.length})</h2>

        {carregando && <p>⏳ Carregando...</p>}

        {pacientesProtocolo.length === 0 ? (
          <div className="vazio">
            <p>Nenhum paciente em protocolo ME no momento.</p>
            <button 
              className="btn-primary"
              onClick={() => setMostraFormularioProtocolo(true)}
            >
              Iniciar primeiro protocolo
            </button>
          </div>
        ) : (
          <div className="pacientes-grid">
            {pacientesProtocolo.map((paciente) => {
              const protocolo = paciente.protocolosME?.[0];
              const statusBadge = obterBadgeStatus(protocolo?.status);
              const examesRealizados = obterExamesRealizados(protocolo);

              return (
                <div key={paciente.id} className={`protocolo-card status-${statusBadge.cor}`}>
                  <div className="card-header">
                    <div>
                      <h3>{paciente.nome}</h3>
                      <p className="cpf">CPF: {paciente.cpf}</p>
                    </div>
                    <span className={`status-badge status-${statusBadge.cor}`}>
                      {statusBadge.label}
                    </span>
                  </div>

                  <div className="card-body">
                    <div className="info-row">
                      <label>Hospital:</label>
                      <span>{paciente.hospital?.nomeHospital || "N/A"}</span>
                    </div>
                    <div className="info-row">
                      <label>Leito:</label>
                      <span>{paciente.leito || "N/A"}</span>
                    </div>
                    <div className="info-row">
                      <label>Diagnóstico:</label>
                      <span>{protocolo?.diagnosticoBasico || "N/A"}</span>
                    </div>
                    <div className="info-row">
                      <label>Data de Notificação:</label>
                      <span>
                        {protocolo?.dataCriacao
                          ? new Date(protocolo.dataCriacao).toLocaleDateString("pt-BR")
                          : "N/A"}
                      </span>
                    </div>

                    {/* Exames realizados */}
                    <div className="exames-resumo">
                      <label>Exames Realizados:</label>
                      <div className="exames-status">
                        <div className={protocolo?.testeClinico1Realizado ? "exame completo" : "exame pendente"}>
                          {protocolo?.testeClinico1Realizado ? "✅" : "⏳"} Teste Clínico 1
                        </div>
                        <div className={protocolo?.testeClinico2Realizado ? "exame completo" : "exame pendente"}>
                          {protocolo?.testeClinico2Realizado ? "✅" : "⏳"} Teste Clínico 2
                        </div>
                        <div className={protocolo?.testesComplementaresRealizados ? "exame completo" : "exame pendente"}>
                          {protocolo?.testesComplementaresRealizados ? "✅" : "⏳"} Exames Complementares
                        </div>
                      </div>
                      <p className="exames-count">
                        <strong>{examesRealizados}/3</strong> exames completos
                      </p>
                    </div>

                    {/* Progresso */}
                    <div className="progress-bar">
                      <div className="progress" style={{ width: `${(examesRealizados / 3) * 100}%` }}></div>
                    </div>
                  </div>

                  <div className="card-actions">
                    <button
                      className="btn-secondary"
                      onClick={() => {
                        setProtocoloSelecionado(protocolo);
                        setMostraExames(true);
                      }}
                    >
                      📋 Acessar Protocolo
                    </button>
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>

      {/* Modal de exames */}
      {mostraExames && protocoloSelecionado && (
        <div className="modal-overlay" onClick={() => setMostraExames(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>Gerenciar Exames - Protocolo ME</h2>
              <button className="modal-close" onClick={() => setMostraExames(false)}>✕</button>
            </div>
            <div className="modal-body">
              <ExameMEManager
                protocoloId={protocoloSelecionado.id}
                onAtualizacao={atualizarPainelAposExame}
              />
            </div>
          </div>
        </div>
      )}
    </section>
  );
}

export default MedicoProtocoloME;
