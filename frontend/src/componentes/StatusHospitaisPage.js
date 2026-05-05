import { useEffect, useState } from 'react';
import hospitalService from '../services/hospitalService';
import '../styles/HospitalStatus.css';
import { formatarTelefone } from '../utils/telefone';

const StatusHospitaisPage = () => {
  const [hospitais, setHospitais] = useState([]);
  const [carregando, setCarregando] = useState(false);
  const [erro, setErro] = useState('');
  const [sucesso, setSucesso] = useState('');
  const [filtroStatus, setFiltroStatus] = useState('');

  const statusOpcoes = [
    { valor: 'ATIVO', label: 'Ativo', cor: 'verde' },
    { valor: 'INATIVO', label: 'Inativo', cor: 'cinza' },
    { valor: 'MANUTENCAO', label: 'Manutenção', cor: 'amarelo' },
    { valor: 'SUSPENSAO', label: 'Suspensão', cor: 'vermelho' }
  ];

  useEffect(() => {
    carregarHospitais();
  }, []);

  const carregarHospitais = async () => {
    setCarregando(true);
    setErro('');
    try {
      const dados = await hospitalService.listar();
      setHospitais(Array.isArray(dados) ? dados : []);
    } catch (err) {
      setErro('Erro ao carregar hospitais');
    } finally {
      setCarregando(false);
    }
  };

  const alterarStatus = async (hospitalId, novoStatus) => {
    setSucesso('');
    setErro('');

    try {
      const atualizado = await hospitalService.atualizarStatus(hospitalId, novoStatus);

      // Atualizar lista local
      setHospitais(hospitais.map(h =>
        h.id === hospitalId ? atualizado : h
      ));

      setSucesso('Status atualizado com sucesso!');
      setTimeout(() => setSucesso(''), 3000);
    } catch (err) {
      setErro('Erro ao atualizar status do hospital');
    }
  };

  const obterCorStatus = (status) => {
    const opcao = statusOpcoes.find(s => s.valor === status);
    return opcao?.cor || 'cinza';
  };

  const obterLabelStatus = (status) => {
    const opcao = statusOpcoes.find(s => s.valor === status);
    return opcao?.label || status;
  };

  const hospitaisFiltrados = filtroStatus
    ? hospitais.filter(h => h.status === filtroStatus)
    : hospitais;

  return (
    <div className="hospital-status-container">
      <h2>Gerenciar Status dos Hospitais</h2>
      <p className="subtitle">Equipe Médica - Controle de Status</p>

      {erro && <div className="alerta alerta-erro">{erro}</div>}
      {sucesso && <div className="alerta alerta-sucesso">{sucesso}</div>}

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

        <button onClick={carregarHospitais} className="btn-recarregar">
          🔄 Atualizar
        </button>
      </div>

      {carregando && <div className="carregando">Carregando...</div>}

      <div className="hospitais-grid">
        {hospitaisFiltrados.length > 0 ? (
          hospitaisFiltrados.map(hospital => (
            <div key={hospital.id} className="hospital-card">
              <div className="hospital-header">
                <h3>{hospital.nome}</h3>
                <span className={`status-badge status-${obterCorStatus(hospital.status)}`}>
                  {obterLabelStatus(hospital.status)}
                </span>
              </div>

              <div className="hospital-info">
                <p><strong>CNPJ:</strong> {hospital.cnpj}</p>
                <p><strong>Localização:</strong> {hospital.cidade}, {hospital.estado}</p>
                <p><strong>Responsável Médico:</strong> {hospital.responsavelMedico || 'Não informado'}</p>
                {hospital.email && <p><strong>Email:</strong> {hospital.email}</p>}
                {hospital.telefone && <p><strong>Telefone:</strong> {formatarTelefone(hospital.telefone)}</p>}
              </div>

              <div className="hospital-actions">
                <p className="campo-obrigatorio">Alterar Status:</p>
                <div className="actions-buttons">
                  {statusOpcoes.map(opcao => (
                    <button
                      key={opcao.valor}
                      className={`btn-status btn-status-${opcao.cor} ${hospital.status === opcao.valor ? 'ativo' : ''}`}
                      onClick={() => alterarStatus(hospital.id, opcao.valor)}
                      disabled={hospital.status === opcao.valor}
                    >
                      {opcao.label}
                    </button>
                  ))}
                </div>
              </div>
            </div>
          ))
        ) : (
          <div className="vazio">
            <p>Nenhum hospital encontrado</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default StatusHospitaisPage;
