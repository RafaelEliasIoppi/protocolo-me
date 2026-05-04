import { useEffect, useRef, useState } from 'react';
import protocoloService from '../services/protocoloService';
import '../styles/ProtocoloMEManager.css';
import OrgaoDoadoManager from './OrgaoDoadoManager';

const ProtocoloMEManager = () => {
  const [protocolos, setProtocolos] = useState([]);
  const [formProtocolo, setFormProtocolo] = useState({
    numeroProtocolo: '',
    hospitalOrigem: '',
    pacienteId: '',
    medicoResponsavel: '',
    enfermeiro: '',
    diagnosticoBasico: '',
    causaMorte: '',
    observacoes: '',
    orgaosDisponiveis: '',
    centralTransplantesId: '',
    autopsiaAutorizada: false,
    preservacaoOrgaos: false
  });

  const [carregando, setCarregando] = useState(false);
  const [erro, setErro] = useState('');
  const [sucesso, setSucesso] = useState('');
  const [filtroStatus, setFiltroStatus] = useState('');
  const [protocolosExpandidos, setProtocolosExpandidos] = useState(new Set());
  const montadoRef = useRef(true);

  const statusOpcoes = [
    { valor: 'NOTIFICADO', label: 'Notificado', cor: 'azul' },
    { valor: 'EM_PROCESSO', label: 'Em Processo', cor: 'amarelo' },
    { valor: 'MORTE_CEREBRAL_CONFIRMADA', label: 'Morte Cerebral Confirmada', cor: 'laranja' },
    { valor: 'ENTREVISTA_FAMILIAR', label: 'Entrevista Familiar', cor: 'roxo' },
    { valor: 'DOACAO_AUTORIZADA', label: 'Doação Autorizada', cor: 'verde-escuro' },
    { valor: 'FAMILIA_RECUSOU', label: 'Família Recusou', cor: 'vermelho' },
    { valor: 'CONTRAINDICADO', label: 'Contraindicado', cor: 'vermelho' },
    { valor: 'FINALIZADO', label: 'Finalizado', cor: 'cinza' }
  ];

  useEffect(() => {
    return () => {
      montadoRef.current = false;
    };
  }, []);

  useEffect(() => {
    let ativo = true;

    const carregar = async () => {
      setCarregando(true);
      try {
        const dados = await protocoloService.listar();
        if (!ativo || !montadoRef.current) {
          return;
        }
        setProtocolos(Array.isArray(dados) ? dados : []);
      } catch (err) {
        if (!ativo || !montadoRef.current) {
          return;
        }
        setErro('Erro ao carregar protocolos');
      } finally {
        if (ativo && montadoRef.current) {
          setCarregando(false);
        }
      }
    };

    carregar();

    return () => {
      ativo = false;
    };
  }, []);

  const atualizarCampoFormulario = (e) => {
    const { name, value, type, checked } = e.target;
    setFormProtocolo(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  const criarProtocolo = async (e) => {
    e.preventDefault();
    setErro('');
    setSucesso('');

    if (!formProtocolo.pacienteId || !formProtocolo.diagnosticoBasico) {
      setErro('Paciente e diagnóstico básico são obrigatórios');
      return;
    }

    try {
      const payload = {
        pacienteId: parseInt(formProtocolo.pacienteId, 10),
        diagnosticoBasico: formProtocolo.diagnosticoBasico,
        numeroProtocolo: formProtocolo.numeroProtocolo
      };

      const criado = await protocoloService.criar(payload);
      setProtocolos([...protocolos, criado]);
      setFormProtocolo({
        numeroProtocolo: '',
        hospitalOrigem: '',
        pacienteId: '',
        medicoResponsavel: '',
        enfermeiro: '',
        diagnosticoBasico: '',
        causaMorte: '',
        observacoes: '',
        orgaosDisponiveis: '',
        centralTransplantesId: '',
        autopsiaAutorizada: false,
        preservacaoOrgaos: false
      });
      setSucesso('Protocolo criado com sucesso!');
      setTimeout(() => setSucesso(''), 3000);
    } catch (err) {
      setErro('Erro ao criar protocolo');
    }
  };

  const registrarTesteClinco1 = async (protocoloId) => {
    try {
      const atualizado = await protocoloService.registrarTesteClinico1(protocoloId);
      atualizarProtocoloNaLista(protocoloId, atualizado);
      setSucesso('Teste clínico 1 registrado!');
    } catch (err) {
      setErro('Erro ao registrar teste');
    }
  };

  const registrarTesteClinco2 = async (protocoloId) => {
    try {
      const atualizado = await protocoloService.registrarTesteClinico2(protocoloId);
      atualizarProtocoloNaLista(protocoloId, atualizado);
      setSucesso('Teste clínico 2 registrado!');
    } catch (err) {
      setErro('Erro ao registrar teste');
    }
  };

  const confirmarMorteCerebral = async (protocoloId) => {
    try {
      const atualizado = await protocoloService.confirmarMorteCerebral(protocoloId);
      atualizarProtocoloNaLista(protocoloId, atualizado);
      setSucesso('Morte cerebral confirmada!');
    } catch (err) {
      setErro('Erro ao confirmar morte cerebral');
    }
  };

  const registrarNotificacaoFamilia = async (protocoloId) => {
    try {
      const atualizado = await protocoloService.registrarNotificacaoFamilia(protocoloId);
      atualizarProtocoloNaLista(protocoloId, atualizado);
      setSucesso('Notificação da família registrada!');
    } catch (err) {
      setErro('Erro ao registrar notificação');
    }
  };

  const registrarPreservacaoOrgaos = async (protocoloId) => {
    try {
      const atualizado = await protocoloService.registrarPreservacaoOrgaos(protocoloId);
      atualizarProtocoloNaLista(protocoloId, atualizado);
      setSucesso('Preservação de órgãos registrada!');
    } catch (err) {
      setErro('Erro ao registrar preservação');
    }
  };

  const alterarStatus = async (protocoloId, novoStatus) => {
    try {
      const atualizado = await protocoloService.atualizarStatus(protocoloId, novoStatus);
      atualizarProtocoloNaLista(protocoloId, atualizado);
      setSucesso('Status atualizado!');
    } catch (err) {
      setErro('Erro ao alterar status');
    }
  };

  const atualizarProtocoloNaLista = (id, protocoloAtualizado) => {
    setProtocolos(protocolos.map(p => p.id === id ? protocoloAtualizado : p));
  };

  const obterCorStatus = (status) => {
    const opcao = statusOpcoes.find(s => s.valor === status);
    return opcao?.cor || 'cinza';
  };

  const obterLabelStatus = (status) => {
    const opcao = statusOpcoes.find(s => s.valor === status);
    return opcao?.label || status;
  };

  const toggleExpandirProtocolo = (protocoloId) => {
    const novoExpandidos = new Set(protocolosExpandidos);
    if (novoExpandidos.has(protocoloId)) {
      novoExpandidos.delete(protocoloId);
    } else {
      novoExpandidos.add(protocoloId);
    }
    setProtocolosExpandidos(novoExpandidos);
  };

  const protocolosFiltrados = filtroStatus
    ? protocolos.filter(p => p.status === filtroStatus)
    : protocolos;

  return (
    <div className="protocolo-manager-container">
      <h2>Gerenciador de Protocolos de Morte Encefálica</h2>
      <p className="subtitle">Central de Transplantes</p>

      {erro && <div className="alerta alerta-erro">{erro}</div>}
      {sucesso && <div className="alerta alerta-sucesso">{sucesso}</div>}

      <div className="protocolo-form-section">
        <h3>Novo Protocolo de ME</h3>
        <form onSubmit={criarProtocolo}>
          <div className="form-row">
            <input
              type="number"
              name="pacienteId"
              placeholder="ID do Paciente *"
              value={formProtocolo.pacienteId}
              onChange={atualizarCampoFormulario}
              required
            />
            <input
              type="text"
              name="numeroProtocolo"
              placeholder="Número do Protocolo"
              value={formProtocolo.numeroProtocolo}
              onChange={atualizarCampoFormulario}
            />
            <input
              type="text"
              name="hospitalOrigem"
              placeholder="Hospital Origem"
              value={formProtocolo.hospitalOrigem}
              onChange={atualizarCampoFormulario}
            />
            <input
              type="text"
              name="medicoResponsavel"
              placeholder="Médico Responsável"
              value={formProtocolo.medicoResponsavel}
              onChange={atualizarCampoFormulario}
            />
          </div>
          <div className="form-row">
            <input
              type="text"
              name="diagnosticoBasico"
              placeholder="Diagnóstico Básico"
              value={formProtocolo.diagnosticoBasico}
              onChange={atualizarCampoFormulario}
            />
            <input
              type="text"
              name="orgaosDisponiveis"
              placeholder="Órgãos Disponíveis"
              value={formProtocolo.orgaosDisponiveis}
              onChange={atualizarCampoFormulario}
            />
          </div>
          <div className="form-row checkboxes">
            <label>
              <input
                type="checkbox"
                name="autopsiaAutorizada"
                checked={formProtocolo.autopsiaAutorizada}
                onChange={atualizarCampoFormulario}
              /> Autópsia Autorizada
            </label>
            <label>
              <input
                type="checkbox"
                name="preservacaoOrgaos"
                checked={formProtocolo.preservacaoOrgaos}
                onChange={atualizarCampoFormulario}
              /> Preservação de Órgãos
            </label>
          </div>
          <button type="submit" className="btn-criar">Criar Protocolo</button>
        </form>
      </div>

      <div className="filtro-section">
        <label htmlFor="filtro">Filtrar por Status:</label>
        <select
          id="filtro"
          value={filtroStatus}
          onChange={(e) => setFiltroStatus(e.target.value)}
          className="select-filtro"
        >
          <option value="">Todos os Status</option>
          {statusOpcoes.map(opcao => (
            <option key={opcao.valor} value={opcao.valor}>
              {opcao.label}
            </option>
          ))}
        </select>
        <button onClick={carregarProtocolos} className="btn-recarregar">
          🔄 Atualizar
        </button>
      </div>

      {carregando && <div className="carregando">Carregando...</div>}

      <div className="protocolos-grid">
        {protocolosFiltrados.length > 0 ? (
          protocolosFiltrados.map(protocolo => (
            <div key={protocolo.id} className="protocolo-card">
              <div className="protocolo-header">
                <div>
                  <h4>{protocolo.numeroProtocolo || `ID: ${protocolo.id}`}</h4>
                  <p className="hospital">{protocolo.hospitalOrigem || 'Hospital não informado'}</p>
                </div>
                <span className={`status-badge status-${obterCorStatus(protocolo.status)}`}>
                  {obterLabelStatus(protocolo.status)}
                </span>
              </div>

              <div className="protocolo-info">
                <p><strong>Paciente ID:</strong> {protocolo.pacienteId || (protocolo.paciente && protocolo.paciente.id) || 'N/A'}</p>
                <p><strong>Médico:</strong> {protocolo.medicoResponsavel || 'N/A'}</p>
                <p><strong>Diagnóstico:</strong> {protocolo.diagnosticoBasico || 'N/A'}</p>
                <p><strong>Órgãos:</strong> {protocolo.orgaosDisponiveis || 'N/A'}</p>
              </div>

              <div className="protocolo-checklist">
                <h5>Checklist de Procedimentos</h5>
                <div className="check-item">
                  <input type="checkbox" checked={protocolo.testeClinico1Realizado} readOnly />
                  <label>Teste Clínico 1</label>
                  {!protocolo.testeClinico1Realizado && (
                    <button onClick={() => registrarTesteClinco1(protocolo.id)} className="btn-pequeno">
                      Registrar
                    </button>
                  )}
                </div>
                <div className="check-item">
                  <input type="checkbox" checked={protocolo.testeClinico2Realizado} readOnly />
                  <label>Teste Clínico 2</label>
                  {!protocolo.testeClinico2Realizado && (
                    <button onClick={() => registrarTesteClinco2(protocolo.id)} className="btn-pequeno">
                      Registrar
                    </button>
                  )}
                </div>
                <div className="check-item">
                  <input type="checkbox" checked={protocolo.familiaNotificada} readOnly />
                  <label>Família Notificada</label>
                  {!protocolo.familiaNotificada && (
                    <button onClick={() => registrarNotificacaoFamilia(protocolo.id)} className="btn-pequeno">
                      Registrar
                    </button>
                  )}
                </div>
                <div className="check-item">
                  <input type="checkbox" checked={protocolo.preservacaoOrgaos} readOnly />
                  <label>Preservação de Órgãos</label>
                  {!protocolo.preservacaoOrgaos && (
                    <button onClick={() => registrarPreservacaoOrgaos(protocolo.id)} className="btn-pequeno">
                      Registrar
                    </button>
                  )}
                </div>
              </div>

              <div className="protocolo-actions">
                <div className="actions-row">
                  <button
                    onClick={() => confirmarMorteCerebral(protocolo.id)}
                    className="btn-acao btn-confirmacao"
                    disabled={protocolo.dataConfirmacaoME != null}
                  >
                    {protocolo.dataConfirmacaoME ? '✓ ME Confirmada' : '✓ Confirmar Morte Cerebral'}
                  </button>
                </div>
                <div className="actions-row">
                  <select
                    value={protocolo.status}
                    onChange={(e) => alterarStatus(protocolo.id, e.target.value)}
                    className="select-status"
                  >
                    {statusOpcoes.map(opcao => (
                      <option key={opcao.valor} value={opcao.valor}>
                        {opcao.label}
                      </option>
                    ))}
                  </select>
                </div>
                <div className="actions-row">
                  <button
                    onClick={() => toggleExpandirProtocolo(protocolo.id)}
                    className="btn-acao btn-info"
                  >
                    {protocolosExpandidos.has(protocolo.id) ? '▼ Ocultar Órgãos' : '▶ Ver Órgãos Doados'}
                  </button>
                </div>
              </div>

              {protocolosExpandidos.has(protocolo.id) && (
                <div className="protocolo-orgaos-section">
                  <OrgaoDoadoManager protocoloId={protocolo.id} />
                </div>
              )}
            </div>
          ))
        ) : (
          <div className="vazio">
            <p>Nenhum protocolo encontrado</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default ProtocoloMEManager;
