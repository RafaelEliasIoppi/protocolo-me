import { useEffect, useState } from "react";
import {
    STATUS_ATIVOS,
    centralDashboardService,
    construirMapaCidadesHospitais,
    mapearProtocolosParaPacientes,
    obterCidadeHospitalNotificante,
    obterExamesPendentes,
    obterNomeHospital
} from "../services/centralDashboardService";
import "../styles/CentralDashboardPage.css";
import { formatarCpf } from "../utils/cpf";
import PainelPacientesCentral from "./PainelPacientesCentral";

const CHAVE_CONFIG_ESTATISTICA_CENTRAL = "central_dashboard_estatisticas_campos";
const CHAVE_CONFIG_TELAO = "central_dashboard_telao_config";

const obterConfiguracaoTelaoPadrao = () => ({
  itensPorPagina: 20,
  intervaloRotacaoSegundos: 10,
  rotacaoAutomatica: true,
  somentePendencias: false
});

const carregarConfiguracaoTelao = () => {
  const padrao = obterConfiguracaoTelaoPadrao();

  try {
    const conteudo = localStorage.getItem(CHAVE_CONFIG_TELAO);
    if (!conteudo) {
      return padrao;
    }

    const config = JSON.parse(conteudo);
    if (!config || typeof config !== "object") {
      return padrao;
    }

    return {
      itensPorPagina: Number.isInteger(config.itensPorPagina) ? config.itensPorPagina : padrao.itensPorPagina,
      intervaloRotacaoSegundos: Number.isInteger(config.intervaloRotacaoSegundos)
        ? config.intervaloRotacaoSegundos
        : padrao.intervaloRotacaoSegundos,
      rotacaoAutomatica: typeof config.rotacaoAutomatica === "boolean" ? config.rotacaoAutomatica : padrao.rotacaoAutomatica,
      somentePendencias: typeof config.somentePendencias === "boolean" ? config.somentePendencias : padrao.somentePendencias,
    };
  } catch (_) {
    return padrao;
  }
};

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

function PainelCentralPage({ telaoMode = false }) {
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
  const [relatorioTextoPorProtocolo, setRelatorioTextoPorProtocolo] = useState({});
  const [cidadesHospitaisPorNome, setCidadesHospitaisPorNome] = useState({});
  const [mostrarOpcoesTelao, setMostrarOpcoesTelao] = useState(false);
  const [filtroHospitalTelao, setFiltroHospitalTelao] = useState("TODOS");
  const [filtroStatusTelao, setFiltroStatusTelao] = useState("TODOS");
  const [paginaAtualTelao, setPaginaAtualTelao] = useState(0);
  const [estaTelaCheia, setEstaTelaCheia] = useState(Boolean(document.fullscreenElement));
  const [erroTelaCheia, setErroTelaCheia] = useState("");
  const [configTelao, setConfigTelao] = useState(() => carregarConfiguracaoTelao());
  const modoTelao = telaoMode || new URLSearchParams(window.location.search).get("telao") === "1";
  const itensPorPaginaTelao = configTelao.itensPorPagina;
  const intervaloRotacaoTelaoMs = configTelao.intervaloRotacaoSegundos * 1000;

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

  const statusAtivos = STATUS_ATIVOS;

  const carregarPacientesDoEstado = async () => {
    try {
      setCarregando(true);
      const { protocolos, estatisticas: estatisticasApi, hospitais } = await centralDashboardService.carregarDadosPainel();

      const dados = mapearProtocolosParaPacientes(protocolos, statusAtivos);
      const mapaCidades = construirMapaCidadesHospitais(hospitais);

      setPacientes(dados);
      setCidadesHospitaisPorNome(mapaCidades);
      setEstatisticas(estatisticasApi || estatisticasIniciais);
      setUltimaAtualizacao(new Date());
      setErro("");
    } catch (e) {
      if (e.response?.status === 403) {
        setErro("Sem permissão para alterar dados. A Central possui acesso somente para verificação.");
      } else {
        setErro("Erro ao atualizar painel da central.");
      }
      setPacientes([]);
      setCidadesHospitaisPorNome({});
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
    const intervalo = setInterval(carregarPacientesDoEstado, modoTelao ? 3000 : 5000);
    return () => clearInterval(intervalo);
  }, [modoTelao]);

  useEffect(() => {
    localStorage.setItem(CHAVE_CONFIG_ESTATISTICA_CENTRAL, JSON.stringify(camposVisiveis));
  }, [camposVisiveis]);

  useEffect(() => {
    localStorage.setItem(CHAVE_CONFIG_TELAO, JSON.stringify(configTelao));
  }, [configTelao]);

  useEffect(() => {
    const atualizarEstadoTelaCheia = () => {
      setEstaTelaCheia(Boolean(document.fullscreenElement));
    };

    document.addEventListener("fullscreenchange", atualizarEstadoTelaCheia);
    return () => document.removeEventListener("fullscreenchange", atualizarEstadoTelaCheia);
  }, []);

  const entrarTelaCheia = async () => {
    try {
      setErroTelaCheia("");
      await document.documentElement.requestFullscreen();
    } catch (_) {
      setErroTelaCheia("Não foi possível entrar em tela cheia automaticamente neste navegador.");
    }
  };

  const sairTelaCheia = async () => {
    if (!document.fullscreenElement) {
      return;
    }

    try {
      await document.exitFullscreen();
    } catch (_) {
      setErroTelaCheia("Não foi possível sair do modo tela cheia.");
    }
  };

  const hospitaisTelao = [
    "TODOS",
    ...Array.from(
      new Set(
        pacientes
          .map((paciente) => obterNomeHospital(paciente, paciente.protocolosME?.[0]))
          .filter((valor) => valor && valor !== "N/A")
      )
    ).sort((a, b) => a.localeCompare(b, "pt-BR"))
  ];

  const statusTelao = ["TODOS", ...statusAtivos];

  const pacientesFiltrados = pacientes.filter((paciente) => {
    if (!modoTelao) {
      return true;
    }

    const protocolo = paciente.protocolosME?.[0];
    const hospital = obterNomeHospital(paciente, protocolo);
    const bateHospital = filtroHospitalTelao === "TODOS" || hospital === filtroHospitalTelao;
    const bateStatus = filtroStatusTelao === "TODOS" || protocolo?.status === filtroStatusTelao;

    if (configTelao.somentePendencias) {
      const protocolo = paciente.protocolosME?.[0];
      const possuiPendencias = obterExamesPendentes(protocolo).length > 0;
      return bateHospital && bateStatus && possuiPendencias;
    }

    return bateHospital && bateStatus;
  });

  const totalPaginasTelao = Math.max(1, Math.ceil(pacientesFiltrados.length / itensPorPaginaTelao));

  const pacientesExibidos = modoTelao
    ? pacientesFiltrados.slice(
        paginaAtualTelao * itensPorPaginaTelao,
        paginaAtualTelao * itensPorPaginaTelao + itensPorPaginaTelao,
      )
    : pacientesFiltrados;

  useEffect(() => {
    if (!modoTelao) {
      return;
    }

    setPaginaAtualTelao(0);
  }, [modoTelao, filtroHospitalTelao, filtroStatusTelao, pacientesFiltrados.length, itensPorPaginaTelao]);

  useEffect(() => {
    if (!modoTelao || !configTelao.rotacaoAutomatica || totalPaginasTelao <= 1) {
      return;
    }

    const timer = setInterval(() => {
      setPaginaAtualTelao((paginaAnterior) => (paginaAnterior + 1) % totalPaginasTelao);
    }, intervaloRotacaoTelaoMs);

    return () => clearInterval(timer);
  }, [modoTelao, totalPaginasTelao, intervaloRotacaoTelaoMs, configTelao.rotacaoAutomatica]);

  const atualizarConfigTelao = (chave, valor) => {
    setConfigTelao((anterior) => ({
      ...anterior,
      [chave]: valor
    }));
  };

  const resetarOpcoesTelao = () => {
    setConfigTelao(obterConfiguracaoTelaoPadrao());
    setFiltroHospitalTelao("TODOS");
    setFiltroStatusTelao("TODOS");
    setPaginaAtualTelao(0);
  };

  const irPaginaAnteriorTelao = () => {
    if (totalPaginasTelao <= 1) {
      return;
    }

    setPaginaAtualTelao((paginaAnterior) =>
      paginaAnterior === 0 ? totalPaginasTelao - 1 : paginaAnterior - 1,
    );
  };

  const irProximaPaginaTelao = () => {
    if (totalPaginasTelao <= 1) {
      return;
    }

    setPaginaAtualTelao((paginaAnterior) => (paginaAnterior + 1) % totalPaginasTelao);
  };

  const abrirVisualizacaoSomenteLeitura = (paciente, protocolo) => {
    if (!protocolo) return;
    setRelatorioFinalPaciente(null);
    setPacienteSelecionado({
      id: paciente.id,
      nome: paciente.nome,
      cpf: paciente.cpf,
      hospital: obterNomeHospital(paciente, protocolo),
      cidade: obterCidadeHospitalNotificante(paciente, protocolo, cidadesHospitaisPorNome),
      statusEntrevistaFamiliar: paciente.statusEntrevistaFamiliar,
      protocolo
    });
  };

  const carregarRelatorioFinalPaciente = async (pacienteId) => {
    if (!pacienteId) return;
    try {
      setCarregandoRelatorio(true);
      const relatorio = await centralDashboardService.obterRelatorioFinalPaciente(pacienteId);
      setRelatorioFinalPaciente(relatorio);
      const textos = {};
      (relatorio?.protocolos || []).forEach((p) => {
        textos[p.protocoloId] = p.relatorioFinalEditavel || "";
      });
      setRelatorioTextoPorProtocolo(textos);
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
      "Autopsia Autorizada",
      "Conclusao Editavel",
      "Total Anexos"
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
        p.autopsiaAutorizada ? "SIM" : "NAO",
        p.relatorioFinalEditavel || "",
        Array.isArray(p.anexos) ? p.anexos.length : 0
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
        <td>${(p.relatorioFinalEditavel || "").replace(/</g, "&lt;")}</td>
        <td>${Array.isArray(p.anexos) ? p.anexos.length : 0}</td>
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
          <div class="meta"><strong>CPF:</strong> ${formatarCpf(relatorioFinalPaciente.cpf)}</div>
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
                <th>Conclusao</th>
                <th>Anexos</th>
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

  const salvarConclusaoProtocolo = async (protocoloId) => {
    const usuario = JSON.parse(localStorage.getItem("usuario") || "{}");
    try {
      await centralDashboardService.salvarRelatorioFinalProtocolo(protocoloId, {
        textoRelatorio: relatorioTextoPorProtocolo[protocoloId] || "",
        atualizadoPor: usuario?.nome || usuario?.email || "central"
      });

      if (pacienteSelecionado?.id) {
        await carregarRelatorioFinalPaciente(pacienteSelecionado.id);
      }
    } catch (e) {
      setErro("Não foi possível salvar a conclusão editável do protocolo.");
    }
  };

  return (
    <section className={`central-dashboard ${modoTelao ? "telao-mode" : ""} ${modoTelao && estaTelaCheia ? "tv-fullscreen" : ""}`}>
      {modoTelao && estaTelaCheia && (
        <button className="exit-fullscreen-fab" onClick={sairTelaCheia}>
          🗗 Sair da Tela Cheia
        </button>
      )}

      <div className="brand-bar dashboard-header">
        <div>
          <h1>{modoTelao ? "📺 Telão Central de Monitoramento ME" : "🏥 Painel Central de Monitoramento ME"}</h1>
          <p>Visão por paciente: nome, hospital e exames que ainda faltam em cada protocolo ME</p>
        </div>
        <div className="action-row">
          <button className="secondary-button" onClick={carregarPacientesDoEstado}>
            🔄 Atualizar
          </button>
          {!modoTelao && (
            <button
              className="secondary-button"
              onClick={() => window.open("/dashboard-central/telao", "_blank", "noopener,noreferrer")}
            >
              📺 Abrir Telão
            </button>
          )}
          {modoTelao && (
            <button
              className="secondary-button"
              onClick={() => setMostrarOpcoesTelao((aberto) => !aberto)}
            >
              ⚙️ Opções do Telão
            </button>
          )}
          {modoTelao && (
            <button
              className="secondary-button"
              onClick={estaTelaCheia ? sairTelaCheia : entrarTelaCheia}
            >
              {estaTelaCheia ? "🗗 Sair da Tela Cheia" : "🗖 Entrar em Tela Cheia"}
            </button>
          )}
        </div>
      </div>

      {modoTelao && (
        <div className="telao-filtros-panel">
          <div className="telao-filtro-item">
            <label htmlFor="filtro-hospital-telao">Hospital</label>
            <select
              id="filtro-hospital-telao"
              value={filtroHospitalTelao}
              onChange={(e) => setFiltroHospitalTelao(e.target.value)}
            >
              {hospitaisTelao.map((hospital) => (
                <option key={`hospital-telao-${hospital}`} value={hospital}>
                  {hospital === "TODOS" ? "Todos os hospitais" : hospital}
                </option>
              ))}
            </select>
          </div>

          <div className="telao-filtro-item">
            <label htmlFor="filtro-status-telao">Status do protocolo</label>
            <select
              id="filtro-status-telao"
              value={filtroStatusTelao}
              onChange={(e) => setFiltroStatusTelao(e.target.value)}
            >
              {statusTelao.map((status) => (
                <option key={`status-telao-${status}`} value={status}>
                  {status === "TODOS" ? "Todos os status" : status.replace(/_/g, " ")}
                </option>
              ))}
            </select>
          </div>

          <div className="telao-filtro-meta">
            <span>Exibindo {pacientesFiltrados.length} paciente(s)</span>
          </div>
        </div>
      )}

      {modoTelao && mostrarOpcoesTelao && (
        <div className="telao-opcoes-panel">
          <div className="telao-opcao-item">
            <label htmlFor="itens-pagina-telao">Pacientes por tela</label>
            <select
              id="itens-pagina-telao"
              value={configTelao.itensPorPagina}
              onChange={(e) => atualizarConfigTelao("itensPorPagina", Number(e.target.value))}
            >
              {[10, 15, 20, 25, 30, 40].map((valor) => (
                <option key={`itens-pagina-${valor}`} value={valor}>{valor}</option>
              ))}
            </select>
          </div>

          <div className="telao-opcao-item">
            <label htmlFor="intervalo-rotacao-telao">Troca automática (segundos)</label>
            <select
              id="intervalo-rotacao-telao"
              value={configTelao.intervaloRotacaoSegundos}
              onChange={(e) => atualizarConfigTelao("intervaloRotacaoSegundos", Number(e.target.value))}
            >
              {[5, 8, 10, 15, 20, 30].map((valor) => (
                <option key={`intervalo-rotacao-${valor}`} value={valor}>{valor}s</option>
              ))}
            </select>
          </div>

          <label className="telao-opcao-checkbox">
            <input
              type="checkbox"
              checked={configTelao.rotacaoAutomatica}
              onChange={(e) => atualizarConfigTelao("rotacaoAutomatica", e.target.checked)}
            />
            <span>Rotação automática</span>
          </label>

          <label className="telao-opcao-checkbox">
            <input
              type="checkbox"
              checked={configTelao.somentePendencias}
              onChange={(e) => atualizarConfigTelao("somentePendencias", e.target.checked)}
            />
            <span>Mostrar só pacientes com pendências</span>
          </label>

          <div className="telao-paginacao-manual">
            <button
              className="secondary-button"
              onClick={irPaginaAnteriorTelao}
              disabled={totalPaginasTelao <= 1}
            >
              ◀ Página anterior
            </button>
            <button
              className="secondary-button"
              onClick={irProximaPaginaTelao}
              disabled={totalPaginasTelao <= 1}
            >
              Próxima página ▶
            </button>
          </div>

          <button className="secondary-button telao-reset-button" onClick={resetarOpcoesTelao}>
            Restaurar padrões
          </button>
        </div>
      )}

      {modoTelao && erroTelaCheia && <div className="mensagem erro">{erroTelaCheia}</div>}

      {erro && <div className="mensagem erro">{erro}</div>}

      {!modoTelao && (
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
      )}

      <PainelPacientesCentral
        modoTelao={modoTelao}
        pacientes={pacientes}
        pacientesFiltrados={pacientesFiltrados}
        pacientesExibidos={pacientesExibidos}
        paginaAtualTelao={paginaAtualTelao}
        totalPaginasTelao={totalPaginasTelao}
        ultimaAtualizacao={ultimaAtualizacao}
        cidadesHospitaisPorNome={cidadesHospitaisPorNome}
        carregando={carregando}
        abrirVisualizacaoSomenteLeitura={abrirVisualizacaoSomenteLeitura}
        pacienteSelecionado={pacienteSelecionado}
        setPacienteSelecionado={setPacienteSelecionado}
        carregandoRelatorio={carregandoRelatorio}
        carregarRelatorioFinalPaciente={carregarRelatorioFinalPaciente}
        relatorioFinalPaciente={relatorioFinalPaciente}
        exportarRelatorioCSV={exportarRelatorioCSV}
        exportarRelatorioPDF={exportarRelatorioPDF}
        relatorioTextoPorProtocolo={relatorioTextoPorProtocolo}
        setRelatorioTextoPorProtocolo={setRelatorioTextoPorProtocolo}
        salvarConclusaoProtocolo={salvarConclusaoProtocolo}
      />
    </section>
  );
}

export default PainelCentralPage;
