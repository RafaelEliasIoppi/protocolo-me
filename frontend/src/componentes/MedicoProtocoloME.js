import { useEffect, useRef, useState } from "react";
import centralTransplantesService from "../services/centralTransplantesService";
import pacienteService from "../services/pacienteService";
import protocoloService from "../services/protocoloService";
import "../styles/MedicoProtocoloME.css";
import { getApiErrorMessage } from "../utils/apiError";
import { formatarCpf } from "../utils/cpf";
import EntrevistaFamiliarManager from "./EntrevistaFamiliarManager";
import GerenciadorExamesME from "./GerenciadorExamesME";

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
  const [abaProtocoloAberta, setAbaProtocoloAberta] = useState("exames");
  const [semCentraisCadastradas, setSemCentraisCadastradas] = useState(false);
  const [alertaCentral, setAlertaCentral] = useState("");
  const [mostrarTodosPacientes, setMostrarTodosPacientes] = useState(false);
  const montadoRef = useRef(true);

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

  const mapearProtocolosParaPacientes = (protocolos) => {
    if (!Array.isArray(protocolos)) {
      return [];
    }

    const porPaciente = new Map();

    protocolos
      .filter((protocolo) => protocolo?.paciente?.id)
      .filter((protocolo) => statusAtivos.includes(protocolo?.status))
      .forEach((protocolo) => {
        const paciente = protocolo.paciente;
        const pacienteId = paciente.id;
        const atual = porPaciente.get(pacienteId);

        const dataAtual = atual?.protocolosME?.[0]?.dataNotificacao || atual?.protocolosME?.[0]?.dataCriacao;
        const dataNova = protocolo?.dataNotificacao || protocolo?.dataCriacao;

        const pacienteComHospital = {
          ...paciente,
          hospital: {
            id: paciente.hospitalId,
            nome: paciente.hospitalNome || paciente.hospital?.nome || paciente.hospital?.nomeHospital || "-"
          }
        };

        if (!atual || (dataNova && (!dataAtual || new Date(dataNova) > new Date(dataAtual)))) {
          porPaciente.set(pacienteId, {
            ...pacienteComHospital,
            protocolosME: [protocolo]
          });
        }
      });

    return Array.from(porPaciente.values());
  };

  const tratarErroAutenticacaoOuPermissao = (e, fallbackMensagem) => {
    const status = e?.response?.status;
    if (status === 401) {
      setErro("Sessão expirada. Faça login novamente para continuar.");
      return true;
    }
    if (status === 403) {
      setErro("Sem permissão para acessar este protocolo. Verifique o perfil logado.");
      return true;
    }
    if (fallbackMensagem) {
      setErro(fallbackMensagem);
    }
    return false;
  };

  useEffect(() => {
    return () => {
      montadoRef.current = false;
    };
  }, []);

  useEffect(() => {
    let ativo = true;

    const carregar = async () => {
      await Promise.all([
        carregarStatusCentrais(),
        carregarPacientesProtocolo(),
        carregarPacientesDisponiveis(),
      ]);

      if (!ativo || !montadoRef.current) {
        return;
      }
    };

    carregar();

    return () => {
      ativo = false;
    };
  }, []);

  useEffect(() => {
    if (pacientesProtocolo.length === 1 && !mostraExames) {
      const paciente = pacientesProtocolo[0];
      const protocolo = paciente.protocolosME?.[0];
      if (protocolo) {
        setProtocoloSelecionado(protocolo);
        setAbaProtocoloAberta("exames");
        setMostraExames(true);
      }
    }
  }, [pacientesProtocolo]);

  useEffect(() => {
    if (protocoloSelecionado && mostraExames) {
      const paciente = pacientesProtocolo.find(p => p.protocolosME?.[0]?.id === protocoloSelecionado.id);
      if (paciente?.protocolosME?.[0]) {
        setProtocoloSelecionado(paciente.protocolosME[0]);
      }
    }
  }, [pacientesProtocolo, mostraExames, protocoloSelecionado?.id]);

  const carregarStatusCentrais = async () => {
    try {
      const lista = await centralTransplantesService.listarDados();
      if (!montadoRef.current) return;
      const centrais = Array.isArray(lista) ? lista : [];
      const naoPossuiCentrais = centrais.length === 0;

      setSemCentraisCadastradas(naoPossuiCentrais);
      setAlertaCentral(
        naoPossuiCentrais
          ? "Nenhuma central de transplantes está cadastrada. Cadastre uma central antes de iniciar protocolo de ME."
          : "",
      );
    } catch (e) {
      if (!montadoRef.current) return;
      setSemCentraisCadastradas(false);
      setAlertaCentral("");
    }
  };

  const carregarPacientesProtocolo = async () => {
    try {
      const protocolos = await protocoloService.listar();
      if (!montadoRef.current) return [];
      const pacientesMapeados = mapearProtocolosParaPacientes(protocolos);
      setPacientesProtocolo(pacientesMapeados);
      setErro("");
      return pacientesMapeados;
    } catch (e) {
      if (!montadoRef.current) return [];
      console.error("Erro ao carregar pacientes em protocolo:", e);
      tratarErroAutenticacaoOuPermissao(e, "Não foi possível carregar os protocolos de ME.");
      return [];
    }
  };

  const atualizarPainelAposExame = async () => {
    try {
      if (!protocoloSelecionado?.id) return;

      const protocoloAtualizado = await protocoloService.obter(protocoloSelecionado.id);
      if (!montadoRef.current) return;

      setProtocoloSelecionado(protocoloAtualizado);

      setPacientesProtocolo((prev) => prev.map((p) => {
        const proto = p.protocolosME?.[0];
        if (proto?.id === protocoloAtualizado.id) {
          return { ...p, protocolosME: [protocoloAtualizado] };
        }
        return p;
      }));
    } catch (e) {
      if (!montadoRef.current) return;
      console.error('Erro ao atualizar protocolo selecionado:', e);
      await carregarPacientesProtocolo();
    }
  };

  // ✅ LOGS DE DIAGNÓSTICO ADICIONADOS
  const carregarPacientesDisponiveis = async () => {
    try {
      console.log("🔍 [1] Chamando listarPorStatus('INTERNADO')...");
      const lista = await pacienteService.listarPorStatus("INTERNADO");

      console.log("🔍 [2] Retorno bruto da API:", lista);
      console.log("🔍 [2] Tipo do retorno:", typeof lista, Array.isArray(lista) ? "(é array)" : "(NÃO é array)");

      if (!montadoRef.current) return;

      const pacientes = Array.isArray(lista) ? lista : [];
      console.log("🔍 [3] Total de pacientes internados:", pacientes.length);

      if (pacientes.length > 0) {
        console.log("🔍 [3] Primeiro paciente (exemplo):", pacientes[0]);
        console.log("🔍 [3] Status dos pacientes:", pacientes.map(p => ({ id: p.id, nome: p.nome, status: p.status })));
        console.log("🔍 [3] protocolosME de cada paciente:", pacientes.map(p => ({ id: p.id, nome: p.nome, protocolosME: p.protocolosME })));
      }

      const semProtocolo = pacientes.filter(
        (p) => !Array.isArray(p.protocolosME) || p.protocolosME.length === 0
      );
      console.log("🔍 [4] Após filtro (sem protocolo ativo):", semProtocolo.length, semProtocolo.map(p => p.nome));

      setPacientesDisponiveis(semProtocolo);
    } catch (e) {
      console.error("❌ [ERRO] carregarPacientesDisponiveis:", e);
      console.error("❌ [ERRO] Status HTTP:", e?.response?.status);
      console.error("❌ [ERRO] Mensagem:", e?.response?.data);
      if (!montadoRef.current) return;
      tratarErroAutenticacaoOuPermissao(
        e,
        "Não foi possível carregar os pacientes internados para iniciar protocolo."
      );
    }
  };

  const carregarTodosPacientes = async () => {
    try {
      const lista = await pacienteService.listar();
      if (!montadoRef.current) return;
      console.log("🔍 [FALLBACK] Todos os pacientes:", lista);
      setPacientesDisponiveis(Array.isArray(lista) ? lista : []);
      setMostrarTodosPacientes(true);
    } catch (e) {
      console.error("Erro ao carregar todos pacientes:", e);
      if (!montadoRef.current) return;
      setErro(getApiErrorMessage(e, "Erro ao carregar pacientes cadastrados"));
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

    if (semCentraisCadastradas) {
      setErro("Não é possível iniciar protocolo: nenhuma central de transplantes cadastrada.");
      return;
    }

    try {
      setCarregando(true);
      await protocoloService.criar({
        pacienteId: parseInt(pacienteSelecionado, 10),
        diagnosticoBasico: diagnostico,
        status: "NOTIFICADO"
      });

      setSucesso("✅ Protocolo ME iniciado com sucesso! Status: NOTIFICADO");
      setMostraFormularioProtocolo(false);
      setPacienteSelecionado("");
      setDiagnostico("");

      await carregarPacientesProtocolo();
      await carregarPacientesDisponiveis();

      if (!montadoRef.current) return;

      setTimeout(() => setSucesso(""), 3000);
    } catch (e) {
      if (!montadoRef.current) return;
      setErro(getApiErrorMessage(e, "Erro ao iniciar protocolo ME"));
    } finally {
      if (montadoRef.current) {
        setCarregando(false);
      }
    }
  };

  const obterBadgeStatus = (status) => {
    const statusMap = {
      "NOTIFICADO": { cor: "notificado", label: "🔵 NOTIFICADO" },
      "EM_PROCESSO": { cor: "em-processo", label: "🟡 EM PROCESSO" },
      "MORTE_CEREBRAL_CONFIRMADA": { cor: "confirmado", label: "🟠 ME CONFIRMADA" },
      "ENTREVISTA_FAMILIAR": { cor: "entrevista", label: "🟢 ENTREVISTA" },
      "DOACAO_AUTORIZADA": { cor: "autorizado", label: "✅ DOAÇÃO AUTORIZADA" },
      "FAMILIA_RECUSOU": { cor: "recusado", label: "❌ DOAÇÃO RECUSADA" },
      "FINALIZADO": { cor: "finalizado", label: "🏁 FINALIZADO" }
    };
    return statusMap[status] || { cor: "default", label: status };
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

  const formatarResultadoEntrevista = (paciente, protocolo) => {
    const status = paciente?.statusEntrevistaFamiliar;

    if (status === "AUTORIZADA" || protocolo?.autopsiaAutorizada === true) {
      return { label: "Positivo", cor: "positivo" };
    }

    if (status === "RECUSADA" || (protocolo?.status === "FINALIZADO" && protocolo?.autopsiaAutorizada === false)) {
      return { label: "Negativo", cor: "negativo" };
    }

    return { label: "Não definido", cor: "nao-definido" };
  };

  const obterExamesRealizados = (protocolo) => {
    if (!protocolo) return 0;
    let exames = 0;
    if (protocolo.testeClinico1Realizado) exames++;
    if (protocolo.testeClinico2Realizado) exames++;
    if (protocolo.testesComplementaresRealizados) exames++;
    const apneiaRealizada = protocolo.exames && protocolo.exames.some(e => e.tipoExame === 'APNEIA_TEST' && e.resultado != null);
    if (apneiaRealizada) exames++;
    return exames;
  };

  const examesObrigatoriosValidados = (protocolo) =>
    Boolean(protocolo?.testeClinico1Validado)
    && Boolean(protocolo?.testeClinico2Validado)
    && Boolean(protocolo?.testesComplementaresValidados)
    && Boolean(protocolo?.apneiaValidada);

  const entrevistaLiberada = (protocolo) => {
    const status = protocolo?.status;
    if (examesObrigatoriosValidados(protocolo)) {
      return true;
    }

    return [
      "ENTREVISTA_FAMILIAR",
      "DOACAO_AUTORIZADA",
      "FAMILIA_RECUSOU",
      "FINALIZADO",
    ].includes(status);
  };

  const obterProximoPasso = (protocolo, paciente) => {
    if (!protocolo) return "Inicie o protocolo e registre os exames obrigatórios.";

    if (protocolo.status === "NOTIFICADO" || protocolo.status === "EM_PROCESSO") {
      return "Próximo passo: concluir 2 testes clínicos e 1 exame complementar.";
    }

    if (protocolo.status === "MORTE_CEREBRAL_CONFIRMADA") {
      const entrevistaStatus = paciente?.statusEntrevistaFamiliar;
      if (!entrevistaStatus || entrevistaStatus === "NAO_INICIADA") {
        return "Próximo passo: abrir Entrevista Familiar e marcar início da entrevista.";
      }
      return "Próximo passo: concluir o resultado final da entrevista familiar.";
    }

    if (protocolo.status === "ENTREVISTA_FAMILIAR") {
      return "Próximo passo: registrar decisão da família (autorizada ou recusada).";
    }

    if (protocolo.status === "FINALIZADO" || protocolo.status === "DOACAO_AUTORIZADA" || protocolo.status === "FAMILIA_RECUSOU") {
      return "Entrevista concluída. Protocolo finalizado nesta etapa.";
    }

    return "Acompanhe o status e atualize os dados necessários no protocolo.";
  };

  const pacienteModal = pacientesProtocolo.find(
    (paciente) => paciente?.protocolosME?.[0]?.id === protocoloSelecionado?.id,
  );
  const statusModal = obterBadgeStatus(protocoloSelecionado?.status);
  const examesConcluidosModal = [
    protocoloSelecionado?.testeClinico1Validado,
    protocoloSelecionado?.testeClinico2Validado,
    protocoloSelecionado?.testesComplementaresValidados,
    protocoloSelecionado?.apneiaValidada,
  ].filter(Boolean).length;

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
          disabled={semCentraisCadastradas}
          title={semCentraisCadastradas ? "Cadastre uma central para iniciar protocolo" : ""}
        >
          {mostraFormularioProtocolo ? "❌ Fechar" : "➕ Iniciar Protocolo ME"}
        </button>
      </div>

      {erro && <div className="mensagem erro">📛 {erro}</div>}
      {alertaCentral && (
        <div className="mensagem erro">
          ⚠️ {alertaCentral} Acesse <a href="/cadastros/centrais">Cadastro de Centrais</a>.
        </div>
      )}
      {sucesso && <div className="mensagem sucesso">{sucesso}</div>}

      <div className="panel pacientes-disponiveis">
        <h2>Pacientes disponíveis para iniciar protocolo</h2>
        <p>
          Encontrados <strong>{pacientesDisponiveis.length}</strong> pacientes internados sem protocolo ativo.
        </p>

        {pacientesDisponiveis.length === 0 ? (
          <div className="vazio">
            <p>Nenhum paciente disponível no momento.</p>
            <button
              type="button"
              className="btn-secondary"
              onClick={carregarPacientesDisponiveis}
            >
              Recarregar pacientes internados
            </button>
          </div>
        ) : (
          <div className="pacientes-grid">
            {pacientesDisponiveis.map((paciente) => (
              <div key={paciente.id} className="protocolo-card status-default">
                <div className="card-header">
                  <div>
                    <h3>{paciente.nome}</h3>
                    <p className="cpf">CPF: {formatarCpf(paciente.cpf)}</p>
                  </div>
                  <span className="status-badge status-default">INTERNADO</span>
                </div>

                <div className="card-body">
                  <div className="info-row">
                    <label>Hospital:</label>
                    <span>{paciente.hospital?.nome || paciente.hospital?.nomeHospital || paciente.hospitalNome || "N/A"}</span>
                  </div>
                  <div className="info-row">
                    <label>Leito:</label>
                    <span>{paciente.leito || "N/A"}</span>
                  </div>
                </div>

                <div className="card-actions">
                  <button
                    type="button"
                    className="btn-primary"
                    onClick={() => {
                      setPacienteSelecionado(String(paciente.id));
                      setMostraFormularioProtocolo(true);
                      setErro("");
                    }}
                  >
                    Iniciar com este paciente
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

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
                      {p.nome} ({formatarCpf(p.cpf)}) - {p.hospital?.nome || p.hospitalNome || p.hospital?.nomeHospital || "-"}
                    </option>
                  ))}
                </select>
                {pacientesDisponiveis.length === 0 && (
                  <div>
                    <small style={{ color: "orange" }}>⚠️ Nenhum paciente disponível</small>
                    <div style={{ marginTop: 6 }}>
                      <button type="button" className="btn-secondary" onClick={carregarTodosPacientes}>
                        🔎 Mostrar pacientes cadastrados (fallback)
                      </button>
                      {mostrarTodosPacientes && <small style={{ marginLeft: 8, color: '#666' }}>Mostrando todos os pacientes para diagnóstico.</small>}
                    </div>
                  </div>
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
              <button type="submit" className="btn-save" disabled={carregando || semCentraisCadastradas}>
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
            {pacientesProtocolo.map((paciente, index) => {
              const protocolo = paciente.protocolosME?.[0];
              const statusBadge = obterBadgeStatus(protocolo?.status);
              const examesRealizados = obterExamesRealizados(protocolo);
              const statusEntrevista = formatarStatusEntrevista(paciente.statusEntrevistaFamiliar);
              const resultadoEntrevista = formatarResultadoEntrevista(paciente, protocolo);
              const entrevistaConcluida = paciente.statusEntrevistaFamiliar === "AUTORIZADA" || paciente.statusEntrevistaFamiliar === "RECUSADA";
              const podeAbrirEntrevista = entrevistaLiberada(protocolo);
              const statusFluxoEntrevista = podeAbrirEntrevista ? "liberada" : "aguardando";

              return (
                <div key={`${paciente.id}-${protocolo?.id || index}`} className={`protocolo-card status-${statusBadge.cor}`}>
                  <div className="card-header">
                    <div>
                      <h3>{paciente.nome}</h3>
                      <p className="cpf">CPF: {formatarCpf(paciente.cpf)}</p>
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
                        {protocolo?.dataNotificacao
                          ? new Date(protocolo.dataNotificacao).toLocaleDateString("pt-BR")
                          : "N/A"}
                      </span>
                    </div>

                    <div className="entrevista-resumo">
                      <div className="entrevista-resumo-topo">
                        <div>
                          <label>Entrevista Familiar</label>
                          <p className="entrevista-resumo-subtitulo">
                            Status técnico do protocolo e resultado humano
                          </p>
                        </div>
                        <span className={`status-badge status-${paciente.statusEntrevistaFamiliar ? paciente.statusEntrevistaFamiliar.toLowerCase() : "nao-iniciada"}`}>
                          {statusEntrevista}
                        </span>
                      </div>

                      <div className={`fluxo-entrevista-pill fluxo-entrevista-pill-${statusFluxoEntrevista}`}>
                        {podeAbrirEntrevista ? "Entrevista liberada" : "Aguardando exames obrigatórios"}
                      </div>

                      <div className="entrevista-resumo-resultado">
                        <span className="entrevista-resumo-rotulo">Resultado:</span>
                        <span className={`resultado-badge resultado-${resultadoEntrevista.cor}`}>
                          {resultadoEntrevista.label}
                        </span>
                      </div>

                      <div className="entrevista-resumo-footer">
                        {!entrevistaConcluida && (
                          <p className="entrevista-resumo-texto">
                            {podeAbrirEntrevista
                              ? "Entrevista liberada: exames obrigatórios concluídos e confirmação de ME pronta para abordagem familiar."
                              : "A entrevista será liberada após concluir 2 testes clínicos e 1 exame complementar."}
                          </p>
                        )}
                        <button
                          className="btn-entrevista-inline"
                          title={!podeAbrirEntrevista ? "Exames precisam ser VALIDADOS pela central: 2 testes clínicos + apneia + 1 exame complementar" : ""}
                          onClick={() => {
                            if (!podeAbrirEntrevista) {
                              setErro("Entrevista ainda não liberada. Aguarde a validação da central para: 2 testes clínicos + apneia + 1 exame complementar.");
                              return;
                            }
                            setProtocoloSelecionado(protocolo);
                            setAbaProtocoloAberta("entrevista");
                            setMostraExames(true);
                          }}
                        >
                          {entrevistaConcluida ? "👀 Ver entrevista" : "👨‍👩‍👧 Abrir entrevista"}
                        </button>
                      </div>
                    </div>

                    <div className="exames-resumo">
                      <div className="exames-resumo-topo">
                        <label>Exames</label>
                        <span className="exames-count">
                          <strong>{examesRealizados}/4</strong> completos
                        </span>
                      </div>

                      <div className="exames-status">
                        <div className={protocolo?.testeClinico1Realizado ? "exame completo" : "exame pendente"}>
                          {protocolo?.testeClinico1Realizado ? "✅" : "⏳"} Teste Clínico 1
                        </div>
                        <div className={protocolo?.testeClinico2Realizado ? "exame completo" : "exame pendente"}>
                          {protocolo?.testeClinico2Realizado ? "✅" : "⏳"} Teste Clínico 2
                        </div>
                        <div className={protocolo?.testesComplementaresRealizados ? "exame completo" : "exame pendente"}>
                          {protocolo?.testesComplementaresRealizados ? "✅" : "⏳"} Complementares
                        </div>
                        <div className={protocolo && protocolo.exames && protocolo.exames.some(e => e.tipoExame === 'APNEIA_TEST' && e.resultado != null) ? "exame completo" : "exame pendente"}>
                          {protocolo && protocolo.exames && protocolo.exames.some(e => e.tipoExame === 'APNEIA_TEST' && e.resultado != null) ? "✅" : "⏳"} Apneia
                        </div>
                      </div>

                      <div className="exames-resumo-footer">
                          <div className="progress-bar">
                          <div className="progress" style={{ width: `${(examesRealizados / 4) * 100}%` }}></div>
                        </div>
                        <p className="exames-ajuda">Para inserir exames, clique no botão <strong>Inserir Exames</strong> abaixo.</p>
                      </div>
                    </div>

                    <div className="proximo-passo-box">
                      <strong>Próximo passo:</strong> {obterProximoPasso(protocolo, paciente)}
                    </div>

                    {!podeAbrirEntrevista && (
                      <div className="entrevista-alerta-card" role="alert">
                        ⚠️ Entrevista bloqueada. Aguardando validação pela central de: 2 testes clínicos + apneia + 1 exame complementar.
                      </div>
                    )}
                  </div>

                  <div className="card-actions">
                    <button
                      className="btn-secondary"
                      onClick={() => {
                        setProtocoloSelecionado(protocolo);
                        setAbaProtocoloAberta("exames");
                        setMostraExames(true);
                      }}
                    >
                      🧪 Inserir Exames
                    </button>
                    <button
                      className="btn-secondary"
                      onClick={() => {
                        setProtocoloSelecionado(protocolo);
                        setAbaProtocoloAberta("exames");
                        setMostraExames(true);
                      }}
                    >
                      📋 Ver Protocolo
                    </button>
                    <button
                      className="btn-secondary"
                      title={!podeAbrirEntrevista ? "Conclua 2 testes clínicos e 1 exame complementar para liberar a entrevista" : ""}
                      onClick={() => {
                        if (!podeAbrirEntrevista) {
                          setErro("Entrevista ainda não liberada. Conclua 2 testes clínicos e 1 exame complementar.");
                          return;
                        }
                        setProtocoloSelecionado(protocolo);
                        setAbaProtocoloAberta("entrevista");
                        setMostraExames(true);
                      }}
                    >
                      {entrevistaConcluida ? "👀 Ver Entrevista" : "👨‍👩‍👧 Realizar Entrevista"}
                    </button>
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>

      {mostraExames && protocoloSelecionado && (
        <div className="modal-overlay" onClick={() => setMostraExames(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <div className="modal-header-main">
                <h2>
                  {abaProtocoloAberta === "entrevista"
                    ? "Entrevista Familiar"
                    : "Gerenciar Exames"}
                </h2>
                <p className="modal-header-subtitle">Protocolo de Morte Encefalica</p>
                <div className="modal-header-meta">
                    <span><strong>Paciente:</strong> {pacienteModal?.nome || "N/A"}</span>
                    <span className={`status-badge status-${statusModal.cor}`}>{statusModal.label}</span>
                  </div>
              </div>
              <button className="modal-close" onClick={() => setMostraExames(false)}>✕</button>
            </div>
            <div className="action-row modal-tabs">
              <button
                  className={`secondary-button modal-tab ${abaProtocoloAberta === "exames" ? "is-active" : ""}`}
                onClick={() => setAbaProtocoloAberta("exames")}
              >
                Exames
              </button>
              <button
                  className={`secondary-button modal-tab ${abaProtocoloAberta === "entrevista" ? "is-active" : ""}`}
                onClick={() => {
                  if (!entrevistaLiberada(protocoloSelecionado)) {
                    setErro("Entrevista ainda não liberada. Conclua 2 testes clínicos e 1 exame complementar.");
                    return;
                  }
                  setAbaProtocoloAberta("entrevista");
                }}
                title={!entrevistaLiberada(protocoloSelecionado) ? "Conclua 2 testes clínicos e 1 exame complementar para liberar a entrevista" : ""}
              >
                Entrevista
              </button>
            </div>
            <div className="modal-pill-row">
              <span
                  className={`fluxo-entrevista-pill ${entrevistaLiberada(protocoloSelecionado) ? "fluxo-entrevista-pill-liberada" : "fluxo-entrevista-pill-aguardando"}`}
              >
                {entrevistaLiberada(protocoloSelecionado)
                  ? "Entrevista liberada"
                  : "Aguardando exames obrigatórios"}
              </span>
            </div>
            {!entrevistaLiberada(protocoloSelecionado) && (
              <div className="entrevista-alerta-card entrevista-alerta-modal" role="alert">
                ⚠️ Entrevista bloqueada no momento. Conclua 2 testes clínicos e 1 exame complementar para liberar esta etapa.
              </div>
            )}

            {abaProtocoloAberta === "exames" && (
              <div className="exames-resumo-modal">
                <h4>Progresso dos Exames</h4>
                <div className="exames-resumo-grid">
                  <div className={`resumo-item ${protocoloSelecionado?.testeClinico1Realizado ? "is-complete" : "is-pending"}`}>
                    <div className="resumo-item-icon">
                      {protocoloSelecionado?.testeClinico1Realizado ? "✅" : "⏳"}
                    </div>
                    <strong>Teste Clinico 1</strong>
                  </div>
                    <div className={`resumo-item ${protocoloSelecionado?.testeClinico2Realizado ? "is-complete" : "is-pending"}`}>
                    <div className="resumo-item-icon">
                      {protocoloSelecionado?.testeClinico2Realizado ? "✅" : "⏳"}
                    </div>
                    <strong>Teste Clinico 2</strong>
                  </div>
                    <div className={`resumo-item ${protocoloSelecionado?.testesComplementaresRealizados ? "is-complete" : "is-pending"}`}>
                    <div className="resumo-item-icon">
                      {protocoloSelecionado?.testesComplementaresRealizados ? "✅" : "⏳"}
                    </div>
                    <strong>Complementares</strong>
                  </div>
                </div>
                <div className="exames-resumo-footer">
                  <p>
                    <strong>Progresso:</strong> {examesConcluidosModal}/4 concluidos
                  </p>
                </div>
              </div>
            )}

            <div className="modal-body">
              {abaProtocoloAberta === "entrevista" && (
                <div className="info-banner entrevista-banner">
                  <strong>Fluxo da entrevista:</strong> primeiro marque o protocolo para entrevista, depois registre se a família foi notificada e a decisão final.
                </div>
              )}
              {abaProtocoloAberta === "entrevista" ? (
                <EntrevistaFamiliarManager
                  protocoloMEId={protocoloSelecionado.id}
                  onAtualizacao={atualizarPainelAposExame}
                />
              ) : (
                <GerenciadorExamesME
                  protocoloId={protocoloSelecionado.id}
                  onAtualizacao={atualizarPainelAposExame}
                />
              )}
            </div>
          </div>
        </div>
      )}
    </section>
  );
}

export default MedicoProtocoloME;
