import { useEffect, useState } from 'react';
import clienteHttpService from '../services/clienteHttpService';
import '../styles/EstatisticasPage.css';
import { formatarCpf } from '../utils/cpf';

const CAMPOS_ESTATISTICA_PROTOCOLO = [
  { key: 'ofNac', label: 'Of Nac' },
  { key: 'rgctDoador', label: 'RGCT-Doador' },
  { key: 'nomeDoador', label: 'Nome - DOADOR' },
  { key: 'hospitalNotif', label: 'HospitalNotif' },
  { key: 'dataOf', label: 'DATA - OF' },
  { key: 'regPdot', label: 'REG-PDOT' },
  { key: 'regOf', label: 'REG-OF' },
  { key: 'mes', label: 'Mes' },
  { key: 'municipio', label: 'Municipio' },
  { key: 'idDoad', label: 'Id-Doad' },
  { key: 'faixaEtariaDoad', label: 'F-Etaria-Doad' },
  { key: 'sexoDoad', label: 'Sexo-Doad' },
  { key: 'aboDoad', label: 'ABO-Doad' },
  { key: 'resCausaMorte', label: 'Res-C-Morte' },
  { key: 'dm', label: 'DM' },
  { key: 'has', label: 'HAS' },
  { key: 'etilismo', label: 'ETILISMO' },
  { key: 'tabagismo', label: 'TABAGISMO' },
  { key: 'crInicial', label: 'Cr Inicial' },
  { key: 'crFinal', label: 'Cr Final' },
  { key: 'rimD', label: 'Rim-D' },
  { key: 'rimE', label: 'Rim-E' },
  { key: 'coracao', label: 'Coracao' },
  { key: 'pulmD', label: 'Pulm-D' },
  { key: 'pulmE', label: 'Pulm-E' },
  { key: 'figado', label: 'Figado' },
  { key: 'corneas', label: 'Corneas' },
  { key: 'pele', label: 'Pele' },
  { key: 'ossoMusculo', label: 'Osso-Musculo' },
  { key: 'destRimD', label: 'Dest-RimD' },
  { key: 'destRimE', label: 'Dest-RimE' },
  { key: 'destCoracao', label: 'Dest-Coracao' },
  { key: 'destPulmD', label: 'Dest-PulmD' },
  { key: 'destPulmE', label: 'Dest-PulmE' },
  { key: 'destFigado', label: 'Dest-Figado' },
  { key: 'txRinsBloco', label: 'Tx- RinsBloco' },
  { key: 'txPulmBilat', label: 'Tx-Pulm Bilat' },
  { key: 'txRimFig', label: 'Tx-Rim Fig' },
  { key: 'txPulmDRim', label: 'Tx-PulmD_ Rim' },
  { key: 'txPulmERim', label: 'Tx-PulmE_Rim' },
  { key: 'txCorRim', label: 'Tx-Cor Rim' },
  { key: 'txCorPulm', label: 'Tx-Cor Pulm' },
  { key: 'descarteRimD', label: 'Descarte-RimD' },
  { key: 'descarteRimE', label: 'Descarte-RimE' },
  { key: 'descarteCoracao', label: 'Descarte-Coracao' },
  { key: 'descartePulmaoD', label: 'Descarte-PulmaoD' },
  { key: 'descartePulmaoE', label: 'Descarte-PulmaoE' },
  { key: 'descarteFigado', label: 'Descarte-Figado' },
  { key: 'motivoDescarteEsclarecer', label: 'MOTIVO DESCARTE Esclarecer' },
  { key: 'hospEquipeRecRd', label: 'Hosp-Equipe-RecRD' },
  { key: 'rgctRd', label: 'RGCT-RD' },
  { key: 'receptorRd', label: 'ReceptorRD' },
  { key: 'idadeRecRd', label: 'Idade-RecRD' },
  { key: 'sexoRecRd', label: 'Sexo-RecRD' },
  { key: 'mesTxRd', label: 'Mes-TxRD' },
  { key: 'hospEquipeRecRe', label: 'Hosp-EquipeRecRE' },
  { key: 'rgctRe', label: 'RGCT-RE' },
  { key: 'receptorRe', label: 'ReceptorRE' },
  { key: 'idadeRecRe', label: 'Idade-RecRE' },
  { key: 'sexoRecRe', label: 'Sexo-RecRE' },
  { key: 'mesTxRe', label: 'Mes-TxRE' },
  { key: 'hospEquipeRecFig', label: 'Hosp-EquipeRecFig' },
  { key: 'rgctFig', label: 'RGCT-Fig' },
  { key: 'receptorFig', label: 'ReceptorFig' },
  { key: 'idadeRecFig', label: 'Idade-RecFig' },
  { key: 'sexoRecFig', label: 'Sexo-RecFig' },
  { key: 'mesTxFig', label: 'Mes-TxFig' },
  { key: 'hospEquipeRecPulmD', label: 'Hosp-EquipeRecPulmD' },
  { key: 'rgctPulmD', label: 'RGCT-PulmD' },
  { key: 'receptorPulmD', label: 'ReceptorPulmD' },
  { key: 'idadeRecPulmD', label: 'Idade-RecPulmD' },
  { key: 'sexoRecPulmD', label: 'Sexo-RecPulmD' },
  { key: 'mesTxPulmD', label: 'Mes-TxPulmD' },
  { key: 'hospEquipeRecPulmE', label: 'Hosp-EquipeRecPulmE' },
  { key: 'rgctPulmE', label: 'RGCT-PulmE' },
  { key: 'receptorPulmE', label: 'ReceptorPulmE' },
  { key: 'idadeRecPulmE', label: 'Idade-RecPulmE' },
  { key: 'sexoRecPulmE', label: 'Sexo-RecPulmE' },
  { key: 'mesTxPulmE', label: 'Mes-TxPulmE' },
  { key: 'hospEquipeRecCor', label: 'Hosp-EquipeRecCor' },
  { key: 'rgctCor', label: 'RGCT-Cor' },
  { key: 'receptorCor', label: 'ReceptorCor' },
  { key: 'idadeRecCor', label: 'Idade-RecCor' },
  { key: 'sexoRecCor', label: 'Sexo-RecCor' },
  { key: 'mesTxCor', label: 'Mes-TxCor' },
  { key: 'doadorOfertaNacional', label: 'DoadorOfertaNacional' },
  { key: 'classif', label: 'Classif' },
  { key: 'algumOrgaoImplantadoNoRs', label: 'Algum Orgao implantado no RS' },
  { key: 'recusaRim', label: 'RECUSA RIM' },
  { key: 'recusaFigado', label: 'RECUSA FIGADO' },
  { key: 'recusaCoracao', label: 'RECUSA CORACAO' },
  { key: 'recusaPulmao', label: 'RECUSA PULMAO' },
  { key: 'observacoes', label: 'observacoes' }
];

const CAMPOS_SIM_NAO = new Set([
  'dm',
  'has',
  'etilismo',
  'tabagismo',
  'rimD',
  'rimE',
  'coracao',
  'pulmD',
  'pulmE',
  'figado',
  'corneas',
  'pele',
  'ossoMusculo',
  'txRinsBloco',
  'txPulmBilat',
  'txRimFig',
  'txPulmDRim',
  'txPulmERim',
  'txCorRim',
  'txCorPulm',
  'descarteRimD',
  'descarteRimE',
  'descarteCoracao',
  'descartePulmaoD',
  'descartePulmaoE',
  'descarteFigado',
  'doadorOfertaNacional',
  'algumOrgaoImplantadoNoRs',
  'recusaRim',
  'recusaFigado',
  'recusaCoracao',
  'recusaPulmao'
]);

const normalizarSimNao = (valor) => {
  const texto = String(valor ?? '').trim().toUpperCase();
  if (['SIM', 'S', 'YES', 'Y', 'TRUE', '1'].includes(texto)) {
    return 'SIM';
  }
  return 'NAO';
};

const aplicarPadraoCamposSimNao = (campos) => {
  const atualizados = { ...(campos || {}) };
  CAMPOS_SIM_NAO.forEach((chave) => {
    atualizados[chave] = normalizarSimNao(atualizados[chave]);
  });
  return atualizados;
};

const EstatisticasPage = () => {
  const [estatisticasGerais, setEstatisticasGerais] = useState(null);
  const [estatisticasPorPaciente, setEstatisticasPorPaciente] = useState([]);
  const [anosDisponiveis, setAnosDisponiveis] = useState([]);
  const [anoSelecionado, setAnoSelecionado] = useState(null);
  const [carregando, setCarregando] = useState(false);
  const [erro, setErro] = useState('');
  const [abas, setAbas] = useState('geral');
  const [filtroNomePaciente, setFiltroNomePaciente] = useState('');
  const [filtroReceptor, setFiltroReceptor] = useState('');
  const [periodicidade, setPeriodicidade] = useState('ANUAL');
  const [mesSelecionado, setMesSelecionado] = useState('');
  const [estatisticasProtocolo, setEstatisticasProtocolo] = useState([]);
  const [protocolosSemEstatistica, setProtocolosSemEstatistica] = useState([]);
  const [protocoloSelecionado, setProtocoloSelecionado] = useState(null);
  const [camposForm, setCamposForm] = useState({});
  const [salvando, setSalvando] = useState(false);

  useEffect(() => {
    carregarAnosDisponiveis();
  }, []);

  const carregarAnosDisponiveis = async () => {
    try {
      const response = await clienteHttpService.get('/api/estatisticas-transplantes/anos-disponiveis');
      setAnosDisponiveis(response.data);
      if (response.data.length > 0) {
        const anoMaisRecente = response.data[0];
        setAnoSelecionado(anoMaisRecente);
        carregarEstatisticasGerais(anoMaisRecente);
      } else {
        carregarEstatisticasGerais(null);
      }
    } catch (err) {
      console.error('Erro ao carregar anos:', err);
      carregarEstatisticasGerais(null);
    }
  };

  const carregarEstatisticasGerais = async (ano = anoSelecionado) => {
    setCarregando(true);
    setErro('');
    try {
      const params = ano ? { ano } : {};
      const response = await clienteHttpService.get('/api/estatisticas-transplantes/gerais', { params });
      setEstatisticasGerais(response.data);
    } catch (err) {
      setErro('Erro ao carregar estatísticas gerais');
      console.error(err);
    } finally {
      setCarregando(false);
    }
  };

  const carregarEstatisticasPorPaciente = async (ano = anoSelecionado) => {
    setCarregando(true);
    setErro('');
    try {
      const params = ano ? { ano } : {};
      const response = await clienteHttpService.get('/api/estatisticas-transplantes/por-paciente', { params });
      setEstatisticasPorPaciente(response.data);
    } catch (err) {
      setErro('Erro ao carregar estatísticas por paciente');
      console.error(err);
    } finally {
      setCarregando(false);
    }
  };

  const carregarEstatisticasProtocolo = async (
    ano = anoSelecionado,
    periodicidadeAtual = periodicidade,
    mesAtual = mesSelecionado,
  ) => {
    setCarregando(true);
    setErro('');
    try {
      const params = {};
      if (ano) params.ano = ano;
      if (periodicidadeAtual) params.periodicidade = periodicidadeAtual;
      if (periodicidadeAtual === 'MENSAL' && mesAtual) params.mes = parseInt(mesAtual, 10);
      const response = await clienteHttpService.get('/api/estatisticas-transplantes/protocolo-me', { params });
      setEstatisticasProtocolo(Array.isArray(response.data) ? response.data : []);
    } catch (err) {
      setErro('Erro ao carregar estatisticas por protocolo');
      console.error(err);
    } finally {
      setCarregando(false);
    }
  };

  const carregarAuditoriaProtocolos = async (ano = anoSelecionado) => {
    try {
      const params = ano ? { ano } : {};
      const response = await clienteHttpService.get('/api/estatisticas-transplantes/protocolo-me/auditoria', { params });
      setProtocolosSemEstatistica(Array.isArray(response.data) ? response.data : []);
    } catch (err) {
      console.error('Erro ao carregar auditoria de protocolos', err);
    }
  };

  const selecionarAno = (ano) => {
    setAnoSelecionado(ano);
    if (abas === 'geral') {
      carregarEstatisticasGerais(ano);
    } else if (abas === 'pacientes') {
      carregarEstatisticasPorPaciente(ano);
    } else {
      carregarEstatisticasProtocolo(ano);
    }
  };

  const selecionarAba = (aba) => {
    setAbas(aba);
    if (aba === 'geral') {
      carregarEstatisticasGerais(anoSelecionado);
    } else if (aba === 'pacientes') {
      carregarEstatisticasPorPaciente(anoSelecionado);
    } else {
      carregarEstatisticasProtocolo(anoSelecionado);
      carregarAuditoriaProtocolos(anoSelecionado);
    }
  };

  const selecionarProtocolo = async (item) => {
    try {
      const response = await clienteHttpService.get(`/api/estatisticas-transplantes/protocolo-me/${item.protocoloMEId}`);
      const data = response.data || {};
      setProtocoloSelecionado(data);
      setCamposForm(aplicarPadraoCamposSimNao(data.campos));
    } catch (err) {
      setErro('Erro ao abrir estatistica do protocolo');
      console.error(err);
    }
  };

  const salvarEstatistica = async () => {
    if (!protocoloSelecionado?.protocoloMEId) return;
    setSalvando(true);
    setErro('');
    try {
      const usuario = JSON.parse(localStorage.getItem('usuario') || '{}');
      const camposNormalizados = aplicarPadraoCamposSimNao(camposForm);
      await clienteHttpService.put(`/api/estatisticas-transplantes/protocolo-me/${protocoloSelecionado.protocoloMEId}`, {
        protocoloMEId: protocoloSelecionado.protocoloMEId,
        anoCompetencia: protocoloSelecionado.anoCompetencia || anoSelecionado,
        mesCompetencia: periodicidade === 'MENSAL' ? (mesSelecionado ? parseInt(mesSelecionado, 10) : null) : null,
        periodicidade,
        campos: camposNormalizados,
        atualizadoPor: usuario?.nome || usuario?.email || 'central'
      });
      setCamposForm(camposNormalizados);
      await carregarEstatisticasProtocolo();
      await carregarAuditoriaProtocolos();
    } catch (err) {
      setErro('Erro ao salvar estatistica do protocolo');
      console.error(err);
    } finally {
      setSalvando(false);
    }
  };

  const pacientesFiltrados = estatisticasPorPaciente.filter((p) => {
    const nomePaciente = (p.nomePaciente || '').toLowerCase();
    const filtroPacienteNormalizado = filtroNomePaciente.toLowerCase();
    const filtroReceptorNormalizado = filtroReceptor.toLowerCase();

    const correspondePaciente = !filtroPacienteNormalizado
      || nomePaciente.includes(filtroPacienteNormalizado);

    const listaImplantados = Array.isArray(p.orgaosImplantados) ? p.orgaosImplantados : [];
    const correspondeReceptor = !filtroReceptorNormalizado
      || listaImplantados.some((orgao) => {
        const nomeReceptor = (orgao.nomeReceptor || orgao.pacienteReceptor || '').toLowerCase();
        const cpfReceptor = String(orgao.cpfReceptor || '').replace(/\D/g, '');
        const filtroCpf = filtroReceptorNormalizado.replace(/\D/g, '');

        if (filtroCpf && cpfReceptor.includes(filtroCpf)) {
          return true;
        }

        return nomeReceptor.includes(filtroReceptorNormalizado);
      });

    return correspondePaciente && correspondeReceptor;
  });

  const totalOrgaosDisponiveis = estatisticasGerais?.totalOrgaosDisponiveis || 0;
  const orgaosImplantados = estatisticasGerais?.orgaosImplantados || 0;
  const orgaosDescartados = estatisticasGerais?.orgaosDescartados || 0;
  const orgaosAguardando = Math.max(totalOrgaosDisponiveis - orgaosImplantados - orgaosDescartados, 0);
  const percentualPizzaImplantados = totalOrgaosDisponiveis > 0
    ? (orgaosImplantados / totalOrgaosDisponiveis)
    : 0;
  // pizza: calcular comprimento do traço (circunferência) dinamicamente
  const pizzaRadius = 90;
  const pizzaCircumference = 2 * Math.PI * pizzaRadius; // ~565 for r=90
  const pizzaDash = percentualPizzaImplantados * pizzaCircumference;
  const totalFinalizados = orgaosImplantados + orgaosDescartados;
  const percentualImplantados = totalFinalizados > 0
    ? (orgaosImplantados / totalFinalizados) * 100
    : 0;
  const percentualDescartados = totalFinalizados > 0
    ? (orgaosDescartados / totalFinalizados) * 100
    : 0;
  const taxaImplantacaoFormatada = Number(estatisticasGerais?.taxaImplantacao || 0).toFixed(1);

  return (
    <div className="estatisticas-page">
      <div className="page-header">
        <h1>📊 Estatísticas de Transplantes</h1>
        <p>Acompanhamento de doações e implantações de órgãos</p>
      </div>

      {erro && <div className="alerta alerta-erro">{erro}</div>}

      {/* Controles de Filtro */}
      <div className="controles-filtro">
        <div className="filtro-ano">
          <label htmlFor="filtro-ano">Filtrar por Ano:</label>
          <select
            id="filtro-ano"
            value={anoSelecionado || ''}
            onChange={(e) => selecionarAno(e.target.value ? parseInt(e.target.value) : null)}
            className="select-ano"
          >
            <option value="">Todos os anos</option>
            {anosDisponiveis.map(ano => (
              <option key={ano} value={ano}>{ano}</option>
            ))}
          </select>
        </div>

        <div className="filtro-ano">
          <label htmlFor="filtro-periodicidade">Periodicidade:</label>
          <select
            id="filtro-periodicidade"
            value={periodicidade}
            onChange={(e) => {
              const valor = e.target.value;
              setPeriodicidade(valor);
              if (abas === 'protocolo') {
                carregarEstatisticasProtocolo(anoSelecionado, valor, mesSelecionado);
              }
            }}
            className="select-ano"
          >
            <option value="ANUAL">Anual</option>
            <option value="MENSAL">Mensal</option>
          </select>
        </div>

        {periodicidade === 'MENSAL' && (
          <div className="filtro-ano">
            <label htmlFor="filtro-mes">Mes:</label>
            <select
              id="filtro-mes"
              value={mesSelecionado}
              onChange={(e) => {
                setMesSelecionado(e.target.value);
                if (abas === 'protocolo') {
                  carregarEstatisticasProtocolo(anoSelecionado, periodicidade, e.target.value);
                }
              }}
              className="select-ano"
            >
              <option value="">Todos</option>
              {Array.from({ length: 12 }).map((_, idx) => (
                <option key={`mes-${idx + 1}`} value={idx + 1}>{idx + 1}</option>
              ))}
            </select>
          </div>
        )}

        <div className="abas">
          <button
            className={`aba ${abas === 'geral' ? 'ativa' : ''}`}
            onClick={() => selecionarAba('geral')}
          >
            📈 Visão Geral
          </button>
          <button
            className={`aba ${abas === 'pacientes' ? 'ativa' : ''}`}
            onClick={() => selecionarAba('pacientes')}
          >
            👥 Por Paciente
          </button>
          <button
            className={`aba ${abas === 'protocolo' ? 'ativa' : ''}`}
            onClick={() => selecionarAba('protocolo')}
          >
            🧾 Por Protocolo
          </button>
        </div>
      </div>

      {/* Conteúdo por Aba */}
      {carregando ? (
        <div className="carregando">⏳ Carregando estatísticas...</div>
      ) : (
        <>
          {/* ABA: VISÃO GERAL */}
          {abas === 'geral' && estatisticasGerais && (
            <div className="aba-conteudo">
              {/* Cards de Resumo */}
              <div className="resumo-cards">
                <div className="card card-primaria">
                  <div className="card-icon">🫀</div>
                  <div className="card-conteudo">
                    <div className="card-valor">{estatisticasGerais.totalOrgaosDisponiveis}</div>
                    <div className="card-label">Total de Órgãos Disponibilizados</div>
                  </div>
                </div>

                <div className="card card-sucesso">
                  <div className="card-icon">✓</div>
                  <div className="card-conteudo">
                    <div className="card-valor">{estatisticasGerais.orgaosImplantados}</div>
                    <div className="card-label">Órgãos Implantados</div>
                  </div>
                </div>

                <div className="card card-aviso">
                  <div className="card-icon">⚠</div>
                  <div className="card-conteudo">
                    <div className="card-valor">{estatisticasGerais.orgaosDescartados}</div>
                    <div className="card-label">Órgãos Descartados</div>
                  </div>
                </div>

                <div className="card card-info">
                  <div className="card-icon">👥</div>
                  <div className="card-conteudo">
                    <div className="card-valor">{estatisticasGerais.receptoresUnicos}</div>
                    <div className="card-label">Receptores Únicos</div>
                  </div>
                </div>

                <div className="card card-doador">
                  <div className="card-icon">❤️</div>
                  <div className="card-conteudo">
                    <div className="card-valor">{estatisticasGerais.totalDoadores}</div>
                    <div className="card-label">Total de Doadores</div>
                  </div>
                </div>

                <div className="card card-percentual">
                  <div className="card-icon">📊</div>
                  <div className="card-conteudo">
                    <div className="card-valor">{taxaImplantacaoFormatada}%</div>
                    <div className="card-label">Taxa de Implantação</div>
                  </div>
                </div>
              </div>

              {/* Gráficos */}
              <div className="graficos-container">
                <div className="grafico-item">
                  <h3>Distribuição de Órgãos</h3>
                  <div className="grafico-pizza">
                    <div className="legenda">
                      <div className="legenda-item">
                        <span className="cor implantado"></span>
                        <span>Implantados: {orgaosImplantados}</span>
                      </div>
                      <div className="legenda-item">
                        <span className="cor descartado"></span>
                        <span>Descartados: {orgaosDescartados}</span>
                      </div>
                      <div className="legenda-item">
                        <span className="cor aguardando"></span>
                        <span>Aguardando: {orgaosAguardando}</span>
                      </div>
                    </div>
                    <svg className="pizza-svg" viewBox="0 0 200 200">
                      <circle cx="100" cy="100" r="90" fill="none" strokeWidth="30"
                        stroke="url(#gradient-pizza)" strokeDasharray={`${pizzaDash} ${pizzaCircumference}`} />
                    </svg>
                  </div>
                </div>

                <div className="grafico-item">
                  <h3>Taxa de Sucesso vs Descarte</h3>
                  <div className="barra-progresso">
                    <div className="barra-container">
                      <div
                        className="barra-sucesso"
                        style={{
                          width: `${percentualImplantados}%`
                        }}
                      >
                        {orgaosImplantados} implantados
                      </div>
                      <div
                        className="barra-descarte"
                        style={{
                          width: `${percentualDescartados}%`
                        }}
                      >
                        {orgaosDescartados} descartados
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          )}

          {/* ABA: POR PACIENTE */}
          {abas === 'pacientes' && (
            <div className="aba-conteudo">
              <div className="filtro-paciente">
                <input
                  type="text"
                  placeholder="🔍 Filtrar por nome do paciente..."
                  value={filtroNomePaciente}
                  onChange={(e) => setFiltroNomePaciente(e.target.value)}
                  className="input-filtro"
                />
                <input
                  type="text"
                  placeholder="🔍 Filtrar por receptor (nome ou CPF)..."
                  value={filtroReceptor}
                  onChange={(e) => setFiltroReceptor(e.target.value)}
                  className="input-filtro"
                />
                <span className="resultado-filtro">
                  {pacientesFiltrados.length} de {estatisticasPorPaciente.length} pacientes
                </span>
              </div>

              {pacientesFiltrados.length === 0 ? (
                <div className="sem-dados">
                  <p>Nenhum paciente encontrado para o filtro selecionado.</p>
                </div>
              ) : (
                <div className="pacientes-lista">
                  {pacientesFiltrados.map((paciente) => (
                    <details key={paciente.pacienteId} className="paciente-item">
                      <summary className="paciente-header">
                        <div className="paciente-info">
                          <strong>{paciente.nomePaciente}</strong>
                          <span className="cpf">CPF: {formatarCpf(paciente.cpfPaciente)}</span>
                          <span className="data-doacao">
                            {new Date(paciente.dataDoacao).toLocaleDateString('pt-BR')}
                          </span>
                        </div>
                        <div className="paciente-resumo">
                          <span className="badge-info">{paciente.totalOrgaos} órgãos</span>
                          <span className="badge-sucesso">{paciente.orgaosImplantados.length} implantados</span>
                          <span className="badge-aviso">{paciente.orgaosDescartados.length} descartados</span>
                        </div>
                      </summary>

                      <div className="paciente-detalhes">
                        {/* Órgãos Implantados */}
                        {paciente.orgaosImplantados.length > 0 && (
                          <div className="detalhes-secao">
                            <h4>✓ Órgãos Implantados ({paciente.orgaosImplantados.length})</h4>
                            <table className="tabela-detalhes">
                              <thead>
                                <tr>
                                  <th>Órgão</th>
                                  <th>Receptor</th>
                                  <th>CPF Receptor</th>
                                  <th>Hospital</th>
                                  <th>Data Implantação</th>
                                </tr>
                              </thead>
                              <tbody>
                                {paciente.orgaosImplantados.map((orgao, idx) => (
                                  <tr key={idx}>
                                    <td><strong>{orgao.nomeOrgao}</strong></td>
                                    <td>{orgao.nomeReceptor}</td>
                                    <td>{formatarCpf(orgao.cpfReceptor)}</td>
                                    <td>{orgao.hospitalReceptor}</td>
                                    <td>{new Date(orgao.dataImplantacao).toLocaleDateString('pt-BR')}</td>
                                  </tr>
                                ))}
                              </tbody>
                            </table>
                          </div>
                        )}

                        {/* Órgãos Descartados */}
                        {paciente.orgaosDescartados.length > 0 && (
                          <div className="detalhes-secao">
                            <h4>⚠ Órgãos Descartados ({paciente.orgaosDescartados.length})</h4>
                            <table className="tabela-detalhes">
                              <thead>
                                <tr>
                                  <th>Órgão</th>
                                  <th>Motivo</th>
                                  <th>Data Descarte</th>
                                </tr>
                              </thead>
                              <tbody>
                                {paciente.orgaosDescartados.map((orgao, idx) => (
                                  <tr key={idx}>
                                    <td><strong>{orgao.nomeOrgao}</strong></td>
                                    <td>{orgao.motivo}</td>
                                    <td>{new Date(orgao.dataDescarte).toLocaleDateString('pt-BR')}</td>
                                  </tr>
                                ))}
                              </tbody>
                            </table>
                          </div>
                        )}
                      </div>
                    </details>
                  ))}
                </div>
              )}
            </div>
          )}

          {abas === 'protocolo' && (
            <div className="aba-conteudo">
              <div className="detalhes-secao">
                <h4>Auditoria - Protocolos sem estatística preenchida</h4>
                <p className="note">Lista de protocolos que ainda não receberam preenchimento completo pela Central.</p>
                {protocolosSemEstatistica.length === 0 ? (
                  <p className="pendencia-ok">Nenhum protocolo pendente de estatística.</p>
                ) : (
                  <ul className="lista-faltantes">
                    {protocolosSemEstatistica.map((item) => (
                      <li key={`pendente-estat-${item.protocoloMEId}`}>
                        <strong>{item.numeroProtocolo || `Protocolo ${item.protocoloMEId}`}</strong>
                        {item.nomeDoador ? ` - ${item.nomeDoador}` : ''}
                        {item.hospitalOrigem ? ` - ${item.hospitalOrigem}` : ''}
                        {item.status ? ` - ${item.status}` : ''}
                      </li>
                    ))}
                  </ul>
                )}
              </div>

              <div className="detalhes-secao">
                <h4>Estatistica por Protocolo ME ({periodicidade.toLowerCase()})</h4>
                <table className="tabela-detalhes">
                  <thead>
                    <tr>
                      <th>Protocolo</th>
                      <th>Doador</th>
                      <th>Ano</th>
                      <th>Mes</th>
                      <th>Atualizado por</th>
                      <th>Acoes</th>
                    </tr>
                  </thead>
                  <tbody>
                    {estatisticasProtocolo.map((item) => (
                      <tr key={`estat-protocolo-${item.protocoloMEId}`}>
                        <td>{item.numeroProtocolo || `Protocolo ${item.protocoloMEId}`}</td>
                        <td>{item.nomeDoador || '-'}</td>
                        <td>{item.anoCompetencia || '-'}</td>
                        <td>{item.mesCompetencia || '-'}</td>
                        <td>{item.atualizadoPor || '-'}</td>
                        <td>
                          <button className="modal-report-button" onClick={() => selecionarProtocolo(item)}>
                            Preencher/Editar
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>

              {protocoloSelecionado && (
                <div className="detalhes-secao">
                  <h4>Formulario da Central - {protocoloSelecionado.numeroProtocolo || protocoloSelecionado.protocoloMEId}</h4>
                  <div className="resumo-cards">
                    {CAMPOS_ESTATISTICA_PROTOCOLO.map((campo) => (
                      <div className="card" key={`campo-${campo.key}`}>
                        <div className="card-conteudo">
                          <div className="card-label">{campo.label}</div>
                          {CAMPOS_SIM_NAO.has(campo.key) ? (
                            <select
                              value={normalizarSimNao(camposForm[campo.key])}
                              onChange={(e) => setCamposForm((prev) => ({ ...prev, [campo.key]: e.target.value }))}
                              className="input-filtro"
                            >
                              <option value="NAO">Não</option>
                              <option value="SIM">Sim</option>
                            </select>
                          ) : (
                            <input
                              type="text"
                              value={camposForm[campo.key] || ''}
                              onChange={(e) => setCamposForm((prev) => ({ ...prev, [campo.key]: e.target.value }))}
                              className="input-filtro"
                            />
                          )}
                        </div>
                      </div>
                    ))}
                  </div>
                  <button className="modal-report-button" onClick={salvarEstatistica} disabled={salvando}>
                    {salvando ? 'Salvando...' : 'Salvar estatistica do protocolo'}
                  </button>
                </div>
              )}
            </div>
          )}
        </>
      )}
    </div>
  );
};

export default EstatisticasPage;
