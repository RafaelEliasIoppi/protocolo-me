import { useEffect, useState } from 'react';
import hospitalService from '../services/hospitalService';
import pacienteService from '../services/pacienteService';
import '../styles/CentralPacienteEditForm.css';
import { getApiErrorMessage } from '../utils/apiError';
import { formatarTelefone } from '../utils/telefone';

const CentralPacienteEditForm = ({ pacienteId, onSave, onCancel }) => {
  const [formData, setFormData] = useState({
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
    status: ''
  });

  const [hospitais, setHospitais] = useState([]);
  const [carregando, setCarregando] = useState(false);
  const [mensagem, setMensagem] = useState({ tipo: '', texto: '' });

  const statusOpcoes = [
    'PRE_INTERNACAO',
    'INTERNADO',
    'EM_PROTOCOLO_ME',
    'APTO_TRANSPLANTE',
    'NAO_APTO',
    'RECUSADO',
    'EXODO'
  ];

  const generoOpcoes = ['MASCULINO', 'FEMININO', 'OUTRO'];

  useEffect(() => {
    carregarPacienteEHospitais();
  }, [pacienteId]);

  const carregarPacienteEHospitais = async () => {
    try {
      setCarregando(true);

      // Carregar hospitais
      const hospitaisDados = await hospitalService.listar();
      const listaHospitais = Array.isArray(hospitaisDados)
        ? hospitaisDados
        : hospitaisDados?.content || [];
      setHospitais(listaHospitais);

      // Carregar paciente se tiver ID
      if (pacienteId) {
        const paciente = await pacienteService.obter(pacienteId);

        setFormData({
          nome: paciente.nome || '',
          cpf: paciente.cpf || '',
          dataNascimento: paciente.dataNascimento || '',
          genero: paciente.genero || '',
          hospitalId: paciente.hospitalId || '',
          leito: paciente.leito || '',
          dataInternacao: paciente.dataInternacao || '',
          diagnosticoPrincipal: paciente.diagnosticoPrincipal || '',
          historicoMedico: paciente.historicoMedico || '',
          nomeResponsavel: paciente.nomeResponsavel || '',
          telefoneResponsavel: formatarTelefone(paciente.telefoneResponsavel),
          emailResponsavel: paciente.emailResponsavel || '',
          status: paciente.status || 'INTERNADO'
        });
      }
    } catch (error) {
      console.error('Erro ao carregar dados:', error);
      setMensagem({ tipo: 'erro', texto: 'Erro ao carregar dados do paciente' });
    } finally {
      setCarregando(false);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;

    if (name === 'cpf') {
      const cpfNumerico = value.replace(/\D/g, '').slice(0, 11);
      const cpfFormatado = cpfNumerico
        .replace(/(\d{3})(\d)/, '$1.$2')
        .replace(/(\d{3})(\d)/, '$1.$2')
        .replace(/(\d{3})(\d{1,2})$/, '$1-$2');

      setFormData({ ...formData, [name]: cpfFormatado });
      return;
    }

    if (name === 'telefoneResponsavel') {
      const telefoneNumerico = value.replace(/\D/g, '').slice(0, 11);
      const telefoneFormatado = formatarTelefone(telefoneNumerico);

      setFormData({ ...formData, [name]: telefoneFormatado });
      return;
    }

    setFormData({ ...formData, [name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!pacienteId) {
      setMensagem({ tipo: 'erro', texto: 'ID do paciente não fornecido' });
      return;
    }

    try {
      setCarregando(true);
      const dados = {
        ...formData,
        hospital: { id: parseInt(formData.hospitalId) }
      };

      await pacienteService.atualizar(pacienteId, dados);
      setMensagem({ tipo: 'sucesso', texto: 'Paciente atualizado com sucesso!' });

      if (onSave) {
        setTimeout(() => onSave(), 1000);
      }
    } catch (error) {
      console.error('Erro ao atualizar paciente:', error);
      const mensagemErro = getApiErrorMessage(error, 'Erro ao atualizar paciente');
      setMensagem({ tipo: 'erro', texto: mensagemErro });
    } finally {
      setCarregando(false);
    }
  };

  return (
    <div className="central-paciente-edit-form">
      <div className="modal-overlay" onClick={onCancel}></div>
      <div className="modal-content">
        <div className="modal-header">
          <h2>Editar Paciente</h2>
          <button className="close-btn" onClick={onCancel}>✕</button>
        </div>

        {mensagem.texto && (
          <div className={`mensagem ${mensagem.tipo}`}>
            {mensagem.texto}
          </div>
        )}

        <form onSubmit={handleSubmit} className="form-grid">
          <div className="form-group">
            <label>Nome *</label>
            <input
              type="text"
              name="nome"
              value={formData.nome}
              onChange={handleInputChange}
              required
              disabled={carregando}
            />
          </div>

          <div className="form-group">
            <label>CPF *</label>
            <input
              type="text"
              name="cpf"
              value={formData.cpf}
              onChange={handleInputChange}
              maxLength={14}
              required
              disabled={carregando}
            />
          </div>

          <div className="form-group">
            <label>Data de Nascimento</label>
            <input
              type="date"
              name="dataNascimento"
              value={formData.dataNascimento}
              onChange={handleInputChange}
              disabled={carregando}
            />
          </div>

          <div className="form-group">
            <label>Gênero</label>
            <select
              name="genero"
              value={formData.genero}
              onChange={handleInputChange}
              disabled={carregando}
            >
              <option value="">Selecione...</option>
              {generoOpcoes.map(g => (
                <option key={g} value={g}>{g}</option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <label>Hospital *</label>
            <select
              name="hospitalId"
              value={formData.hospitalId}
              onChange={handleInputChange}
              required
              disabled={carregando}
            >
              <option value="">Selecione um hospital...</option>
              {hospitais.map(h => (
                <option key={h.id} value={h.id}>{h.nome}</option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <label>Leito</label>
            <input
              type="text"
              name="leito"
              value={formData.leito}
              onChange={handleInputChange}
              disabled={carregando}
            />
          </div>

          <div className="form-group">
            <label>Data de Internação</label>
            <input
              type="date"
              name="dataInternacao"
              value={formData.dataInternacao}
              onChange={handleInputChange}
              disabled={carregando}
            />
          </div>

          <div className="form-group full-width">
            <label>Diagnóstico Principal</label>
            <textarea
              name="diagnosticoPrincipal"
              value={formData.diagnosticoPrincipal}
              onChange={handleInputChange}
              rows="3"
              disabled={carregando}
            />
          </div>

          <div className="form-group full-width">
            <label>Histórico Médico</label>
            <textarea
              name="historicoMedico"
              value={formData.historicoMedico}
              onChange={handleInputChange}
              rows="3"
              disabled={carregando}
            />
          </div>

          <div className="form-group">
            <label>Nome Responsável</label>
            <input
              type="text"
              name="nomeResponsavel"
              value={formData.nomeResponsavel}
              onChange={handleInputChange}
              disabled={carregando}
            />
          </div>

          <div className="form-group">
            <label>Telefone Responsável</label>
            <input
              type="tel"
              name="telefoneResponsavel"
              value={formData.telefoneResponsavel}
              onChange={handleInputChange}
              maxLength={15}
              disabled={carregando}
            />
          </div>

          <div className="form-group">
            <label>Email Responsável</label>
            <input
              type="email"
              name="emailResponsavel"
              value={formData.emailResponsavel}
              onChange={handleInputChange}
              disabled={carregando}
            />
          </div>

          <div className="form-group">
            <label>Status *</label>
            <select
              name="status"
              value={formData.status}
              onChange={handleInputChange}
              required
              disabled={carregando}
            >
              <option value="">Selecione...</option>
              {statusOpcoes.map(s => (
                <option key={s} value={s}>{s}</option>
              ))}
            </select>
          </div>

          <div className="form-actions">
            <button
              type="button"
              onClick={onCancel}
              disabled={carregando}
              className="btn-cancel"
            >
              Cancelar
            </button>
            <button
              type="submit"
              disabled={carregando}
              className="btn-save"
            >
              {carregando ? 'Salvando...' : 'Salvar Alterações'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default CentralPacienteEditForm;
