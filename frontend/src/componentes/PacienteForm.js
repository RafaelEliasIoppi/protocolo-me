import { useEffect, useState } from 'react';
import hospitalService from '../services/hospitalService';
import pacienteService from '../services/pacienteService';
import '../styles/PacienteForm.css';
import { getApiErrorMessage } from '../utils/apiError';
import { formatarCpf } from '../utils/cpf';
import { formatarTelefone } from '../utils/telefone';

const formDataPadrao = {
  nome: '',
  cpf: '',
  dataNascimento: '',
  genero: '',
  hospitalId: '',
  leito: '',
  dataInternacao: '',
  diagnosticoPrincipal: '',
  historicoMedico: '',
  nomeResponsavel: '',
  telefoneResponsavel: '',
  emailResponsavel: '',
  statusEntrevistaFamiliar: '',
  observacoesEntrevistaFamiliar: '',
  dataEntrevistaFamiliar: '',
  status: 'INTERNADO'
};

const PacienteForm = ({
  paciente,
  onSave,
  onCancel,
  onEditarPaciente,
  ocultarResumo = false,
  somenteListagem = false,
  somenteFormulario = false,
}) => {
  const [formData, setFormData] = useState(formDataPadrao);
  const [hospitais, setHospitais] = useState([]);
  const [pacientes, setPacientes] = useState([]);
  const [editandoId, setEditandoId] = useState(null);
  const [filtroStatus, setFiltroStatus] = useState('');
  const [filtroHospital, setFiltroHospital] = useState('');
  const [busca, setBusca] = useState('');
  const [mensagem, setMensagem] = useState({ tipo: '', texto: '' });
  const [erroHospitais, setErroHospitais] = useState('');
  const [carregando, setCarregando] = useState(false);
  const [estatisticas, setEstatisticas] = useState(null);

  const statusOpcoes = [
    'PRE_INTERNACAO',
    'INTERNADO',
    'EM_PROTOCOLO_ME',
    'APTO_TRANSPLANTE',
    'NAO_APTO',
    'RECUSADO',
    'EXODO'
  ];

  const statusOpcoesManuais = statusOpcoes.filter((s) => s !== 'EM_PROTOCOLO_ME');
  const generoOpcoes = ['MASCULINO', 'FEMININO', 'OUTRO'];

  const normalizarLista = (dados) => {
    if (Array.isArray(dados)) return dados;
    if (Array.isArray(dados?.content)) return dados.content;
    if (Array.isArray(dados?.data)) return dados.data;
    return [];
  };

  const valorOuNull = (valor) => {
    if (valor == null) return null;
    const texto = String(valor).trim();
    return texto ? texto : null;
  };

  // Carregar hospitais, pacientes e estatísticas ao montar
  useEffect(() => {
    let ativo = true;

    const carregarTudo = async () => {
      // Hospitais
      try {
        const dados = await hospitalService.listar();
        if (!ativo) return;
        setErroHospitais('');
        setHospitais(normalizarLista(dados));
      } catch (error) {
        if (!ativo) return;
        console.error('[PacienteForm] Erro ao carregar hospitais:', error);
        setHospitais([]);
        setErroHospitais('Não foi possível carregar os hospitais cadastrados. Verifique permissões ou conexão com o servidor.');
      }

      // Pacientes
      try {
        setCarregando(true);
        const dados = await pacienteService.listar();
        if (!ativo) return;
        const lista = normalizarLista(dados);
        setPacientes(lista);
        setMensagem({ tipo: 'sucesso', texto: `${lista.length} pacientes encontrados` });
      } catch (error) {
        if (!ativo) return;
        console.error('Erro ao carregar pacientes:', error);
        setPacientes([]);
        setMensagem({ tipo: 'erro', texto: 'Erro ao carregar pacientes' });
      } finally {
        if (ativo) setCarregando(false);
      }

      // Estatísticas
      try {
        const dados = await pacienteService.obterEstatisticas();
        if (!ativo) return;
        setEstatisticas(dados);
      } catch (error) {
        if (!ativo) return;
        console.error('Erro ao carregar estatísticas:', error);
      }
    };

    carregarTudo();

    return () => {
      ativo = false;
    };
  }, []);

  // Se veio um paciente como prop, preencher formulário
  useEffect(() => {
    if (paciente) {
      setFormData({
        nome: paciente.nome || '',
        cpf: paciente.cpf || '',
        dataNascimento: paciente.dataNascimento || '',
        genero: paciente.genero || '',
        hospitalId: paciente.hospital?.id || '',
        leito: paciente.leito || '',
        dataInternacao: paciente.dataInternacao || '',
        diagnosticoPrincipal: paciente.diagnosticoPrincipal || '',
        historicoMedico: paciente.historicoMedico || '',
        nomeResponsavel: paciente.nomeResponsavel || '',
        telefoneResponsavel: formatarTelefone(paciente.telefoneResponsavel),
        emailResponsavel: paciente.emailResponsavel || '',
        statusEntrevistaFamiliar: paciente.statusEntrevistaFamiliar || '',
        observacoesEntrevistaFamiliar: paciente.observacoesEntrevistaFamiliar || '',
        dataEntrevistaFamiliar: paciente.dataEntrevistaFamiliar || '',
        status: paciente.status || 'INTERNADO'
      });
      setEditandoId(paciente.id);
      return;
    }

    setFormData(formDataPadrao);
    setEditandoId(null);
  }, [paciente]);

  const carregarPacientes = async () => {
    try {
      setCarregando(true);

      let dados;
      if (busca) {
        dados = await pacienteService.buscarPorNome(busca);
      } else if (filtroStatus && filtroHospital) {
        dados = await pacienteService.listarPorHospitalEStatus(filtroHospital, filtroStatus);
      } else if (filtroStatus) {
        dados = await pacienteService.listarPorStatus(filtroStatus);
      } else if (filtroHospital) {
        dados = await pacienteService.listarPorHospital(filtroHospital);
      } else {
        dados = await pacienteService.listar();
      }

      const lista = normalizarLista(dados);
      setPacientes(lista);
      setMensagem({ tipo: 'sucesso', texto: `${lista.length} pacientes encontrados` });
    } catch (error) {
      console.error('Erro ao carregar pacientes:', error);
      setPacientes([]);
      setMensagem({ tipo: 'erro', texto: 'Erro ao carregar pacientes' });
    } finally {
      setCarregando(false);
    }
  };

  const carregarEstatisticas = async () => {
    try {
      const dados = await pacienteService.obterEstatisticas();
      setEstatisticas(dados);
    } catch (error) {
      console.error('Erro ao carregar estatísticas:', error);
    }
  };

  const atualizarCampoFormulario = (e) => {
    const { name, value } = e.target;

    if (name === 'telefoneResponsavel') {
      const telefoneNumerico = value.replace(/\D/g, '').slice(0, 11);
      setFormData({ ...formData, [name]: formatarTelefone(telefoneNumerico) });
      return;
    }

    if (name === 'cpf') {
      const cpfNumerico = value.replace(/\D/g, '').slice(0, 11);
      const cpfFormatado = cpfNumerico
        .replace(/(\d{3})(\d)/, '$1.$2')
        .replace(/(\d{3})(\d)/, '$1.$2')
        .replace(/(\d{3})(\d{1,2})$/, '$1-$2');
      setFormData({ ...formData, [name]: cpfFormatado });
      return;
    }

    setFormData({ ...formData, [name]: value });
  };

  const salvarPaciente = async (e) => {
    e.preventDefault();
    try {
      setCarregando(true);

      const dadosPaciente = {
        ...formData,
        cpf: formData.cpf.replace(/\D/g, ''),
        dataNascimento: valorOuNull(formData.dataNascimento),
        dataInternacao: valorOuNull(formData.dataInternacao),
        dataEntrevistaFamiliar: valorOuNull(formData.dataEntrevistaFamiliar),
        status: editandoId ? formData.status : 'INTERNADO',
        hospital: { id: parseInt(formData.hospitalId) }
      };

      if (editandoId) {
        await pacienteService.atualizar(editandoId, dadosPaciente);
        setMensagem({ tipo: 'sucesso', texto: 'Paciente atualizado com sucesso!' });
        if (onSave) onSave(dadosPaciente);
      } else {
        const response = await pacienteService.criar(dadosPaciente);
        setMensagem({ tipo: 'sucesso', texto: 'Paciente criado com sucesso!' });
        if (onSave) onSave(response);
      }

      limparFormulario();
      carregarPacientes();
      carregarEstatisticas();
    } catch (error) {
      console.error('Erro ao salvar paciente:', error);
      setMensagem({ tipo: 'erro', texto: getApiErrorMessage(error, 'Erro ao salvar paciente') });
    } finally {
      setCarregando(false);
    }
  };

  const editarPaciente = (pacienteItem) => {
    setFormData({
      nome: pacienteItem.nome,
      cpf: pacienteItem.cpf,
      dataNascimento: pacienteItem.dataNascimento,
      genero: pacienteItem.genero,
      hospitalId: pacienteItem.hospital?.id || '',
      leito: pacienteItem.leito || '',
      dataInternacao: pacienteItem.dataInternacao || '',
      diagnosticoPrincipal: pacienteItem.diagnosticoPrincipal || '',
      historicoMedico: pacienteItem.historicoMedico || '',
      nomeResponsavel: pacienteItem.nomeResponsavel || '',
      telefoneResponsavel: formatarTelefone(pacienteItem.telefoneResponsavel),
      emailResponsavel: pacienteItem.emailResponsavel || '',
      statusEntrevistaFamiliar: pacienteItem.statusEntrevistaFamiliar || '',
      observacoesEntrevistaFamiliar: pacienteItem.observacoesEntrevistaFamiliar || '',
      dataEntrevistaFamiliar: pacienteItem.dataEntrevistaFamiliar || '',
      status: pacienteItem.status
    });
    setEditandoId(pacienteItem.id);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const deletarPaciente = async (id) => {
    if (window.confirm('Tem certeza que deseja deletar este paciente?')) {
      try {
        await pacienteService.deletar(id);
        setMensagem({ tipo: 'sucesso', texto: 'Paciente deletado com sucesso!' });
        carregarPacientes();
        carregarEstatisticas();
      } catch (error) {
        console.error('Erro ao deletar paciente:', error);
        setMensagem({ tipo: 'erro', texto: 'Erro ao deletar paciente' });
      }
    }
  };

  const limparFormulario = () => {
    setFormData(formDataPadrao);
    setEditandoId(null);
    if (onCancel) onCancel();
  };

  const formatarData = (data) => {
    if (!data) return '-';
    return new Date(data).toLocaleDateString('pt-BR');
  };

  const formatarStatus = (status) => {
    const statusMap = {
      'PRE_INTERNACAO': 'Pré-internalização',
      'INTERNADO': 'Internado',
      'EM_PROTOCOLO_ME': 'Em Protocolo ME',
      'APTO_TRANSPLANTE': 'Apto para Transplante',
      'NAO_APTO': 'Não Apto',
      'RECUSADO': 'Recusado',
      'EXODO': 'Óbito'
    };
    return statusMap[status] || status;
  };

  const formatarStatusEntrevista = (status) => {
    const statusMap = {
      'NAO_INICIADA': 'Não iniciada',
      'EM_ANDAMENTO': 'Em andamento',
      'AUTORIZADA': 'Autorizada',
      'RECUSADA': 'Recusada'
    };
    return statusMap[status] || status || 'Não iniciada';
  };

  return (
    <div className="paciente-container">
      {!paciente && !ocultarResumo && <h1>Gestão de Pacientes</h1>}

      {/* Estatísticas */}
      {!paciente && !ocultarResumo && estatisticas && (
        <div className="estatisticas-grid">
          <div className="stat-card">
            <div className="stat-valor">{estatisticas.totalPacientes}</div>
            <div className="stat-label">Total de Pacientes</div>
          </div>
          <div className="stat-card stat-internados">
            <div className="stat-valor">{estatisticas.pacientesInternados}</div>
            <div className="stat-label">Internados</div>
          </div>
          <div className="stat-card stat-protocolo">
            <div className="stat-valor">{estatisticas.pacientesEmProtocoloME}</div>
            <div className="stat-label">Em Protocolo ME</div>
          </div>
          <div className="stat-card stat-apto">
            <div className="stat-valor">{estatisticas.pacientesAptosTransplante}</div>
            <div className="stat-label">Aptos Transplante</div>
          </div>
          <div className="stat-card stat-nao-apto">
            <div className="stat-valor">{estatisticas.pacientesNaoAptos}</div>
            <div className="stat-label">Não Aptos</div>
          </div>
        </div>
      )}

      {/* Mensagens */}
      {mensagem.texto && (
        <div className={`mensagem ${mensagem.tipo}`}>
          {mensagem.texto}
        </div>
      )}

      {/* Formulário */}
      {!somenteListagem && (
        <form onSubmit={salvarPaciente} className="paciente-form">
          <h2>{editandoId ? 'Editar Paciente' : 'Novo Paciente'}</h2>

          <div className="form-row">
            <div className="form-group">
              <label>Nome *</label>
              <input
                type="text"
                name="nome"
                value={formData.nome}
                onChange={atualizarCampoFormulario}
                required
                placeholder="Nome completo"
              />
            </div>
            <div className="form-group">
              <label>CPF *</label>
              <input
                type="text"
                name="cpf"
                value={formData.cpf}
                onChange={atualizarCampoFormulario}
                maxLength={14}
                required
                placeholder="XXX.XXX.XXX-XX"
                disabled={editandoId !== null}
              />
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label>Data de Nascimento *</label>
              <input
                type="date"
                name="dataNascimento"
                value={formData.dataNascimento}
                onChange={atualizarCampoFormulario}
                required
              />
            </div>
            <div className="form-group">
              <label>Gênero *</label>
              <select
                name="genero"
                value={formData.genero}
                onChange={atualizarCampoFormulario}
                required
              >
                <option value="">Selecione...</option>
                {generoOpcoes.map(g => (
                  <option key={g} value={g}>{g}</option>
                ))}
              </select>
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label>Hospital *</label>
              <select
                name="hospitalId"
                value={formData.hospitalId}
                onChange={atualizarCampoFormulario}
                required
              >
                <option value="">Selecione um hospital...</option>
                {hospitais.map(h => (
                  <option key={h.id} value={h.id}>{h.nome || h.nomeHospital || 'Hospital sem nome'}</option>
                ))}
              </select>
              {erroHospitais && <small className="erro-hospitais">{erroHospitais}</small>}
            </div>
            <div className="form-group">
              <label>Leito</label>
              <input
                type="text"
                name="leito"
                value={formData.leito}
                onChange={atualizarCampoFormulario}
                placeholder="Ex: UTI 205"
              />
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label>Data de Internação</label>
              <input
                type="date"
                name="dataInternacao"
                value={formData.dataInternacao}
                onChange={atualizarCampoFormulario}
              />
            </div>
            <div className="form-group">
              {editandoId ? (
                <>
                  <label>Status</label>
                  <select
                    name="status"
                    value={formData.status}
                    onChange={atualizarCampoFormulario}
                    disabled={formData.status === 'EM_PROTOCOLO_ME'}
                  >
                    {statusOpcoesManuais.map(s => (
                      <option key={s} value={s}>{formatarStatus(s)}</option>
                    ))}
                  </select>
                  {formData.status === 'EM_PROTOCOLO_ME' && (
                    <small>Paciente em protocolo ME: status controlado automaticamente pelo protocolo.</small>
                  )}
                </>
              ) : (
                <>
                  <label>Status</label>
                  <input type="text" value="Internado" disabled />
                  <small>O status Em Protocolo ME é definido automaticamente ao iniciar o protocolo.</small>
                </>
              )}
            </div>
          </div>

          <div className="form-group">
            <label>Entrevista Familiar</label>
            <input
              type="text"
              value={formatarStatusEntrevista(formData.statusEntrevistaFamiliar)}
              disabled
            />
            <small>O resultado da entrevista é sincronizado automaticamente a partir do protocolo.</small>
            {formData.dataEntrevistaFamiliar && (
              <small>Última atualização: {formatarData(formData.dataEntrevistaFamiliar)}</small>
            )}
            {formData.observacoesEntrevistaFamiliar && (
              <textarea value={formData.observacoesEntrevistaFamiliar} disabled rows="3" />
            )}
          </div>

          <div className="form-group">
            <label>Diagnóstico Principal</label>
            <textarea
              name="diagnosticoPrincipal"
              value={formData.diagnosticoPrincipal}
              onChange={atualizarCampoFormulario}
              placeholder="Descreva o diagnóstico principal..."
              rows="3"
            />
          </div>

          <div className="form-group">
            <label>Histórico Médico</label>
            <textarea
              name="historicoMedico"
              value={formData.historicoMedico}
              onChange={atualizarCampoFormulario}
              placeholder="Descreva o histórico médico..."
              rows="3"
            />
          </div>

          <h3>Responsável</h3>
          <div className="form-row">
            <div className="form-group">
              <label>Nome do Responsável</label>
              <input
                type="text"
                name="nomeResponsavel"
                value={formData.nomeResponsavel}
                onChange={atualizarCampoFormulario}
                placeholder="Nome"
              />
            </div>
            <div className="form-group">
              <label>Telefone</label>
              <input
                type="tel"
                name="telefoneResponsavel"
                value={formData.telefoneResponsavel}
                onChange={atualizarCampoFormulario}
                maxLength={15}
                placeholder="(XX) XXXXX-XXXX"
              />
            </div>
            <div className="form-group">
              <label>Email</label>
              <input
                type="email"
                name="emailResponsavel"
                value={formData.emailResponsavel}
                onChange={atualizarCampoFormulario}
                placeholder="email@example.com"
              />
            </div>
          </div>

          <div className="form-actions">
            <button type="submit" className="btn-salvar" disabled={carregando}>
              {carregando ? 'Salvando...' : (editandoId ? 'Atualizar' : 'Criar')} Paciente
            </button>
            {editandoId ? (
              <button type="button" className="btn-cancelar" onClick={limparFormulario}>
                Cancelar
              </button>
            ) : onCancel ? (
              <button type="button" className="btn-cancelar" onClick={onCancel}>
                Voltar
              </button>
            ) : null}
          </div>
        </form>
      )}

      {/* Filtros e Listagem */}
      {!paciente && !somenteFormulario && (
        <>
          <div className="filtros-section">
            <h2>Filtros e Busca</h2>
            <div className="filtros">
              <div className="filtro">
                <label>Buscar por Nome:</label>
                <input
                  type="text"
                  value={busca}
                  onChange={(e) => setBusca(e.target.value)}
                  placeholder="Digite o nome..."
                />
              </div>
              <div className="filtro">
                <label>Filtrar por Status:</label>
                <select value={filtroStatus} onChange={(e) => setFiltroStatus(e.target.value)}>
                  <option value="">Todos</option>
                  {statusOpcoes.map(s => (
                    <option key={s} value={s}>{formatarStatus(s)}</option>
                  ))}
                </select>
              </div>
              <div className="filtro">
                <label>Filtrar por Hospital:</label>
                <select value={filtroHospital} onChange={(e) => setFiltroHospital(e.target.value)}>
                  <option value="">Todos</option>
                  {hospitais.map(h => (
                    <option key={h.id} value={h.id}>{h.nome || h.nomeHospital || 'Hospital sem nome'}</option>
                  ))}
                </select>
              </div>
              <button className="btn-filtrar" onClick={carregarPacientes} disabled={carregando}>
                Aplicar Filtros
              </button>
            </div>
          </div>

          <div className="pacientes-list">
            <h2>Pacientes ({pacientes.length})</h2>
            {carregando && <p className="carregando">Carregando...</p>}
            {!carregando && pacientes.length === 0 && <p className="sem-resultados">Nenhum paciente encontrado</p>}
            {!carregando && pacientes.length > 0 && (
              <div className="pacientes-grid">
                {pacientes.map(pItem => (
                  <div key={pItem.id} className={`paciente-card status-${(pItem.status || 'INTERNADO').toLowerCase().replace(/_/g, '-')}`}>
                    <div className="card-header">
                      <h3>{pItem.nome}</h3>
                      <span className={`status-badge status-${(pItem.status || 'INTERNADO').toLowerCase().replace(/_/g, '-')}`}>
                        {formatarStatus(pItem.status)}
                      </span>
                    </div>
                    <div className="card-body">
                      <p><strong>CPF:</strong> {formatarCpf(pItem.cpf)}</p>
                      <p><strong>Gênero:</strong> {pItem.genero}</p>
                      <p><strong>Data Nascimento:</strong> {formatarData(pItem.dataNascimento)}</p>
                      <p><strong>Hospital:</strong> {pItem.hospital?.nome || pItem.hospital?.nomeHospital || '-'}</p>
                      <p><strong>Leito:</strong> {pItem.leito || '-'}</p>
                      <p><strong>Data Internação:</strong> {formatarData(pItem.dataInternacao)}</p>
                      <p><strong>Diagnóstico:</strong> {pItem.diagnosticoPrincipal || '-'}</p>
                      <p><strong>Entrevista Familiar:</strong> {pItem.statusEntrevistaFamiliar || 'Não iniciada'}</p>
                      {pItem.nomeResponsavel && (
                        <p><strong>Responsável:</strong> {pItem.nomeResponsavel}</p>
                      )}
                    </div>
                    <div className="card-actions">
                      <button
                        className="btn-editar"
                        onClick={() => {
                          if (somenteListagem && typeof onEditarPaciente === 'function') {
                            onEditarPaciente(pItem);
                            return;
                          }
                          editarPaciente(pItem);
                        }}
                      >
                        Editar
                      </button>
                      <span className="status-badge-listagem">
                        {formatarStatus(pItem.status)}
                      </span>
                      <button className="btn-deletar" onClick={() => deletarPaciente(pItem.id)}>Deletar</button>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </>
      )}
    </div>
  );
};

export default PacienteForm;
