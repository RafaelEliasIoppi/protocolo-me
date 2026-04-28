import { useEffect, useState } from 'react';
import apiClient from '../services/apiClient';
import '../styles/OrgaoDoadoManager.css';
import { formatarCpf } from '../utils/cpf';

const OrgaoDoadoManager = ({ protocoloId }) => {
  const [orgaos, setOrgaos] = useState([]);
  const [formOrgao, setFormOrgao] = useState({
    nomeOrgao: '',
    status: 'AGUARDANDO_IMPLANTACAO',
    motivo: '',
    hospitalReceptor: '',
    pacienteReceptor: '',
    cpfReceptor: '',
    motivoDescarte: '',
    observacoes: '',
  });

  const [carregando, setCarregando] = useState(false);
  const [erro, setErro] = useState('');
  const [sucesso, setSucesso] = useState('');
  const [orgaoEditando, setOrgaoEditando] = useState(null);
  const [mostrarFormulario, setMostrarFormulario] = useState(false);
  const [estatisticas, setEstatisticas] = useState(null);
  const [modalAcao, setModalAcao] = useState({
    aberto: false,
    tipo: null,
    orgaoId: null,
    nomeOrgao: '',
    hospitalReceptor: '',
    pacienteReceptor: '',
    motivo: ''
  });

  const statusOpcoes = [
    { valor: 'AGUARDANDO_IMPLANTACAO', label: 'Aguardando Implantação' },
    { valor: 'IMPLANTADO', label: 'Implantado' },
    { valor: 'DESCARTADO', label: 'Descartado' },
    { valor: 'PROCESSANDO', label: 'Processando' },
    { valor: 'FALHA_IMPLANTACAO', label: 'Falha na Implantação' },
  ];

  const orgaoPadraoOpcoes = [
    'Coração',
    'Pulmão',
    'Fígado',
    'Rins',
    'Pâncreas',
    'Intestino',
    'Córneas',
    'Pele',
    'Ossos',
    'Tendões',
    'Válvulas Cardíacas'
  ];

  const motivoDescarteOpcoes = [
    'Contraindicação médica',
    'Deterioração do órgão',
    'Incompatibilidade',
    'Falha no transporte',
    'Teste positivo para doença',
    'Incompatibilidade imunológica',
    'Outro'
  ];

  // Carregar órgãos ao montar o componente
  useEffect(() => {
    carregarOrgaos();
    carregarEstatisticas();
  }, [protocoloId]);

  const carregarOrgaos = async () => {
    setCarregando(true);
    setErro('');
    try {
      const response = await apiClient.get(`/api/orgaos-doados/protocolo/${protocoloId}`);
      setOrgaos(response.data || []);
    } catch (err) {
      setErro('Erro ao carregar órgãos doados');
      console.error(err);
    } finally {
      setCarregando(false);
    }
  };

  const carregarEstatisticas = async () => {
    try {
      const response = await apiClient.get(`/api/orgaos-doados/protocolo/${protocoloId}/estatisticas`);
      setEstatisticas(response.data);
    } catch (err) {
      console.error('Erro ao carregar estatísticas:', err);
    }
  };

  const atualizarCampoFormulario = (e) => {
    const { name, value } = e.target;

    if (name === 'cpfReceptor') {
      const cpfNumerico = value.replace(/\D/g, '').slice(0, 11);
      const cpfFormatado = cpfNumerico
        .replace(/(\d{3})(\d)/, '$1.$2')
        .replace(/(\d{3})(\d)/, '$1.$2')
        .replace(/(\d{3})(\d{1,2})$/, '$1-$2');

      setFormOrgao(prev => ({
        ...prev,
        [name]: cpfFormatado
      }));
      return;
    }

    setFormOrgao(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const limparFormulario = () => {
    setFormOrgao({
      nomeOrgao: '',
      status: 'AGUARDANDO_IMPLANTACAO',
      motivo: '',
      hospitalReceptor: '',
      pacienteReceptor: '',
      cpfReceptor: '',
      motivoDescarte: '',
      observacoes: '',
    });
    setOrgaoEditando(null);
  };

  const salvarOrgao = async (e) => {
    e.preventDefault();
    setErro('');
    setSucesso('');

    if (!formOrgao.nomeOrgao.trim()) {
      setErro('Nome do órgão é obrigatório');
      return;
    }

    try {
      const dadosOrgao = {
        nomeOrgao: formOrgao.nomeOrgao,
        status: formOrgao.status,
        motivo: formOrgao.motivo || null,
        hospitalReceptor: formOrgao.hospitalReceptor || null,
        pacienteReceptor: formOrgao.pacienteReceptor || null,
        cpfReceptor: formOrgao.cpfReceptor || null,
        motivoDescarte: formOrgao.motivoDescarte || null,
        observacoes: formOrgao.observacoes || null,
        protocoloME: { id: protocoloId }
      };

      if (orgaoEditando) {
        await apiClient.put(`/api/orgaos-doados/${orgaoEditando.id}`, dadosOrgao);
        setSucesso('Órgão atualizado com sucesso');
      } else {
        await apiClient.post('/api/orgaos-doados', dadosOrgao);
        setSucesso('Órgão doado registrado com sucesso');
      }

      limparFormulario();
      setMostrarFormulario(false);
      carregarOrgaos();
      carregarEstatisticas();
    } catch (err) {
      setErro('Erro ao salvar órgão doado');
      console.error(err);
    }
  };

  const editarOrgao = (orgao) => {
    setFormOrgao({
      nomeOrgao: orgao.nomeOrgao,
      status: orgao.status,
      motivo: orgao.motivo || '',
      hospitalReceptor: orgao.hospitalReceptor || '',
      pacienteReceptor: orgao.pacienteReceptor || '',
      cpfReceptor: orgao.cpfReceptor || '',
      motivoDescarte: orgao.motivoDescarte || '',
      observacoes: orgao.observacoes || '',
    });
    setOrgaoEditando(orgao);
    setMostrarFormulario(true);
  };

  const deletarOrgao = async (id) => {
    if (window.confirm('Tem certeza que deseja deletar este órgão doado?')) {
      try {
        await apiClient.delete(`/api/orgaos-doados/${id}`);
        setSucesso('Órgão removido com sucesso');
        carregarOrgaos();
        carregarEstatisticas();
      } catch (err) {
        setErro('Erro ao deletar órgão');
        console.error(err);
      }
    }
  };

  const abrirModalImplantacao = (orgao) => {
    setModalAcao({
      aberto: true,
      tipo: 'IMPLANTAR',
      orgaoId: orgao.id,
      nomeOrgao: orgao.nomeOrgao,
      hospitalReceptor: orgao.hospitalReceptor || '',
      pacienteReceptor: orgao.pacienteReceptor || '',
      motivo: ''
    });
  };

  const abrirModalDescarte = (orgao) => {
    setModalAcao({
      aberto: true,
      tipo: 'DESCARTAR',
      orgaoId: orgao.id,
      nomeOrgao: orgao.nomeOrgao,
      hospitalReceptor: '',
      pacienteReceptor: '',
      motivo: orgao.motivoDescarte || ''
    });
  };

  const fecharModalAcao = () => {
    setModalAcao({
      aberto: false,
      tipo: null,
      orgaoId: null,
      nomeOrgao: '',
      hospitalReceptor: '',
      pacienteReceptor: '',
      motivo: ''
    });
  };

  const confirmarModalAcao = async () => {
    if (!modalAcao.orgaoId || !modalAcao.tipo) return;

    setErro('');
    setSucesso('');

    if (modalAcao.tipo === 'IMPLANTAR') {
      if (!modalAcao.hospitalReceptor.trim() || !modalAcao.pacienteReceptor.trim()) {
        setErro('Informe hospital e paciente receptor para registrar implantação.');
        return;
      }

      try {
        await apiClient.post(`/api/orgaos-doados/${modalAcao.orgaoId}/implantar`, null, {
          params: {
            hospitalReceptor: modalAcao.hospitalReceptor.trim(),
            pacienteReceptor: modalAcao.pacienteReceptor.trim()
          }
        });
        setSucesso('Implantação registrada com sucesso');
        fecharModalAcao();
        carregarOrgaos();
        carregarEstatisticas();
      } catch (err) {
        setErro('Erro ao registrar implantação');
        console.error(err);
      }
      return;
    }

    if (!modalAcao.motivo.trim()) {
      setErro('Informe o motivo para registrar descarte.');
      return;
    }

    try {
      await apiClient.post(`/api/orgaos-doados/${modalAcao.orgaoId}/descartar`, null, {
        params: {
          motivo: modalAcao.motivo.trim()
        }
      });
      setSucesso('Descarte registrado com sucesso');
      fecharModalAcao();
      carregarOrgaos();
      carregarEstatisticas();
    } catch (err) {
      setErro('Erro ao registrar descarte');
      console.error(err);
    }
  };

  const getStatusBadge = (status) => {
    const statusMap = {
      'AGUARDANDO_IMPLANTACAO': { classe: 'badge-warning', label: 'Aguardando' },
      'IMPLANTADO': { classe: 'badge-success', label: 'Implantado' },
      'DESCARTADO': { classe: 'badge-danger', label: 'Descartado' },
      'PROCESSANDO': { classe: 'badge-info', label: 'Processando' },
      'FALHA_IMPLANTACAO': { classe: 'badge-danger', label: 'Falha' },
    };
    const statusInfo = statusMap[status] || { classe: 'badge-secondary', label: status };
    return <span className={`badge ${statusInfo.classe}`}>{statusInfo.label}</span>;
  };

  return (
    <div className="orgao-doado-manager">
      <div className="orgao-header">
        <h3>Rastreamento de Órgãos Doados</h3>
        <button
          className="btn-novo"
          onClick={() => {
            limparFormulario();
            setMostrarFormulario(!mostrarFormulario);
          }}
        >
          {mostrarFormulario ? 'Cancelar' : '+ Novo Órgão'}
        </button>
      </div>

      {erro && <div className="alerta alerta-erro">{erro}</div>}
      {sucesso && <div className="alerta alerta-sucesso">{sucesso}</div>}

      {/* Estatísticas */}
      {estatisticas && (
        <div className="orgao-estatisticas">
          <div className="stat-card">
            <div className="stat-number">{estatisticas.total}</div>
            <div className="stat-label">Total de Órgãos</div>
          </div>
          <div className="stat-card success">
            <div className="stat-number">{estatisticas.implantados}</div>
            <div className="stat-label">Implantados</div>
          </div>
          <div className="stat-card danger">
            <div className="stat-number">{estatisticas.descartados}</div>
            <div className="stat-label">Descartados</div>
          </div>
          <div className="stat-card warning">
            <div className="stat-number">{estatisticas.aguardando}</div>
            <div className="stat-label">Aguardando</div>
          </div>
        </div>
      )}

      {/* Formulário */}
      {mostrarFormulario && (
        <div className="orgao-form-section">
          <h4>{orgaoEditando ? 'Editar Órgão Doado' : 'Registrar Novo Órgão Doado'}</h4>
          <form onSubmit={salvarOrgao} className="orgao-form">
            <div className="form-row">
              <div className="form-group">
                <label>Órgão/Tecido *</label>
                <select
                  name="nomeOrgao"
                  value={formOrgao.nomeOrgao}
                  onChange={atualizarCampoFormulario}
                  required
                >
                  <option value="">Selecione um órgão</option>
                  {orgaoPadraoOpcoes.map(orgao => (
                    <option key={orgao} value={orgao}>{orgao}</option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label>Status *</label>
                <select
                  name="status"
                  value={formOrgao.status}
                  onChange={atualizarCampoFormulario}
                >
                  {statusOpcoes.map(status => (
                    <option key={status.valor} value={status.valor}>{status.label}</option>
                  ))}
                </select>
              </div>
            </div>

            {formOrgao.status === 'IMPLANTADO' && (
              <div className="form-row">
                <div className="form-group">
                  <label>Hospital Receptor</label>
                  <input
                    type="text"
                    name="hospitalReceptor"
                    placeholder="Nome do hospital"
                    value={formOrgao.hospitalReceptor}
                    onChange={atualizarCampoFormulario}
                  />
                </div>

                <div className="form-group">
                  <label>Paciente Receptor</label>
                  <input
                    type="text"
                    name="pacienteReceptor"
                    placeholder="Nome do paciente receptor"
                    value={formOrgao.pacienteReceptor}
                    onChange={atualizarCampoFormulario}
                  />
                </div>

                <div className="form-group">
                  <label>CPF Receptor</label>
                  <input
                    type="text"
                    name="cpfReceptor"
                    placeholder="CPF do receptor"
                    value={formOrgao.cpfReceptor}
                    onChange={atualizarCampoFormulario}
                    maxLength={14}
                  />
                </div>
              </div>
            )}

            {formOrgao.status === 'DESCARTADO' && (
              <div className="form-row">
                <div className="form-group full">
                  <label>Motivo do Descarte</label>
                  <select
                    name="motivoDescarte"
                    value={formOrgao.motivoDescarte}
                    onChange={atualizarCampoFormulario}
                  >
                    <option value="">Selecione um motivo</option>
                    {motivoDescarteOpcoes.map(motivo => (
                      <option key={motivo} value={motivo}>{motivo}</option>
                    ))}
                  </select>
                  {formOrgao.motivoDescarte === 'Outro' && (
                    <input
                      type="text"
                      name="motivoDescarte"
                      placeholder="Especifique o motivo"
                      value={formOrgao.motivoDescarte}
                      onChange={atualizarCampoFormulario}
                      className="motivo-outro"
                    />
                  )}
                </div>
              </div>
            )}

            <div className="form-row">
              <div className="form-group full">
                <label>Motivo Geral</label>
                <input
                  type="text"
                  name="motivo"
                  placeholder="Motivo adicional (compatibilidade, transporte, etc)"
                  value={formOrgao.motivo}
                  onChange={atualizarCampoFormulario}
                />
              </div>
            </div>

            <div className="form-row">
              <div className="form-group full">
                <label>Observações</label>
                <textarea
                  name="observacoes"
                  placeholder="Observações adicionais sobre o órgão"
                  value={formOrgao.observacoes}
                  onChange={atualizarCampoFormulario}
                  rows="3"
                />
              </div>
            </div>

            <div className="form-actions">
              <button type="submit" className="btn-salvar">
                {orgaoEditando ? 'Atualizar' : 'Registrar'}
              </button>
              <button type="button" className="btn-cancelar" onClick={limparFormulario}>
                Limpar
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Lista de Órgãos */}
      {carregando ? (
        <div className="carregando">Carregando órgãos doados...</div>
      ) : orgaos.length === 0 ? (
        <div className="sem-dados">Nenhum órgão doado registrado</div>
      ) : (
        <div className="orgao-lista">
          <table className="orgao-tabela">
            <thead>
              <tr>
                <th>Órgão/Tecido</th>
                <th>Status</th>
                <th>Data de Criação</th>
                <th>Hospital Receptor</th>
                <th>Paciente Receptor</th>
                <th>Ações</th>
              </tr>
            </thead>
            <tbody>
              {orgaos.map(orgao => (
                <tr key={orgao.id}>
                  <td><strong>{orgao.nomeOrgao}</strong></td>
                  <td>{getStatusBadge(orgao.status)}</td>
                  <td>{new Date(orgao.dataCriacao).toLocaleDateString('pt-BR')}</td>
                  <td>{orgao.hospitalReceptor || '-'}</td>
                  <td>{orgao.pacienteReceptor || '-'}</td>
                  <td className="acoes">
                    {orgao.status === 'AGUARDANDO_IMPLANTACAO' && (
                      <>
                        <button
                          className="btn-small btn-success"
                          onClick={() => abrirModalImplantacao(orgao)}
                          title="Registrar implantação"
                        >
                          ✓ Implantar
                        </button>
                        <button
                          className="btn-small btn-danger"
                          onClick={() => abrirModalDescarte(orgao)}
                          title="Registrar descarte"
                        >
                          ✗ Descartar
                        </button>
                      </>
                    )}
                    <button
                      className="btn-small btn-primary"
                      onClick={() => editarOrgao(orgao)}
                      title="Editar órgão"
                    >
                      ✎ Editar
                    </button>
                    <button
                      className="btn-small btn-secondary"
                      onClick={() => deletarOrgao(orgao.id)}
                      title="Deletar órgão"
                    >
                      🗑 Deletar
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>

          {/* Detalhes expandidos */}
          <div className="orgao-detalhes">
            {orgaos.map(orgao => (
              <details key={`details-${orgao.id}`} className="orgao-detail-item">
                <summary className="detail-summary">
                  <strong>{orgao.nomeOrgao}</strong> - {getStatusBadge(orgao.status)}
                </summary>
                <div className="detail-content">
                  <div className="detail-row">
                    <span className="detail-label">Data de Criação:</span>
                    <span>{new Date(orgao.dataCriacao).toLocaleString('pt-BR')}</span>
                  </div>
                  {orgao.status === 'IMPLANTADO' && orgao.dataImplantacao && (
                    <div className="detail-row">
                      <span className="detail-label">Data de Implantação:</span>
                      <span>{new Date(orgao.dataImplantacao).toLocaleString('pt-BR')}</span>
                    </div>
                  )}
                  {orgao.status === 'DESCARTADO' && orgao.dataDescarte && (
                    <div className="detail-row">
                      <span className="detail-label">Data de Descarte:</span>
                      <span>{new Date(orgao.dataDescarte).toLocaleString('pt-BR')}</span>
                    </div>
                  )}
                  {orgao.motivoDescarte && (
                    <div className="detail-row">
                      <span className="detail-label">Motivo do Descarte:</span>
                      <span>{orgao.motivoDescarte}</span>
                    </div>
                  )}
                  {orgao.hospitalReceptor && (
                    <div className="detail-row">
                      <span className="detail-label">Hospital Receptor:</span>
                      <span>{orgao.hospitalReceptor}</span>
                    </div>
                  )}
                  {orgao.pacienteReceptor && (
                    <div className="detail-row">
                      <span className="detail-label">Paciente Receptor:</span>
                      <span>{orgao.pacienteReceptor}</span>
                    </div>
                  )}
                  {orgao.cpfReceptor && (
                    <div className="detail-row">
                      <span className="detail-label">CPF do Receptor:</span>
                      <span>{formatarCpf(orgao.cpfReceptor)}</span>
                    </div>
                  )}
                  {orgao.motivo && (
                    <div className="detail-row">
                      <span className="detail-label">Motivo Geral:</span>
                      <span>{orgao.motivo}</span>
                    </div>
                  )}
                  {orgao.observacoes && (
                    <div className="detail-row">
                      <span className="detail-label">Observações:</span>
                      <span>{orgao.observacoes}</span>
                    </div>
                  )}
                </div>
              </details>
            ))}
          </div>
        </div>
      )}

      {modalAcao.aberto && (
        <div className="orgao-acao-modal-overlay" onClick={fecharModalAcao}>
          <div className="orgao-acao-modal" onClick={(e) => e.stopPropagation()}>
            <h4>
              {modalAcao.tipo === 'IMPLANTAR' ? 'Registrar Implantação' : 'Registrar Descarte'}
            </h4>
            <p className="orgao-acao-modal-subtitle">
              Órgão: <strong>{modalAcao.nomeOrgao || 'N/A'}</strong>
            </p>

            {modalAcao.tipo === 'IMPLANTAR' ? (
              <div className="orgao-acao-modal-fields">
                <label htmlFor="modal-hospital-receptor">Hospital receptor</label>
                <input
                  id="modal-hospital-receptor"
                  type="text"
                  value={modalAcao.hospitalReceptor}
                  onChange={(e) => setModalAcao((prev) => ({ ...prev, hospitalReceptor: e.target.value }))}
                  placeholder="Nome do hospital"
                />

                <label htmlFor="modal-paciente-receptor">Paciente receptor</label>
                <input
                  id="modal-paciente-receptor"
                  type="text"
                  value={modalAcao.pacienteReceptor}
                  onChange={(e) => setModalAcao((prev) => ({ ...prev, pacienteReceptor: e.target.value }))}
                  placeholder="Nome do paciente"
                />
              </div>
            ) : (
              <div className="orgao-acao-modal-fields">
                <label htmlFor="modal-motivo-descarte">Motivo do descarte</label>
                <textarea
                  id="modal-motivo-descarte"
                  rows={3}
                  value={modalAcao.motivo}
                  onChange={(e) => setModalAcao((prev) => ({ ...prev, motivo: e.target.value }))}
                  placeholder="Descreva o motivo"
                />
              </div>
            )}

            <div className="orgao-acao-modal-actions">
              <button type="button" className="btn-cancelar" onClick={fecharModalAcao}>
                Cancelar
              </button>
              <button type="button" className="btn-salvar" onClick={confirmarModalAcao}>
                Confirmar
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default OrgaoDoadoManager;
