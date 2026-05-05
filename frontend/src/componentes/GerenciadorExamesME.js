import { useEffect, useRef, useState } from 'react';
import exameService from '../services/exameService';
import '../styles/GerenciadorExamesME.css';

const GerenciadorExamesME = ({ protocoloId, onAtualizacao }) => {

  const [exames, setExames] = useState([]);

  const [novoExame, setNovoExame] = useState({
    tipoExame: '',
    descricao: '',
    responsavel: '',
    observacoes: '',
    resultadoPositivo: '',

    // TESTE DE APNEIA
    paco2Inicial: '',
    paco2Final: '',
    tempoTeste: '',
    saturacao: '',
    interrompido: false
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

      const dados =
        await exameService.listarPorProtocolo(protocoloAtual);

      const lista = normalizarExames(dados);

      if (!montadoRef.current) {
        return lista;
      }

      setExames(lista);

      return lista;

    } catch (err) {

      console.error(err);

      if (montadoRef.current) {
        setErro('Erro ao carregar exames');
      }

      return [];
    }
  };

  // LIMPA ALERTAS
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

  // TIPOS DE EXAMES
  const tiposExame = [
    { valor: 'REFLEXO_PUPILAR', label: 'Reflexo Pupilar' },

    { valor: 'REFLEXO_CORNEAL', label: 'Reflexo Corneal' },

    { valor: 'APNEIA_TEST', label: 'Teste de Apneia' },

    {
      valor: 'RESPOSTA_ESTIMULO_DOLOROSO',
      label: 'Resposta ao Estímulo Doloroso'
    },

    {
      valor: 'REFLEXO_VESTIBULO_OCULAR',
      label: 'Reflexo Vestibulo-Ocular'
    },

    {
      valor: 'REFLEXO_TOSSE',
      label: 'Reflexo de Tosse'
    },

    {
      valor: 'ANGIOGRAFIA_CEREBRAL',
      label: 'Angiografia Cerebral'
    },

    {
      valor: 'TOMOGRAFIA_CRANIO',
      label: 'Tomografia de Crânio'
    },

    {
      valor: 'ELETROENCEFALOGRAMA',
      label: 'EEG'
    }
  ];

  const obterCategoriaExame = (tipoExame) => {

    const clinicos = new Set([
      'REFLEXO_PUPILAR',
      'REFLEXO_CORNEAL',
      'APNEIA_TEST',
      'RESPOSTA_ESTIMULO_DOLOROSO',
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

    const {
      name,
      value,
      type,
      checked
    } = e.target;

    setNovoExame(prev => ({
      ...prev,

      [name]:
        type === 'checkbox'
          ? checked
          : value
    }));
  };

  const criarExame = async (e) => {

    e.preventDefault();

    setErro('');
    setSucesso('');
    setCarregando(true);

    if (
      !novoExame.tipoExame ||
      !novoExame.descricao.trim() ||
      novoExame.resultadoPositivo === ''
    ) {
      setErro('Preencha todos os campos obrigatórios');
      setCarregando(false);
      return;
    }

    try {

      const payload = {

        protocoloId,

        categoria:
          obterCategoriaExame(novoExame.tipoExame),

        tipoExame:
          novoExame.tipoExame,

        descricao:
          novoExame.descricao.trim(),

        responsavel:
          novoExame.responsavel,

        observacoes:
          novoExame.observacoes,

        resultadoPositivo:
          novoExame.resultadoPositivo === 'true',

        resultado:
          novoExame.resultadoPositivo === 'true'
            ? 'POSITIVO'
            : 'NEGATIVO',

        // APNEIA
        paco2Inicial:
          novoExame.paco2Inicial,

        paco2Final:
          novoExame.paco2Final,

        tempoTeste:
          novoExame.tempoTeste,

        saturacao:
          novoExame.saturacao,

        interrompido:
          novoExame.interrompido
      };

      await exameService.criar(payload);

      await carregarExames();

      // RESET FORM
      setNovoExame({
        tipoExame: '',
        descricao: '',
        responsavel: '',
        observacoes: '',
        resultadoPositivo: '',

        paco2Inicial: '',
        paco2Final: '',
        tempoTeste: '',
        saturacao: '',
        interrompido: false
      });

      setSucesso('Exame criado com sucesso');

      if (onAtualizacao) {
        onAtualizacao();
      }

    } catch (err) {

      console.error(err);

      setErro('Erro ao criar exame');

    } finally {

      setCarregando(false);
    }
  };

  const deletarExame = async (id) => {

    const ok = window.confirm(
      'Deseja realmente excluir este exame?'
    );

    if (!ok) return;

    setCarregando(true);
    setErro('');

    try {

      await exameService.deletar(id);

      await carregarExames();

      setSucesso('Exame excluído');

      if (onAtualizacao) {
        onAtualizacao();
      }

    } catch (err) {

      console.error(err);

      setErro('Erro ao excluir');

    } finally {

      setCarregando(false);
    }
  };

  const salvarResultado = async (exame) => {

    setCarregando(true);
    setErro('');

    try {

      await exameService.atualizarResultado(
        exame.id,
        exame.resultadoPositivo,
        exame.responsavel
      );

      await carregarExames();

      setExameSelecionado(null);

      setSucesso('Resultado atualizado');

      if (onAtualizacao) {
        onAtualizacao();
      }

    } catch (err) {

      console.error(err);

      setErro('Erro ao salvar resultado');

    } finally {

      setCarregando(false);
    }
  };

  // FILTROS
  const examesClinicos =
    exames.filter(
      e => obterCategoriaExame(e.tipoExame) === 'CLINICO'
    );

  const examesComplementares =
    exames.filter(
      e => obterCategoriaExame(e.tipoExame) === 'COMPLEMENTAR'
    );

  const examesLaboratoriais =
    exames.filter(
      e => obterCategoriaExame(e.tipoExame) === 'LABORATORIAL'
    );

  const renderCardExame = (exame) => (

    <div key={exame.id} className="card-exame">

      <div className="header-exame">

        <strong>
          {
            tiposExame.find(
              t => t.valor === exame.tipoExame
            )?.label || exame.tipoExame
          }
        </strong>

        <span
          className={
            exame.resultadoPositivo
              ? 'positivo'
              : 'negativo'
          }
        >
          {
            exame.resultadoPositivo
              ? 'POSITIVO'
              : 'NEGATIVO'
          }
        </span>

      </div>

      <p>
        <strong>Responsável:</strong>
        {' '}
        {exame.responsavel || 'Não informado'}
      </p>

      {exame.observacoes && (
        <p className="obs">
          <strong>Obs:</strong>
          {' '}
          {exame.observacoes}
        </p>
      )}

      {/* DADOS APNEIA */}
      {exame.tipoExame === 'APNEIA_TEST' && (

        <div className="dados-apneia">

          <p>
            <strong>PaCO2 Inicial:</strong>
            {' '}
            {exame.paco2Inicial || '-'}
          </p>

          <p>
            <strong>PaCO2 Final:</strong>
            {' '}
            {exame.paco2Final || '-'}
          </p>

          <p>
            <strong>Tempo:</strong>
            {' '}
            {exame.tempoTeste || '-'} min
          </p>

          <p>
            <strong>Saturação:</strong>
            {' '}
            {exame.saturacao || '-'}%
          </p>

          <p>
            <strong>Interrompido:</strong>
            {' '}
            {exame.interrompido ? 'Sim' : 'Não'}
          </p>

        </div>
      )}

      <div className="acoes">

        <button
          className="btn-editar"
          onClick={() =>
            setExameSelecionado({ ...exame })
          }
          disabled={carregando}
        >
          Editar
        </button>

        <button
          className="btn-excluir"
          onClick={() => deletarExame(exame.id)}
          disabled={carregando}
        >
          Excluir
        </button>

      </div>

      {exameSelecionado?.id === exame.id && (

        <div className="edit-box">

          <div className="form-group">

            <label>Resultado:</label>

            <select
              value={
                exameSelecionado.resultadoPositivo
                  ? 'true'
                  : 'false'
              }
              onChange={(e) =>
                setExameSelecionado({
                  ...exameSelecionado,
                  resultadoPositivo:
                    e.target.value === 'true'
                })
              }
            >

              <option value="true">
                Positivo
              </option>

              <option value="false">
                Negativo
              </option>

            </select>

          </div>

          <div className="form-group">

            <label>Responsável:</label>

            <input
              type="text"
              value={exameSelecionado.responsavel || ''}
              onChange={(e) =>
                setExameSelecionado({
                  ...exameSelecionado,
                  responsavel: e.target.value
                })
              }
            />

          </div>

          {/* EDIÇÃO APNEIA */}
          {exameSelecionado.tipoExame === 'APNEIA_TEST' && (

            <div className="bloco-apneia">

              <input
                type="number"
                placeholder="PaCO2 Inicial"
                value={exameSelecionado.paco2Inicial || ''}
                onChange={(e) =>
                  setExameSelecionado({
                    ...exameSelecionado,
                    paco2Inicial: e.target.value
                  })
                }
              />

              <input
                type="number"
                placeholder="PaCO2 Final"
                value={exameSelecionado.paco2Final || ''}
                onChange={(e) =>
                  setExameSelecionado({
                    ...exameSelecionado,
                    paco2Final: e.target.value
                  })
                }
              />

              <input
                type="number"
                placeholder="Tempo do teste"
                value={exameSelecionado.tempoTeste || ''}
                onChange={(e) =>
                  setExameSelecionado({
                    ...exameSelecionado,
                    tempoTeste: e.target.value
                  })
                }
              />

            </div>
          )}

          <div className="acoes-edit">

            <button
              className="btn-salvar"
              onClick={() =>
                salvarResultado(exameSelecionado)
              }
              disabled={carregando}
            >
              Salvar
            </button>

            <button
              className="btn-cancelar"
              onClick={() =>
                setExameSelecionado(null)
              }
              disabled={carregando}
            >
              Cancelar
            </button>

          </div>

        </div>
      )}

    </div>
  );

  return (

    <div className="exame-manager-container">

      <div className="exame-header">

        <h2>
          Exames do Protocolo
        </h2>

        <p className="exame-subtitulo">
          Cadastro e revisão dos exames clínicos do protocolo de ME.
        </p>

      </div>

      {erro && (
        <div className="alerta erro">
          {erro}
        </div>
      )}

      {sucesso && (
        <div className="alerta sucesso">
          {sucesso}
        </div>
      )}

      <div className="card-form">

        <h3>Novo Exame</h3>

        <p className="exame-ajuda">
          Preencha os campos obrigatórios.
        </p>

        <form onSubmit={criarExame}>

          <select
            name="tipoExame"
            value={novoExame.tipoExame}
            onChange={atualizarCampoFormulario}
            disabled={carregando}
          >

            <option value="">
              Tipo de exame
            </option>

            {tiposExame.map(t => (
              <option
                key={t.valor}
                value={t.valor}
              >
                {t.label}
              </option>
            ))}

          </select>

          <input
            name="descricao"
            placeholder="Descrição"
            value={novoExame.descricao}
            onChange={atualizarCampoFormulario}
            disabled={carregando}
          />

          <select
            name="resultadoPositivo"
            value={novoExame.resultadoPositivo}
            onChange={atualizarCampoFormulario}
            disabled={carregando}
          >

            <option value="">
              Positivo / Negativo
            </option>

            <option value="true">
              Positivo
            </option>

            <option value="false">
              Negativo
            </option>

          </select>

          <input
            name="responsavel"
            placeholder="Responsável"
            value={novoExame.responsavel}
            onChange={atualizarCampoFormulario}
            disabled={carregando}
          />

          {/* FORM APNEIA */}
          {novoExame.tipoExame === 'APNEIA_TEST' && (

            <div className="bloco-apneia">

              <h4>
                Dados do Teste de Apneia
              </h4>

              <input
                type="number"
                name="paco2Inicial"
                placeholder="PaCO2 Inicial"
                value={novoExame.paco2Inicial}
                onChange={atualizarCampoFormulario}
              />

              <input
                type="number"
                name="paco2Final"
                placeholder="PaCO2 Final"
                value={novoExame.paco2Final}
                onChange={atualizarCampoFormulario}
              />

              <input
                type="number"
                name="tempoTeste"
                placeholder="Tempo do teste (min)"
                value={novoExame.tempoTeste}
                onChange={atualizarCampoFormulario}
              />

              <input
                type="number"
                name="saturacao"
                placeholder="Saturação (%)"
                value={novoExame.saturacao}
                onChange={atualizarCampoFormulario}
              />

              <label className="checkbox-apneia">

                <input
                  type="checkbox"
                  name="interrompido"
                  checked={novoExame.interrompido}
                  onChange={atualizarCampoFormulario}
                />

                Teste interrompido

              </label>

            </div>
          )}

          <textarea
            className="campo-largo"
            name="observacoes"
            placeholder="Observações"
            value={novoExame.observacoes}
            onChange={atualizarCampoFormulario}
            disabled={carregando}
          />

          <button
            type="submit"
            className="btn-primario"
            disabled={carregando}
          >
            {
              carregando
                ? 'Salvando exame...'
                : 'Salvar exame'
            }
          </button>

        </form>

      </div>

      <div className="painel-categorias">

        <section className="categoria-exames">

          <h3 className="titulo-categoria">
            Exames Clínicos
          </h3>

          <div className="lista-exames">

            {examesClinicos.length === 0
              ? (
                <div className="estado-vazio">
                  Nenhum exame clínico registrado.
                </div>
              )
              : examesClinicos.map(renderCardExame)
            }

          </div>

        </section>

        <section className="categoria-exames">

          <h3 className="titulo-categoria">
            Exames Complementares
          </h3>

          <div className="lista-exames">

            {examesComplementares.length === 0
              ? (
                <div className="estado-vazio">
                  Nenhum exame complementar registrado.
                </div>
              )
              : examesComplementares.map(renderCardExame)
            }

          </div>

        </section>

        {examesLaboratoriais.length > 0 && (

          <section className="categoria-exames">

            <h3 className="titulo-categoria">
              Exames Laboratoriais
            </h3>

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
