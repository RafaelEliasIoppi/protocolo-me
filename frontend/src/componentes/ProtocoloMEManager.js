import React, { useState, useEffect } from 'react';
import apiClient from '../services/apiClient';
import '../styles/ProtocoloMEManager.css';

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
    centralTransplantesId: ''
  });

  const [carregando, setCarregando] = useState(false);
  const [erro, setErro] = useState('');
  const [sucesso, setSucesso] = useState('');
  const [filtroStatus, setFiltroStatus] = useState('');
  const [protocoloSelecionado, setProtocoloSelecionado] = useState(null);

  const statusOpcoes = [
    { valor: 'NOTIFICADO', label: 'Notificado', cor: 'azul' },
    { valor: 'EM_PROCESSO', label: 'Em Processo', cor: 'amarelo' },
    { valor: 'MORTE_CEREBRAL_CONFIRMADA', label: 'Morte Cerebral Confirmada', cor: 'laranja' },
    { valor: 'FAMILIA_INFORMADA', label: 'Família Informada', cor: 'verde' },
    { valor: 'ORGAOS_PRESERVADOS', label: 'Órgãos Preservados', cor: 'roxo' },
    { valor: 'APTO_TRANSPLANTE', label: 'Apto para Transplante', cor: 'verde-escuro' },
    { valor: 'CONTRAINDICADO', label: 'Contraindicado', cor: 'vermelho' },
    { valor: 'FINALIZADO', label: 'Finalizado', cor: 'cinza' }
  ];

  useEffect(() => {
    carregarProtocolos();
  }, []);

  const carregarProtocolos = async () => {
    setCarregando(true);
    try {
      const response = await apiClient.get('/api/protocolos-me');
      setProtocolos(response.data);
    } catch (err) {
      setErro('Erro ao carregar protocolos');
    } finally {
      setCarregando(false);
    }
  };

  const handleChangeForm = (e) => {
    const { name, value } = e.target;
    setFormProtocolo(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleCriarProtocolo = async (e) => {
    e.preventDefault();
    setErro('');
    setSucesso('');

    if (!formProtocolo.numeroProtocolo || !formProtocolo.hospitalOrigem) {
      setErro('Número do protocolo e hospital origem são obrigatórios');
      return;
    }

    try {
      const protocoloData = {
        numeroProtocolo: formProtocolo.numeroProtocolo,
        hospitalOrigem: formProtocolo.hospitalOrigem,
        medicoResponsavel: formProtocolo.medicoResponsavel,
        enfermeiro: formProtocolo.enfermeiro,
        diagnosticoBasico: formProtocolo.diagnosticoBasico,
        causaMorte: formProtocolo.causaMorte,
        observacoes: formProtocolo.observacoes,
        orgaosDisponiveis: formProtocolo.orgaosDisponiveis,
        paciente: formProtocolo.pacienteId ? { id: parseInt(formProtocolo.pacienteId) } : null,
        centralTransplantes: formProtocolo.centralTransplantesId ? { id: parseInt(formProtocolo.centralTransplantesId) } : null
      };
      const response = await apiClient.post('/api/protocolos-me', protocoloData);
      setProtocolos([...protocolos, response.data]);
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
        centralTransplantesId: ''
      });
      setSucesso('Protocolo criado com sucesso!');
      setTimeout(() => setSucesso(''), 3000);
    } catch (err) {
      setErro('Erro ao criar protocolo');
    }
  };

  const registrarTesteClinico1 = async (protocoloId) => {
    try {
      const response = await apiClient.post(`/api/protocolos-me/${protocoloId}/teste-clinico-1`);
      atualizarProtocoloNaLista(protocoloId, response.data);
      setSucesso('Teste clínico 1 registrado!');
    } catch (err) {
      setErro('Erro ao registrar teste');
    }
  };

  const registrarTesteClinico2 = async (protocoloId) => {
    try {
      const response = await apiClient.post(`/api/protocolos-me/${protocoloId}/teste-clinico-2`);
      atualizarProtocoloNaLista(protocoloId, response.data);
      setSucesso('Teste clínico 2 registrado!');
    } catch (err) {
      setErro('Erro ao registrar teste');
    }
  };

  const confirmarMorteCerebral = async (protocoloId) => {
    try {
      const response = await apiClient.post(`/api/protocolos-me/${protocoloId}/confirmar-morte-cerebral`);
      atualizarProtocoloNaLista(protocoloId, response.data);
      setSucesso('Morte cerebral confirmada!');
    } catch (err) {
      setErro('Erro ao confirmar morte cerebral');
    }
  };

  const registrarNotificacaoFamilia = async (protocoloId) => {
    try {
      const response = await apiClient.post(`/api/protocolos-me/${protocoloId}/notificar-familia`);
      atualizarProtocoloNaLista(protocoloId, response.data);
      setSucesso('Notificação da família registrada!');
    } catch (err) {
      setErro('Erro ao registrar notificação');
    }
  };

  const registrarPreservacaoOrgaos = async (protocoloId) => {
    try {
      const response = await apiClient.post(`/api/protocolos-me/${protocoloId}/preservacao-orgaos`);
      atualizarProtocoloNaLista(protocoloId, response.data);
      setSucesso('Preservação de órgãos registrada!');
    } catch (err) {
      setErro('Erro ao registrar preservação');
    }
  };

  const alterarStatus = async (protocoloId, novoStatus) => {
    try {
      const response = await apiClient.patch(
        `/api/protocolos-me/${protocoloId}/status`,
        {},
        { params: { status: novoStatus } }
      );
      atualizarProtocoloNaLista(protocoloId, response.data);
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
        <form onSubmit={handleCriarProtocolo}>
          <div className="form-row">
            <input
              type="text"
              name="numeroProtocolo"
              placeholder="Número do Protocolo *"
              value={formProtocolo.numeroProtocolo}
              onChange={handleChangeForm}
              required
            />
            <input
              type="text"
              name="hospitalOrigem"
              placeholder="Hospital Origem *"
              value={formProtocolo.hospitalOrigem}
              onChange={handleChangeForm}
              required
            />
            <input
              type="text"
              name="medicoResponsavel"
              placeholder="Médico Responsável"
              value={formProtocolo.medicoResponsavel}
              onChange={handleChangeForm}
            />
          </div>
          <div className="form-row">
            <input
              type="text"
              name="diagnosticoBasico"
              placeholder="Diagnóstico Básico"
              value={formProtocolo.diagnosticoBasico}
              onChange={handleChangeForm}
            />
            <input
              type="text"
              name="orgaosDisponiveis"
              placeholder="Órgãos Disponíveis"
              value={formProtocolo.orgaosDisponiveis}
              onChange={handleChangeForm}
            />
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
                  <h4>{protocolo.numeroProtocolo}</h4>
                  <p className="hospital">{protocolo.hospitalOrigem}</p>
                </div>
                <span className={`status-badge status-${obterCorStatus(protocolo.status)}`}>
                  {obterLabelStatus(protocolo.status)}
                </span>
              </div>

              <div className="protocolo-info">
                <p><strong>Paciente ID:</strong> {protocolo.pacienteId || 'N/A'}</p>
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
                    <button onClick={() => registrarTesteClinico1(protocolo.id)} className="btn-pequeno">
                      Registrar
                    </button>
                  )}
                </div>
                <div className="check-item">
                  <input type="checkbox" checked={protocolo.testeClinico2Realizado} readOnly />
                  <label>Teste Clínico 2</label>
                  {!protocolo.testeClinico2Realizado && (
                    <button onClick={() => registrarTesteClinico2(protocolo.id)} className="btn-pequeno">
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
                  <button onClick={() => confirmarMorteCerebral(protocolo.id)} className="btn-acao btn-confirmacao">
                    ✓ Confirmar Morte Cerebral
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
              </div>
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
