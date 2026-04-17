import React, { useState, useEffect } from 'react';
import apiClient from '../services/apiClient';
import GerenciadorAnexos from './GerenciadorAnexos';
import '../styles/ExameMEManager.css';

const ExameMEManager = ({ protocoloId, onAtualizacao }) => {
  const [exames, setExames] = useState([]);
  const [examesFiltrados, setExamesFiltrados] = useState([]);
  const [resumoExames, setResumoExames] = useState(null);
  const [novoExame, setNovoExame] = useState({
    tipoExame: '',
    descricao: '',
    resultado: '',
    resultado_positivo: null,
    responsavel: '',
    observacoes: ''
  });

  const [filtroCategoria, setFiltroCategoria] = useState('');
  const [carregando, setCarregando] = useState(false);
  const [erro, setErro] = useState('');
  const [sucesso, setSucesso] = useState('');
  const [exameSelecionado, setExameSelecionado] = useState(null);

  const tiposExame = [
    // Exames Clínicos
    { valor: 'RESPOSTA_ESTIMULO_DORO', label: 'Resposta ao Estímulo Doloroso', categoria: 'CLINICO' },
    { valor: 'REFLEXO_PUPILAR', label: 'Reflexo Pupilar', categoria: 'CLINICO' },
    { valor: 'REFLEXO_CORNEAL', label: 'Reflexo Corneal', categoria: 'CLINICO' },
    { valor: 'REFLEXO_VESTIBULO_OCULAR', label: 'Reflexo Vestibulo-Ocular (Calórico)', categoria: 'CLINICO' },
    { valor: 'REFLEXO_NAUSEOSO', label: 'Reflexo Nauseoso/Faríngeo', categoria: 'CLINICO' },
    { valor: 'REFLEXO_TOSSE', label: 'Reflexo de Tosse', categoria: 'CLINICO' },
    { valor: 'APNEIA_TEST', label: 'Teste de Apneia', categoria: 'CLINICO' },
    { valor: 'POSTURA_DECEREBRADO', label: 'Postura Decerebrada', categoria: 'CLINICO' },
    { valor: 'POSTURA_DESCEREBRADO', label: 'Postura Descerebrado', categoria: 'CLINICO' },

    // Exames Complementares
    { valor: 'ANGIOGRAFIA_CEREBRAL', label: 'Angiografia Cerebral Digital', categoria: 'COMPLEMENTAR' },
    { valor: 'RESSONANCIA_MAGNETICA', label: 'Ressonância Magnética', categoria: 'COMPLEMENTAR' },
    { valor: 'TOMOGRAFIA_CRANIO', label: 'Tomografia de Crânio', categoria: 'COMPLEMENTAR' },
    { valor: 'TOMOGRAFIA_ANGIO', label: 'Tomografia Angio', categoria: 'COMPLEMENTAR' },
    { valor: 'ULTRASSOM_DOPPLER', label: 'Ultrassom Doppler Transcraniano', categoria: 'COMPLEMENTAR' },
    { valor: 'ELETROENCEFALOGRAMA', label: 'Eletroencefalograma (EEG)', categoria: 'COMPLEMENTAR' },
    { valor: 'MAPEAMENTO_CEREBRAL', label: 'Mapeamento Cerebral', categoria: 'COMPLEMENTAR' },
    { valor: 'RESSONANCIA_MAGNETICA_FUNCIONAL', label: 'Ressonância Magnética Funcional', categoria: 'COMPLEMENTAR' },

    // Exames Laboratoriais
    { valor: 'GASOMETRIA_ARTERIAL', label: 'Gasometria Arterial', categoria: 'LABORATORIAL' },
    { valor: 'HEMOGRAMA', label: 'Hemograma Completo', categoria: 'LABORATORIAL' },
    { valor: 'ELETRÓLITOS', label: 'Eletrólitos (Na, K, Cl)', categoria: 'LABORATORIAL' },
    { valor: 'GLICEMIA', label: 'Glicemia', categoria: 'LABORATORIAL' },
    { valor: 'CALCIO', label: 'Cálcio Iônico', categoria: 'LABORATORIAL' },
    { valor: 'FUNCAO_HEPATICA', label: 'Função Hepática (AST, ALT, Bilirrubina)', categoria: 'LABORATORIAL' },
    { valor: 'FUNCAO_RENAL', label: 'Função Renal (Creatinina, Uréia)', categoria: 'LABORATORIAL' },
    { valor: 'COAGULACAO', label: 'Testes de Coagulação (PT, APPT)', categoria: 'LABORATORIAL' },
    { valor: 'PROTEINAS_TOTAIS', label: 'Proteínas Totais', categoria: 'LABORATORIAL' },
    { valor: 'SOROLOGIA_HIV', label: 'Sorologia HIV', categoria: 'LABORATORIAL' },
    { valor: 'SOROLOGIA_HEPATITE_B', label: 'Sorologia Hepatite B', categoria: 'LABORATORIAL' },
    { valor: 'SOROLOGIA_HEPATITE_C', label: 'Sorologia Hepatite C', categoria: 'LABORATORIAL' },
    { valor: 'SOROLOGIA_SIFILIS', label: 'Sorologia Sífilis (RPR/VDRL)', categoria: 'LABORATORIAL' },
    { valor: 'CULTURA_SANGUE', label: 'Hemocultura', categoria: 'LABORATORIAL' },
    { valor: 'TIPAGEM_SANGUINEA', label: 'Tipagem Sanguínea', categoria: 'LABORATORIAL' },
    { valor: 'SOROLOGIAS_DIVERSAS', label: 'Sorologias Diversas', categoria: 'LABORATORIAL' },
    { valor: 'TESTE_FUNCAO_TIREOIDE', label: 'Teste de Função Tireoidiana', categoria: 'LABORATORIAL' },
    { valor: 'LACTATO', label: 'Lactato Sérico', categoria: 'LABORATORIAL' }
  ];

  const categorias = [
    { valor: 'CLINICO', label: 'Exames Clínicos', cor: 'azul' },
    { valor: 'COMPLEMENTAR', label: 'Exames Complementares', cor: 'verde' },
    { valor: 'LABORATORIAL', label: 'Exames Laboratoriais', cor: 'roxo' }
  ];

  const tiposExameDisponiveis = tiposExame.filter(
    (tipo) => !exames.some((exame) => exame.tipoExame === tipo.valor)
  );

  useEffect(() => {
    if (protocoloId) {
      carregarExames();
      carregarResumo();
    }
  }, [protocoloId]);

  useEffect(() => {
    filtrarExames();
  }, [exames, filtroCategoria]);

  const carregarExames = async () => {
    setCarregando(true);
    try {
      const response = await apiClient.get(`/api/exames-me/protocolo/${protocoloId}`);
      setExames(response.data);
    } catch (err) {
      setErro('Erro ao carregar exames');
    } finally {
      setCarregando(false);
    }
  };

  const carregarResumo = async () => {
    try {
      const response = await apiClient.get(`/api/exames-me/protocolo/${protocoloId}/resumo`);
      setResumoExames(response.data);
    } catch (err) {
      console.error('Erro ao carregar resumo');
    }
  };

  const filtrarExames = () => {
    if (filtroCategoria) {
      setExamesFiltrados(exames.filter(e => e.categoria === filtroCategoria));
    } else {
      setExamesFiltrados(exames);
    }
  };

  const handleChangeForm = (e) => {
    const { name, value, checked, type } = e.target;
    setNovoExame(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  const handleCriarExame = async (e) => {
    e.preventDefault();
    setErro('');
    setSucesso('');

    if (!novoExame.tipoExame) {
      setErro('Selecione um tipo de exame');
      return;
    }

    const exameExistente = exames.find((exame) => exame.tipoExame === novoExame.tipoExame);
    if (exameExistente) {
      setErro('Esse exame já existe para este protocolo e não pode ser criado novamente');
      return;
    }

    try {
      const tipoSelecionado = tiposExame.find(t => t.valor === novoExame.tipoExame);
      const exame = {
        ...novoExame,
        tipoExame: novoExame.tipoExame,
        categoria: tipoSelecionado.categoria,
        protocoloME: { id: protocoloId }
      };

      const response = await apiClient.post('/api/exames-me', exame);
      setExames([...exames, response.data]);
      setNovoExame({
        tipoExame: '',
        descricao: '',
        resultado: '',
        resultado_positivo: null,
        responsavel: '',
        observacoes: ''
      });
      setSucesso('Exame adicionado!');
      setTimeout(() => setSucesso(''), 2000);
      carregarResumo();
      if (onAtualizacao) {
        onAtualizacao();
      }
    } catch (err) {
      setErro('Erro ao criar exame');
    }
  };

  const registrarResultado = async (exameId) => {
    if (!exameSelecionado) return;

    try {
      const params = new URLSearchParams();
      params.append('resultado', exameSelecionado.resultado || '');
      params.append('resultado_positivo', exameSelecionado.resultado_positivo);
      if (exameSelecionado.responsavel) {
        params.append('responsavel', exameSelecionado.responsavel);
      }

      const response = await apiClient.post(
        `/api/exames-me/${exameId}/resultado`,
        {},
        { params: Object.fromEntries(params) }
      );

      setExames(exames.map(e => e.id === exameId ? response.data : e));
      setExameSelecionado(null);
      setSucesso('Resultado registrado!');
      carregarResumo();
      if (onAtualizacao) {
        onAtualizacao();
      }
    } catch (err) {
      setErro('Erro ao registrar resultado');
    }
  };

  const deletarExame = async (exameId) => {
    if (window.confirm('Tem certeza que deseja deletar este exame?')) {
      try {
        await apiClient.delete(`/api/exames-me/${exameId}`);
        setExames(exames.filter(e => e.id !== exameId));
        setSucesso('Exame deletado!');
        carregarResumo();
        if (onAtualizacao) {
          onAtualizacao();
        }
      } catch (err) {
        setErro('Erro ao deletar exame');
      }
    }
  };

  const getCategoriaLabel = (categoria) => {
    const cat = categorias.find(c => c.valor === categoria);
    return cat ? cat.label : categoria;
  };

  const getCorCategoria = (categoria) => {
    const cat = categorias.find(c => c.valor === categoria);
    return cat ? cat.cor : 'cinza';
  };

  const getTipoLabel = (tipo) => {
    const t = tiposExame.find(x => x.valor === tipo);
    return t ? t.label : tipo;
  };

  const isExameRealizado = (exame) => {
    if (!exame) return false;
    const temResultadoTexto = exame.resultado && exame.resultado.trim() !== '';
    const temResultadoBooleano = exame.resultado_positivo !== null && exame.resultado_positivo !== undefined;
    const temData = !!exame.dataRealizacao;
    return temResultadoTexto || temResultadoBooleano || temData;
  };

  return (
    <div className="exame-manager-container">
      <h2>Gerenciador de Exames - Protocolo ME</h2>

      {resumoExames && (
        <div className="resumo-exames">
          <div className="card-resumo">
            <h4>Total de Exames</h4>
            <p className="valor">{resumoExames.totalExames}</p>
          </div>
          <div className="card-resumo">
            <h4>Exames Realizados</h4>
            <p className="valor">{resumoExames.examesRealizados}/{resumoExames.totalExames}</p>
          </div>
          <div className="card-resumo clinicos">
            <h4>Clínicos</h4>
            <p className="valor">{resumoExames.exames_Clinicos}/{resumoExames.examesClinicosTotal}</p>
          </div>
          <div className="card-resumo complementares">
            <h4>Complementares</h4>
            <p className="valor">{resumoExames.examesComplementares}/{resumoExames.examesComplementaresTotal}</p>
          </div>
          <div className="card-resumo laboratoriais">
            <h4>Laboratoriais</h4>
            <p className="valor">{resumoExames.examesLaboratoriais}/{resumoExames.examesLaboratoriaisTotal}</p>
          </div>
        </div>
      )}

      {erro && <div className="alerta alerta-erro">{erro}</div>}
      {sucesso && <div className="alerta alerta-sucesso">{sucesso}</div>}

      <div className="novo-exame-section">
        <h3>Adicionar Novo Exame</h3>
        {tiposExameDisponiveis.length === 0 ? (
          <p className="note">Todos os tipos de exame já foram gerados para este protocolo. Não é necessário adicionar novos exames.</p>
        ) : (
        <form onSubmit={handleCriarExame}>
          <div className="form-row">
            <div className="form-group">
              <label>Tipo de Exame *</label>
              <select
                name="tipoExame"
                value={novoExame.tipoExame}
                onChange={handleChangeForm}
                required
              >
                <option value="">Selecione um exame...</option>
                {categorias.map(cat => (
                  <optgroup key={cat.valor} label={cat.label}>
                    {tiposExameDisponiveis
                      .filter(t => t.categoria === cat.valor)
                      .map(tipo => (
                        <option key={tipo.valor} value={tipo.valor}>
                          {tipo.label}
                        </option>
                      ))}
                  </optgroup>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label>Responsável</label>
              <input
                type="text"
                name="responsavel"
                value={novoExame.responsavel}
                onChange={handleChangeForm}
                placeholder="Ex: Dr. Silva"
              />
            </div>
          </div>

          <div className="form-group">
            <label>Descrição/Observações</label>
            <textarea
              name="observacoes"
              value={novoExame.observacoes}
              onChange={handleChangeForm}
              placeholder="Notas sobre o exame"
              rows="2"
            />
          </div>

          <button type="submit" className="btn-adicionar">+ Adicionar Exame</button>
        </form>
        )}
      </div>

      <div className="filtro-section">
        <label>Filtrar por Categoria:</label>
        <select value={filtroCategoria} onChange={(e) => setFiltroCategoria(e.target.value)}>
          <option value="">Todas</option>
          {categorias.map(cat => (
            <option key={cat.valor} value={cat.valor}>
              {cat.label}
            </option>
          ))}
        </select>
        <button onClick={carregarExames} className="btn-recarregar">🔄 Atualizar</button>
      </div>

      {carregando && <div className="carregando">Carregando...</div>}

      <div className="exames-lista">
        {examesFiltrados.length > 0 ? (
          examesFiltrados.map(exame => (
            <div key={exame.id} className={`exame-item categoria-${getCorCategoria(exame.categoria)}`}>
              <div className="exame-header">
                <div>
                  <h4>{getTipoLabel(exame.tipoExame)}</h4>
                  <span className="categoria-badge">{getCategoriaLabel(exame.categoria)}</span>
                </div>
                <div className="exame-status">
                  {isExameRealizado(exame) ? (
                    <span className="realizado">✓ Realizado</span>
                  ) : (
                    <span className="pendente">⏱ Pendente</span>
                  )}
                </div>
              </div>

              <div className="exame-info">
                {exame.responsavel && <p><strong>Responsável:</strong> {exame.responsavel}</p>}
                {exame.observacoes && <p><strong>Observações:</strong> {exame.observacoes}</p>}
                {exame.resultado && <p><strong>Resultado:</strong> {exame.resultado}</p>}
              </div>

              <div className="exame-actions">
                <button
                  className="btn-resultado"
                    onClick={() => {
                      if (!isExameRealizado(exame)) {
                        setExameSelecionado(exame);
                      }
                    }}
                    disabled={isExameRealizado(exame)}
                >
                  {isExameRealizado(exame) ? '🔒 Realizado' : '📝 Registrar Resultado'}
                </button>
                <button
                  className="btn-deletar"
                  onClick={() => deletarExame(exame.id)}
                >
                  🗑️ Deletar
                </button>
              </div>

              {exameSelecionado?.id === exame.id && !isExameRealizado(exame) && (
                <div className="resultado-form">
                  <h5>Registrar Resultado</h5>
                  <div className="form-row">
                    <input
                      type="text"
                      placeholder="Resultado"
                      value={exameSelecionado.resultado || ''}
                      onChange={(e) => setExameSelecionado({
                        ...exameSelecionado,
                        resultado: e.target.value
                      })}
                    />
                    <select
                      value={exameSelecionado.resultado_positivo === null ? '' : exameSelecionado.resultado_positivo}
                      onChange={(e) => setExameSelecionado({
                        ...exameSelecionado,
                        resultado_positivo: e.target.value === '' ? null : e.target.value === 'true'
                      })}
                    >
                      <option value="">Selecione...</option>
                      <option value="true">Positivo</option>
                      <option value="false">Negativo</option>
                    </select>
                  </div>
                  <div className="actions-resultado">
                    <button onClick={() => registrarResultado(exame.id)} className="btn-salvar">
                      Salvar Resultado
                    </button>
                    <button
                      onClick={() => setExameSelecionado(null)}
                      className="btn-cancelar"
                    >
                      Cancelar
                    </button>
                  </div>

                  <GerenciadorAnexos
                    tipoAnexo="EXAME"
                    idExameOuProtocolo={exame.id}
                    titulo={`📎 Documentos - ${getTipoLabel(exame.tipoExame)}`}
                  />
                </div>
              )}
            </div>
          ))
        ) : (
          <div className="vazio">
            <p>Nenhum exame encontrado</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default ExameMEManager;
