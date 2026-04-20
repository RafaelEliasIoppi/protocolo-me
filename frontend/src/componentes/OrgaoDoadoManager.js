import React, { useState, useEffect } from 'react';
import apiClient from '../services/apiClient';
import '../styles/OrgaoDoadoManager.css';

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

  const handleChangeForm = (e) => {
    const { name, value } = e.target;
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

  const handleCriarOuAtualizarOrgao = async (e) => {
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

  const handleEditar = (orgao) => {
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

  const handleDeletar = async (id) => {
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

  const handleRegistrarImplantacao = async (id) => {
    const hospitalReceptor = prompt('Hospital receptor:');
    if (!hospitalReceptor) return;

    const pacienteReceptor = prompt('Nome do paciente receptor:');
    if (!pacienteReceptor) return;

    try {
      await apiClient.post(`/api/orgaos-doados/${id}/implantar?hospitalReceptor=${hospitalReceptor}&pacienteReceptor=${pacienteReceptor}`);
      setSucesso('Implantação registrada com sucesso');
      carregarOrgaos();
      carregarEstatisticas();
    } catch (err) {
      setErro('Erro ao registrar implantação');
      console.error(err);
    }
  };

  const handleRegistrarDescarte = async (id) => {
    const motivo = prompt('Motivo do descarte:');
    if (!motivo) return;

    try {
      await apiClient.post(`/api/orgaos-doados/${id}/descartar?motivo=${encodeURIComponent(motivo)}`);
      setSucesso('Descarte registrado com sucesso');
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
          <form onSubmit={handleCriarOuAtualizarOrgao} className="orgao-form">
            <div className="form-row">
              <div className="form-group">
                <label>Órgão/Tecido *</label>
                <select
                  name="nomeOrgao"
                  value={formOrgao.nomeOrgao}
                  onChange={handleChangeForm}
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
                  onChange={handleChangeForm}
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
                    onChange={handleChangeForm}
                  />
                </div>

                <div className="form-group">
                  <label>Paciente Receptor</label>
                  <input
                    type="text"
                    name="pacienteReceptor"
                    placeholder="Nome do paciente receptor"
                    value={formOrgao.pacienteReceptor}
                    onChange={handleChangeForm}
                  />
                </div>

                <div className="form-group">
                  <label>CPF Receptor</label>
                  <input
                    type="text"
                    name="cpfReceptor"
                    placeholder="CPF do receptor"
                    value={formOrgao.cpfReceptor}
                    onChange={handleChangeForm}
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
                    onChange={handleChangeForm}
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
                      onChange={handleChangeForm}
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
                  onChange={handleChangeForm}
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
                  onChange={handleChangeForm}
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
                          onClick={() => handleRegistrarImplantacao(orgao.id)}
                          title="Registrar implantação"
                        >
                          ✓ Implantar
                        </button>
                        <button
                          className="btn-small btn-danger"
                          onClick={() => handleRegistrarDescarte(orgao.id)}
                          title="Registrar descarte"
                        >
                          ✗ Descartar
                        </button>
                      </>
                    )}
                    <button
                      className="btn-small btn-primary"
                      onClick={() => handleEditar(orgao)}
                      title="Editar órgão"
                    >
                      ✎ Editar
                    </button>
                    <button
                      className="btn-small btn-secondary"
                      onClick={() => handleDeletar(orgao.id)}
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
                      <span>{orgao.cpfReceptor}</span>
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
    </div>
  );
};

export default OrgaoDoadoManager;
