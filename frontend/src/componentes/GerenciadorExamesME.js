import { useEffect, useRef, useState } from 'react';
import exameService from '../services/exameService';
import '../styles/GerenciadorExamesME.css';

const GerenciadorExamesME = ({ protocoloId, onAtualizacao }) => {
  // Lista de exames já registrados para este protocolo.
  const [exames, setExames] = useState([]);

  // Estado do formulário de criação do exame.
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
  const [carregando, setCarregando] = useState(false);
  const montadoRef = useRef(true);

  const normalizarExames = (dados) => {
    if (Array.isArray(dados)) return dados;
    if (Array.isArray(dados?.content)) return dados.content;
    if (Array.isArray(dados?.data)) return dados.data;
    return [];
  };

  const carregarExames = async (protocoloAtual = protocoloId) => {
    if (!protocoloAtual) {
      setExames([]);
      return [];
    }

    try {
      const dados = await exameService.listarPorProtocolo(protocoloAtual);
      const lista = normalizarExames(dados);
      if (!montadoRef.current) {
        return lista;
      }
      setExames(lista);
      return lista;
    } catch {
      if (montadoRef.current) {
        setErro('Erro ao carregar exames');
      }
      return [];
    }
  };

  // Limpar mensagens após 5 segundos
  useEffect(() => {
    if (erro) {
      const timer = setTimeout(() => setErro(''), 5000);
      return () => clearTimeout(timer);
    }
  }, [erro]);

  useEffect(() => {
    if (sucesso) {
      const timer = setTimeout(() => setSucesso(''), 5000);
      return () => clearTimeout(timer);
    }
  }, [sucesso]);

  // Catálogo de tipos de exame exibido no select.
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
    return () => {
      montadoRef.current = false;
    };
  }, []);

  useEffect(() => {
    // Sempre que o protocolo mudar, recarrega os exames relacionados.
    let ativo = true;

    const carregar = async () => {
      if (!ativo) return;
      await carregarExames(protocoloId);
    };

    carregar();

    return () => {
      ativo = false;
    };
  }, [protocoloId]);

  const atualizarCampoFormulario = (e) => {
    // Atualiza qualquer campo do formulário de forma genérica.
    const { name, value } = e.target;
    setNovoExame(prev => ({ ...prev, [name]: value }));
  };

  const criarExame = async (e) => {
    e.preventDefault();
    setErro('');
    setSucesso('');
    setCarregando(true);

    // Validação mínima antes de chamar a API.
    if (!novoExame.tipoExame || !novoExame.descricao.trim() || novoExame.resultadoPositivo === '') {
      setErro('Preencha todos os campos obrigatórios');
      setCarregando(false);
      return;
    }

    try {
      // Monta o payload no formato esperado pelo backend.
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

      // Envia o exame para criação.
      await exameService.criar(payload);

      await carregarExames();

      // Limpa o formulário
      setNovoExame({
        tipoExame: '',
        descricao: '',
        responsavel: '',
        observacoes: '',
        resultadoPositivo: ''
      });

      setSucesso('Exame criado com sucesso');
      if (onAtualizacao) onAtualizacao();
    } catch (err) {
      console.error('Erro ao criar exame:', err);
      setErro('Erro ao criar exame. Tente novamente.');
    } finally {
      setCarregando(false);
    }
  };

  const deletarExame = async (id) => {
    // Confirma antes de apagar para evitar exclusão acidental.
    const ok = window.confirm('Deseja realmente excluir este exame?');
    if (!ok) return;

    setCarregando(true);
    setErro('');

    try {
      await exameService.deletar(id);
      await carregarExames();
      setSucesso('Exame excluído');
      if (onAtualizacao) onAtualizacao();
    } catch (err) {
      console.error('Erro ao excluir exame:', err);
      setErro('Erro ao excluir');
    } finally {
      setCarregando(false);
    }
  };

  const salvarResultado = async (exame) => {
    setCarregando(true);
    setErro('');

    try {
      // Persiste as alterações feitas no cartão de edição inline.
      await exameService.atualizarResultado(
        exame.id,
        exame.resultadoPositivo,
        exame.responsavel
      );

      await carregarExames();
      setExameSelecionado(null);
      setSucesso('Resultado atualizado');
      if (onAtualizacao) onAtualizacao();
    } catch (err) {
      console.error('Erro ao salvar resultado:', err);
      setErro('Erro ao salvar resultado');
    } finally {
      setCarregando(false);
    }
  };

  // Filtrar exames por categoria
  const examesClinicos = exames.filter(e => obterCategoriaExame(e.tipoExame) === 'CLINICO');
  const examesComplementares = exames.filter(e => obterCategoriaExame(e.tipoExame) === 'COMPLEMENTAR');
  const examesLaboratoriais = exames.filter(e => obterCategoriaExame(e.tipoExame) === 'LABORATORIAL');

  const renderCardExame = (exame) => (
    <div key={exame.id} className="card-exame">
      <div className="header-exame">
        <strong>{tiposExame.find(t => t.valor === exame.tipoExame)?.label || exame.tipoExame}</strong>
        <span className={exame.resultadoPositivo ? 'positivo' : 'negativo'}>
          {exame.resultadoPositivo ? 'POSITIVO' : 'NEGATIVO'}
        </span>
      </div>
      <p><strong>Responsável:</strong> {exame.responsavel || 'Não informado'}</p>
      {exame.observacoes && <p className="obs"><strong>Obs:</strong> {exame.observacoes}</p>}
      <div className="acoes">
        <button className="btn-editar" onClick={() => setExameSelecionado({...exame})} disabled={carregando}>Editar</button>
        <button className="btn-excluir" onClick={() => deletarExame(exame.id)} disabled={carregando}>Excluir</button>
      </div>
      {exameSelecionado?.id === exame.id && (
        <div className="edit-box">
          <div className="form-group">
            <label>Resultado:</label>
            <select
              value={exameSelecionado.resultadoPositivo ? 'true' : 'false'}
              onChange={(e) => setExameSelecionado({ ...exameSelecionado, resultadoPositivo: e.target.value === 'true' })}
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
              onChange={(e) => setExameSelecionado({ ...exameSelecionado, responsavel: e.target.value })}
            />
          </div>
          <div className="acoes-edit">
            <button className="btn-salvar" onClick={() => salvarResultado(exameSelecionado)} disabled={carregando}>Salvar</button>
            <button className="btn-cancelar" onClick={() => setExameSelecionado(null)} disabled={carregando}>Cancelar</button>
          </div>
        </div>
      )}
    </div>
  );

  return (
    <div className="exame-manager-container">
      <div className="exame-header">
        <h2>Exames do Protocolo</h2>
        <p className="exame-subtitulo">Cadastro e revisão dos exames clínicos do protocolo de ME.</p>
      </div>

      {erro && <div className="alerta erro">{erro}</div>}
      {sucesso && <div className="alerta sucesso">{sucesso}</div>}

      <div className="card-form">
        <h3>Novo Exame</h3>
        <p className="exame-ajuda">Preencha os campos obrigatórios e clique em Salvar exame.</p>
        <form onSubmit={criarExame}>
          <select name="tipoExame" value={novoExame.tipoExame} onChange={atualizarCampoFormulario} disabled={carregando}>
            <option value="">Tipo de exame</option>
            {tiposExame.map(t => (
              <option key={t.valor} value={t.valor}>{t.label}</option>
            ))}
          </select>
          <input name="descricao" placeholder="Descrição" value={novoExame.descricao} onChange={atualizarCampoFormulario} disabled={carregando} />
          <select name="resultadoPositivo" value={novoExame.resultadoPositivo} onChange={atualizarCampoFormulario} disabled={carregando}>
            <option value="">Positivo / Negativo</option>
            <option value="true">Positivo</option>
            <option value="false">Negativo</option>
          </select>
          <input name="responsavel" placeholder="Responsável" value={novoExame.responsavel} onChange={atualizarCampoFormulario} disabled={carregando} />
          <textarea className="campo-largo" name="observacoes" placeholder="Observações" value={novoExame.observacoes} onChange={atualizarCampoFormulario} disabled={carregando} />
          <button type="submit" className="btn-primario" title="Salvar exame" disabled={carregando}>
            {carregando ? 'Salvando exame...' : 'Salvar exame'}
          </button>
        </form>
      </div>

      <div className="painel-categorias">
        <section className="categoria-exames">
          <h3 className="titulo-categoria">Exames Clínicos</h3>
          <div className="lista-exames">
            {examesClinicos.length === 0 ? <div className="estado-vazio">Nenhum exame clínico registrado.</div> : examesClinicos.map(renderCardExame)}
          </div>
        </section>

        <section className="categoria-exames">
          <h3 className="titulo-categoria">Exames Complementares</h3>
          <div className="lista-exames">
            {examesComplementares.length === 0 ? <div className="estado-vazio">Nenhum exame complementar registrado.</div> : examesComplementares.map(renderCardExame)}
          </div>
        </section>

        {examesLaboratoriais.length > 0 && (
          <section className="categoria-exames">
            <h3 className="titulo-categoria">Exames Laboratoriais</h3>
            <div className="lista-exames">
              {examesLaboratoriais.map(renderCardExame)}
            </div>
          </section>
        )}
      </div>
    </div>
  );
};

export default GerenciadorExamesME;
