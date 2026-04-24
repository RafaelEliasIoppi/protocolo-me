import React, { useState, useEffect } from 'react';
import apiClient from '../services/apiClient';
import '../styles/ExameMEManager.css';

const ExameMEManager = ({ protocoloId, onAtualizacao }) => {
  const [exames, setExames] = useState([]);

  const [novoExame, setNovoExame] = useState({
    tipoExame: '',
    responsavel: '',
    observacoes: '',
    resultado_positivo: ''
  });

  const [exameSelecionado, setExameSelecionado] = useState(null);
  const [erro, setErro] = useState('');
  const [sucesso, setSucesso] = useState('');

  const tiposExame = [
    { valor: 'REFLEXO_PUPILAR', label: 'Reflexo Pupilar' },
    { valor: 'REFLEXO_CORNEAL', label: 'Reflexo Corneal' },
    { valor: 'APNEIA_TEST', label: 'Teste de Apneia' }
  ];

  useEffect(() => {
    if (protocoloId) carregarExames();
  }, [protocoloId]);

  const carregarExames = async () => {
    try {
      const res = await apiClient.get(`/api/exames-me/protocolo/${protocoloId}`);
      setExames(res.data);
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

    if (!novoExame.tipoExame || novoExame.resultado_positivo === '') {
      setErro('Preencha todos os campos obrigatórios');
      return;
    }

    try {
      const payload = {
        ...novoExame,
        resultado_positivo: novoExame.resultado_positivo === 'true',
        protocoloME: { id: protocoloId }
      };

      const res = await apiClient.post('/api/exames-me', payload);

      setExames([...exames, res.data]);

      setNovoExame({
        tipoExame: '',
        responsavel: '',
        observacoes: '',
        resultado_positivo: ''
      });

      setSucesso('Exame criado com sucesso');
    } catch {
      setErro('Erro ao criar exame');
    }
  };

  const deletarExame = async (id) => {
    const ok = window.confirm('Deseja realmente excluir este exame?');
    if (!ok) return;

    try {
      await apiClient.delete(`/api/exames-me/${id}`);
      setExames(exames.filter(e => e.id !== id));
      setSucesso('Exame excluído');
    } catch {
      setErro('Erro ao excluir');
    }
  };

  const salvarResultado = async (exame) => {
    try {
      const res = await apiClient.post(
        `/api/exames-me/${exame.id}/resultado`,
        {},
        {
          params: {
            resultado_positivo: exame.resultado_positivo
          }
        }
      );

      setExames(exames.map(e => e.id === exame.id ? res.data : e));
      setExameSelecionado(null);
      setSucesso('Resultado atualizado');
    } catch {
      setErro('Erro ao salvar resultado');
    }
  };

  return (
    <div className="exame-manager-container">

      <h2>Exames do Protocolo</h2>

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

          <select
            name="resultado_positivo"
            value={novoExame.resultado_positivo}
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

        {exames.map(exame => (
          <div key={exame.id} className="card-exame">

            <div className="header-exame">
              <strong>{exame.tipoExame}</strong>

              <span className={exame.resultado_positivo ? 'positivo' : 'negativo'}>
                {exame.resultado_positivo ? 'POSITIVO' : 'NEGATIVO'}
              </span>
            </div>

            <p>{exame.responsavel}</p>

            {/* BOTÕES */}
            <div className="acoes">

              <button
                className="btn-editar"
                onClick={() => setExameSelecionado(exame)}
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

                <select
                  value={exameSelecionado.resultado_positivo ? 'true' : 'false'}
                  onChange={(e) =>
                    setExameSelecionado({
                      ...exameSelecionado,
                      resultado_positivo: e.target.value === 'true'
                    })
                  }
                >
                  <option value="true">Positivo</option>
                  <option value="false">Negativo</option>
                </select>

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