import { useEffect, useState } from 'react';
import exameService from '../services/exameService';
import '../styles/ExameMEManager.css';

const ExameMEManager = ({ protocoloId, onAtualizacao }) => {
  const [exames, setExames] = useState([]);

  const [novoExame, setNovoExame] = useState({
    tipoExame: '',
    descricao: '',
    responsavel: '',
    observacoes: '',
    resultadoPositivo: ''
  });

  const [exameSelecionado, setExameSelecionado] = useState(null);
  const [erro, setErro] = useState('');
  const [sucesso, setSucesso] = useState('');

  const tiposExame = [
    { valor: 'REFLEXO_PUPILAR', label: 'Reflexo Pupilar' },
    { valor: 'REFLEXO_CORNEAL', label: 'Reflexo Corneal' },
    { valor: 'APNEIA_TEST', label: 'Teste de Apneia' },
    { valor: 'RESPOSTA_ESTIMULO_DORO', label: 'Resposta ao Estímulo Doloroso' },
    { valor: 'REFLEXO_VESTIBULO_OCULAR', label: 'Reflexo Vestibulo-Ocular' },
    { valor: 'REFLEXO_TOSSE', label: 'Reflexo de Tosse' },
    { valor: 'ANGIOGRAFIA_CEREBRAL', label: 'Angiografia Cerebral' },
    { valor: 'TOMOGRAFIA_CRANIO', label: 'Tomografia de Crânio' },
    { valor: 'ELETROENCEFALOGRAMA', label: 'EEG' }
  ];

  const obterCategoriaExame = (tipoExame) => {
    const clinicos = new Set([
      'REFLEXO_PUPILAR',
      'REFLEXO_CORNEAL',
      'APNEIA_TEST',
      'RESPOSTA_ESTIMULO_DORO',
      'REFLEXO_VESTIBULO_OCULAR',
      'REFLEXO_TOSSE'
    ]);

    const complementares = new Set([
      'ANGIOGRAFIA_CEREBRAL',
      'TOMOGRAFIA_CRANIO',
      'ELETROENCEFALOGRAMA'
    ]);

    if (clinicos.has(tipoExame)) {
      return 'CLINICO';
    }

    if (complementares.has(tipoExame)) {
      return 'COMPLEMENTAR';
    }

    return 'LABORATORIAL';
  };

  useEffect(() => {
    if (protocoloId) carregarExames();
  }, [protocoloId]);

  const carregarExames = async () => {
    try {
      const dados = await exameService.listarPorProtocolo(protocoloId);
      setExames(dados);
    } catch {
      setErro('Erro ao carregar exames');
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setNovoExame(prev => ({ ...prev, [name]: value }));
  };

  const criarExame = async (e) => {
    e.preventDefault();
    setErro('');
    setSucesso('');

    if (!novoExame.tipoExame || !novoExame.descricao.trim() || novoExame.resultadoPositivo === '') {
      setErro('Preencha todos os campos obrigatórios');
      return;
    }

    try {
      const payload = {
        protocoloId,
        categoria: obterCategoriaExame(novoExame.tipoExame),
        tipoExame: novoExame.tipoExame,
        descricao: novoExame.descricao.trim(),
        responsavel: novoExame.responsavel,
        observacoes: novoExame.observacoes,
        resultadoPositivo: novoExame.resultadoPositivo === 'true',
        resultado: novoExame.resultadoPositivo === 'true' ? 'POSITIVO' : 'NEGATIVO'
      };

      const criado = await exameService.criar(payload);

      setExames([...exames, criado]);

      setNovoExame({
        tipoExame: '',
        descricao: '',
        responsavel: '',
        observacoes: '',
        resultadoPositivo: ''
      });

      setSucesso('Exame criado com sucesso');
      if (onAtualizacao) onAtualizacao();
    } catch {
      setErro('Erro ao criar exame');
    }
  };

  const deletarExame = async (id) => {
    const ok = window.confirm('Deseja realmente excluir este exame?');
    if (!ok) return;

    try {
      await exameService.deletar(id);
      setExames(exames.filter(e => e.id !== id));
      setSucesso('Exame excluído');
      if (onAtualizacao) onAtualizacao();
    } catch {
      setErro('Erro ao excluir');
    }
  };

  const salvarResultado = async (exame) => {
    try {
      const atualizado = await exameService.atualizarResultado(
        exame.id,
        exame.resultadoPositivo,
        exame.responsavel
      );

      setExames(exames.map(e => e.id === exame.id ? atualizado : e));
      setExameSelecionado(null);
      setSucesso('Resultado atualizado');
      if (onAtualizacao) onAtualizacao();
    } catch {
      setErro('Erro ao salvar resultado');
    }
  };

  return (
    <div className="exame-manager-container">
      <div className="exame-header">
        <h2>Exames do Protocolo</h2>
        <p className="exame-subtitulo">Cadastro e revisão dos exames clínicos do protocolo de ME.</p>
      </div>

      {erro && <div className="alerta erro">{erro}</div>}
      {sucesso && <div className="alerta sucesso">{sucesso}</div>}

      {/* ================= FORM ================= */}
      <div className="card-form">
        <h3>Novo Exame</h3>

        <form onSubmit={criarExame}>

          <select name="tipoExame" value={novoExame.tipoExame} onChange={handleChange}>
            <option value="">Tipo de exame</option>
            {tiposExame.map(t => (
              <option key={t.valor} value={t.valor}>{t.label}</option>
            ))}
          </select>

          <input
            name="descricao"
            placeholder="Descrição"
            value={novoExame.descricao}
            onChange={handleChange}
          />

          <select
            name="resultadoPositivo"
            value={novoExame.resultadoPositivo}
            onChange={handleChange}
          >
            <option value="">Positivo / Negativo</option>
            <option value="true">Positivo</option>
            <option value="false">Negativo</option>
          </select>

          <input
            name="responsavel"
            placeholder="Responsável"
            value={novoExame.responsavel}
            onChange={handleChange}
          />

          <textarea
            className="campo-largo"
            name="observacoes"
            placeholder="Observações"
            value={novoExame.observacoes}
            onChange={handleChange}
          />

          <button type="submit" className="btn-primario">
            + Adicionar Exame
          </button>

        </form>
      </div>

      {/* ================= LISTA ================= */}
      <div className="lista-exames">
        {exames.length === 0 && (
          <div className="estado-vazio">
            Nenhum exame registrado para este protocolo.
          </div>
        )}

        {exames.map(exame => (
          <div key={exame.id} className="card-exame">

            <div className="header-exame">
              <strong>{exame.tipoExame}</strong>

              <span className={exame.resultadoPositivo ? 'positivo' : 'negativo'}>
                {exame.resultadoPositivo ? 'POSITIVO' : 'NEGATIVO'}
              </span>
            </div>

            <p><strong>Responsável:</strong> {exame.responsavel || 'Não informado'}</p>
            {exame.observacoes && <p className="obs"><strong>Obs:</strong> {exame.observacoes}</p>}

            {/* BOTÕES */}
            <div className="acoes">

              <button
                className="btn-editar"
                onClick={() => setExameSelecionado({...exame})}
              >
                Editar
              </button>

              <button
                className="btn-excluir"
                onClick={() => deletarExame(exame.id)}
              >
                Excluir
              </button>

            </div>

            {/* EDIT INLINE */}
            {exameSelecionado?.id === exame.id && (
              <div className="edit-box">

                <div className="form-group">
                  <label>Resultado:</label>
                  <select
                    value={exameSelecionado.resultadoPositivo ? 'true' : 'false'}
                    onChange={(e) =>
                      setExameSelecionado({
                        ...exameSelecionado,
                        resultadoPositivo: e.target.value === 'true'
                      })
                    }
                  >
                    <option value="true">Positivo</option>
                    <option value="false">Negativo</option>
                  </select>
                </div>

                <div className="form-group">
                  <label>Responsável:</label>
                  <input
                    type="text"
                    value={exameSelecionado.responsavel || ''}
                    onChange={(e) => setExameSelecionado({
                      ...exameSelecionado,
                      responsavel: e.target.value
                    })}
                  />
                </div>

                <div className="acoes-edit">
                  <button
                    className="btn-salvar"
                    onClick={() => salvarResultado(exameSelecionado)}
                  >
                    Salvar
                  </button>

                  <button
                    className="btn-cancelar"
                    onClick={() => setExameSelecionado(null)}
                  >
                    Cancelar
                  </button>
                </div>

              </div>
            )}

          </div>
        ))}

      </div>
    </div>
  );
};

export default ExameMEManager;
