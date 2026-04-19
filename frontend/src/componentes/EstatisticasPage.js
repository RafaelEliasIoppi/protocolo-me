import React, { useState, useEffect } from 'react';
import apiClient from '../services/apiClient';
import '../styles/EstatisticasPage.css';

const EstatisticasPage = () => {
  const [estatisticasGerais, setEstatisticasGerais] = useState(null);
  const [estatisticasPorPaciente, setEstatisticasPorPaciente] = useState([]);
  const [anosDisponiveis, setAnosDisponiveis] = useState([]);
  const [anoSelecionado, setAnoSelecionado] = useState(null);
  const [carregando, setCarregando] = useState(false);
  const [erro, setErro] = useState('');
  const [abas, setAbas] = useState('geral'); // 'geral' ou 'pacientes'
  const [filtroNomePaciente, setFiltroNomePaciente] = useState('');

  useEffect(() => {
    carregarAnosDisponiveis();
    carregarEstatisticasGerais(null);
  }, []);

  const carregarAnosDisponiveis = async () => {
    try {
      const response = await apiClient.get('/api/estatisticas-transplantes/anos-disponiveis');
      setAnosDisponiveis(response.data);
      if (response.data.length > 0) {
        setAnoSelecionado(response.data[0]);
      }
    } catch (err) {
      console.error('Erro ao carregar anos:', err);
    }
  };

  const carregarEstatisticasGerais = async (ano = anoSelecionado) => {
    setCarregando(true);
    setErro('');
    try {
      const params = ano ? { ano } : {};
      const response = await apiClient.get('/api/estatisticas-transplantes/gerais', { params });
      setEstatisticasGerais(response.data);
    } catch (err) {
      setErro('Erro ao carregar estatísticas gerais');
      console.error(err);
    } finally {
      setCarregando(false);
    }
  };

  const carregarEstatisticasPorPaciente = async (ano = anoSelecionado) => {
    setCarregando(true);
    setErro('');
    try {
      const params = ano ? { ano } : {};
      const response = await apiClient.get('/api/estatisticas-transplantes/por-paciente', { params });
      setEstatisticasPorPaciente(response.data);
    } catch (err) {
      setErro('Erro ao carregar estatísticas por paciente');
      console.error(err);
    } finally {
      setCarregando(false);
    }
  };

  const handleMudarAno = (ano) => {
    setAnoSelecionado(ano);
    if (abas === 'geral') {
      carregarEstatisticasGerais(ano);
    } else {
      carregarEstatisticasPorPaciente(ano);
    }
  };

  const handleMudarAba = (aba) => {
    setAbas(aba);
    if (aba === 'geral') {
      carregarEstatisticasGerais(anoSelecionado);
    } else {
      carregarEstatisticasPorPaciente(anoSelecionado);
    }
  };

  const pacientesFiltrados = estatisticasPorPaciente.filter(p =>
    p.nomePaciente.toLowerCase().includes(filtroNomePaciente.toLowerCase())
  );

  return (
    <div className="estatisticas-page">
      <div className="page-header">
        <h1>📊 Estatísticas de Transplantes</h1>
        <p>Acompanhamento de doações e implantações de órgãos</p>
      </div>

      {erro && <div className="alerta alerta-erro">{erro}</div>}

      {/* Controles de Filtro */}
      <div className="controles-filtro">
        <div className="filtro-ano">
          <label htmlFor="filtro-ano">Filtrar por Ano:</label>
          <select
            id="filtro-ano"
            value={anoSelecionado || ''}
            onChange={(e) => handleMudarAno(e.target.value ? parseInt(e.target.value) : null)}
            className="select-ano"
          >
            <option value="">Todos os anos</option>
            {anosDisponiveis.map(ano => (
              <option key={ano} value={ano}>{ano}</option>
            ))}
          </select>
        </div>

        <div className="abas">
          <button
            className={`aba ${abas === 'geral' ? 'ativa' : ''}`}
            onClick={() => handleMudarAba('geral')}
          >
            📈 Visão Geral
          </button>
          <button
            className={`aba ${abas === 'pacientes' ? 'ativa' : ''}`}
            onClick={() => handleMudarAba('pacientes')}
          >
            👥 Por Paciente
          </button>
        </div>
      </div>

      {/* Conteúdo por Aba */}
      {carregando ? (
        <div className="carregando">⏳ Carregando estatísticas...</div>
      ) : (
        <>
          {/* ABA: VISÃO GERAL */}
          {abas === 'geral' && estatisticasGerais && (
            <div className="aba-conteudo">
              {/* Cards de Resumo */}
              <div className="resumo-cards">
                <div className="card card-primaria">
                  <div className="card-icon">🫀</div>
                  <div className="card-conteudo">
                    <div className="card-valor">{estatisticasGerais.totalOrgaosDisponiveis}</div>
                    <div className="card-label">Total de Órgãos Disponibilizados</div>
                  </div>
                </div>

                <div className="card card-sucesso">
                  <div className="card-icon">✓</div>
                  <div className="card-conteudo">
                    <div className="card-valor">{estatisticasGerais.orgaosImplantados}</div>
                    <div className="card-label">Órgãos Implantados</div>
                  </div>
                </div>

                <div className="card card-aviso">
                  <div className="card-icon">⚠</div>
                  <div className="card-conteudo">
                    <div className="card-valor">{estatisticasGerais.orgaosDescartados}</div>
                    <div className="card-label">Órgãos Descartados</div>
                  </div>
                </div>

                <div className="card card-info">
                  <div className="card-icon">👥</div>
                  <div className="card-conteudo">
                    <div className="card-valor">{estatisticasGerais.receptoresUnicos}</div>
                    <div className="card-label">Receptores Únicos</div>
                  </div>
                </div>

                <div className="card card-doador">
                  <div className="card-icon">❤️</div>
                  <div className="card-conteudo">
                    <div className="card-valor">{estatisticasGerais.totalDoadores}</div>
                    <div className="card-label">Total de Doadores</div>
                  </div>
                </div>

                <div className="card card-percentual">
                  <div className="card-icon">📊</div>
                  <div className="card-conteudo">
                    <div className="card-valor">{estatisticasGerais.taxaImplantacao.toFixed(1)}%</div>
                    <div className="card-label">Taxa de Implantação</div>
                  </div>
                </div>
              </div>

              {/* Gráficos */}
              <div className="graficos-container">
                <div className="grafico-item">
                  <h3>Distribuição de Órgãos</h3>
                  <div className="grafico-pizza">
                    <div className="legenda">
                      <div className="legenda-item">
                        <span className="cor implantado"></span>
                        <span>Implantados: {estatisticasGerais.orgaosImplantados}</span>
                      </div>
                      <div className="legenda-item">
                        <span className="cor descartado"></span>
                        <span>Descartados: {estatisticasGerais.orgaosDescartados}</span>
                      </div>
                      <div className="legenda-item">
                        <span className="cor aguardando"></span>
                        <span>Aguardando: {
                          estatisticasGerais.totalOrgaosDisponiveis - 
                          estatisticasGerais.orgaosImplantados - 
                          estatisticasGerais.orgaosDescartados
                        }</span>
                      </div>
                    </div>
                    <svg className="pizza-svg" viewBox="0 0 200 200">
                      <circle cx="100" cy="100" r="90" fill="none" strokeWidth="30" 
                        stroke="url(#gradient-pizza)" strokeDasharray={`${
                          (estatisticasGerais.orgaosImplantados / estatisticasGerais.totalOrgaosDisponiveis) * 565
                        } 565`} />
                    </svg>
                  </div>
                </div>

                <div className="grafico-item">
                  <h3>Taxa de Sucesso vs Descarte</h3>
                  <div className="barra-progresso">
                    <div className="barra-container">
                      <div
                        className="barra-sucesso"
                        style={{
                          width: `${(estatisticasGerais.orgaosImplantados / 
                                    (estatisticasGerais.orgaosImplantados + estatisticasGerais.orgaosDescartados)) * 100}%`
                        }}
                      >
                        {estatisticasGerais.orgaosImplantados} implantados
                      </div>
                      <div
                        className="barra-descarte"
                        style={{
                          width: `${(estatisticasGerais.orgaosDescartados / 
                                    (estatisticasGerais.orgaosImplantados + estatisticasGerais.orgaosDescartados)) * 100}%`
                        }}
                      >
                        {estatisticasGerais.orgaosDescartados} descartados
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          )}

          {/* ABA: POR PACIENTE */}
          {abas === 'pacientes' && (
            <div className="aba-conteudo">
              <div className="filtro-paciente">
                <input
                  type="text"
                  placeholder="🔍 Filtrar por nome do paciente..."
                  value={filtroNomePaciente}
                  onChange={(e) => setFiltroNomePaciente(e.target.value)}
                  className="input-filtro"
                />
                <span className="resultado-filtro">
                  {pacientesFiltrados.length} de {estatisticasPorPaciente.length} pacientes
                </span>
              </div>

              {pacientesFiltrados.length === 0 ? (
                <div className="sem-dados">
                  <p>Nenhum paciente encontrado para o filtro selecionado.</p>
                </div>
              ) : (
                <div className="pacientes-lista">
                  {pacientesFiltrados.map((paciente) => (
                    <details key={paciente.pacienteId} className="paciente-item">
                      <summary className="paciente-header">
                        <div className="paciente-info">
                          <strong>{paciente.nomePaciente}</strong>
                          <span className="cpf">CPF: {paciente.cpfPaciente}</span>
                          <span className="data-doacao">
                            {new Date(paciente.dataDoacao).toLocaleDateString('pt-BR')}
                          </span>
                        </div>
                        <div className="paciente-resumo">
                          <span className="badge-info">{paciente.totalOrgaos} órgãos</span>
                          <span className="badge-sucesso">{paciente.orgaosImplantados.length} implantados</span>
                          <span className="badge-aviso">{paciente.orgaosDescartados.length} descartados</span>
                        </div>
                      </summary>

                      <div className="paciente-detalhes">
                        {/* Órgãos Implantados */}
                        {paciente.orgaosImplantados.length > 0 && (
                          <div className="detalhes-secao">
                            <h4>✓ Órgãos Implantados ({paciente.orgaosImplantados.length})</h4>
                            <table className="tabela-detalhes">
                              <thead>
                                <tr>
                                  <th>Órgão</th>
                                  <th>Receptor</th>
                                  <th>CPF Receptor</th>
                                  <th>Hospital</th>
                                  <th>Data Implantação</th>
                                </tr>
                              </thead>
                              <tbody>
                                {paciente.orgaosImplantados.map((orgao, idx) => (
                                  <tr key={idx}>
                                    <td><strong>{orgao.nomeOrgao}</strong></td>
                                    <td>{orgao.nomeReceptor}</td>
                                    <td>{orgao.cpfReceptor}</td>
                                    <td>{orgao.hospitalReceptor}</td>
                                    <td>{new Date(orgao.dataImplantacao).toLocaleDateString('pt-BR')}</td>
                                  </tr>
                                ))}
                              </tbody>
                            </table>
                          </div>
                        )}

                        {/* Órgãos Descartados */}
                        {paciente.orgaosDescartados.length > 0 && (
                          <div className="detalhes-secao">
                            <h4>⚠ Órgãos Descartados ({paciente.orgaosDescartados.length})</h4>
                            <table className="tabela-detalhes">
                              <thead>
                                <tr>
                                  <th>Órgão</th>
                                  <th>Motivo</th>
                                  <th>Data Descarte</th>
                                </tr>
                              </thead>
                              <tbody>
                                {paciente.orgaosDescartados.map((orgao, idx) => (
                                  <tr key={idx}>
                                    <td><strong>{orgao.nomeOrgao}</strong></td>
                                    <td>{orgao.motivo}</td>
                                    <td>{new Date(orgao.dataDescarte).toLocaleDateString('pt-BR')}</td>
                                  </tr>
                                ))}
                              </tbody>
                            </table>
                          </div>
                        )}
                      </div>
                    </details>
                  ))}
                </div>
              )}
            </div>
          )}
        </>
      )}
    </div>
  );
};

export default EstatisticasPage;
