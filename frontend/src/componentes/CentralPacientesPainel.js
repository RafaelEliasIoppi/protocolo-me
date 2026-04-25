import {
    formatarResultadoExame,
    formatarStatusEntrevista,
    obterCidadeHospitalNotificante,
    obterCorStatus,
    obterExamesConcluidos,
    obterExamesPendentes,
    obterExamesRealizadosDetalhados,
    obterNomeHospital,
    obterResumoStatusExames,
} from "../services/centralDashboardService";
import { formatarCpf } from "../utils/cpf";

function CentralPacientesPainel({
  modoTelao,
  pacientes,
  pacientesFiltrados,
  pacientesExibidos,
  paginaAtualTelao,
  totalPaginasTelao,
  ultimaAtualizacao,
  cidadesHospitaisPorNome,
  carregando,
  abrirVisualizacaoSomenteLeitura,
  pacienteSelecionado,
  setPacienteSelecionado,
  carregandoRelatorio,
  carregarRelatorioFinalPaciente,
  relatorioFinalPaciente,
  exportarRelatorioCSV,
  exportarRelatorioPDF,
  relatorioTextoPorProtocolo,
  setRelatorioTextoPorProtocolo,
  salvarConclusaoProtocolo,
}) {
  return (
    <>
      <div className="panel pacientes-painel">
        <header className="painel-header">
          <div>
            <h2>👥 Pacientes com Protocolo de ME Iniciado ({pacientes.length})</h2>
            <p className="note">
              {ultimaAtualizacao
                ? `🔄 Atualizado em: ${ultimaAtualizacao.toLocaleTimeString("pt-BR")}`
                : "Carregando..."}
            </p>
            <p className="note central-note-readonly">Status clínico em tempo real para apoio à decisão da Central.</p>
            {modoTelao && pacientesFiltrados.length > 0 && (
              <p className="note telao-page-indicator">
                Página {paginaAtualTelao + 1}/{totalPaginasTelao} • {pacientesExibidos.length} paciente(s) nesta tela
              </p>
            )}
          </div>
        </header>

        {carregando ? (
          <p className="loading-message">⏳ Atualizando painel em tempo real...</p>
        ) : pacientesFiltrados.length === 0 ? (
          <p className="no-data-message">✓ Nenhum paciente em protocolo de ME no momento.</p>
        ) : (
          <div className="pacientes-tabela-wrapper">
            <table className="tabela-pacientes">
              <thead>
                <tr>
                  <th className="col-nome">Paciente</th>
                  <th className="col-cpf">CPF do Paciente</th>
                  <th className="col-hospital">Hospital Notificante</th>
                  <th className="col-cidade">Cidade do Hospital</th>
                  <th className="col-data">Notificação</th>
                  <th className="col-exames">Exames Concluídos</th>
                  <th className="col-status-exames">Resumo dos Exames</th>
                  <th className="col-faltantes">Pendências de Exames</th>
                  <th className="col-status">Status do Protocolo</th>
                </tr>
              </thead>
              <tbody>
                {pacientesExibidos.map((paciente) => {
                  const protocolo = paciente.protocolosME?.[0];
                  const examesPendentes = obterExamesPendentes(protocolo);
                  const examesConcluidos = obterExamesConcluidos(protocolo);
                  const resumoStatusExames = obterResumoStatusExames(protocolo);
                  return (
                    <tr
                      key={paciente.id}
                      className={`row-status-${obterCorStatus(protocolo?.status)}`}
                      onClick={() => !modoTelao && abrirVisualizacaoSomenteLeitura(paciente, protocolo)}
                      style={{ cursor: protocolo && !modoTelao ? "pointer" : "default" }}
                    >
                      <td className="col-nome" data-label="Paciente">
                        <strong>{paciente.nome}</strong>
                      </td>
                      <td className="col-cpf" data-label="CPF do Paciente">{formatarCpf(paciente.cpf)}</td>
                      <td className="col-hospital" data-label="Hospital Notificante">{obterNomeHospital(paciente, protocolo)}</td>
                      <td className="col-cidade" data-label="Cidade do Hospital">
                        {obterCidadeHospitalNotificante(paciente, protocolo, cidadesHospitaisPorNome)}
                      </td>
                      <td className="col-data" data-label="Notificação">
                        {protocolo?.dataNotificacao
                          ? new Date(protocolo.dataNotificacao).toLocaleDateString("pt-BR")
                          : "N/A"}
                      </td>
                      <td className="col-exames" data-label="Exames Concluídos">
                        <strong>{examesConcluidos}/3</strong>
                      </td>
                      <td className="col-status-exames" data-label="Resumo dos Exames">
                        <div className="status-exames-resumo">
                          <span className="badge-exame badge-exame-positivo">+ {resumoStatusExames.positivos}</span>
                          <span className="badge-exame badge-exame-negativo">- {resumoStatusExames.negativos}</span>
                          <span className="badge-exame badge-exame-pendente">⏳ {resumoStatusExames.pendentes}</span>
                        </div>
                      </td>
                      <td className="col-faltantes" data-label="Pendências de Exames">
                        {examesPendentes.length === 0 ? (
                          <span className="pendencia-ok">Todos concluídos</span>
                        ) : (
                          <ul className="lista-faltantes">
                            {examesPendentes.map((item) => (
                              <li key={`${paciente.id}-${item}`}>{item}</li>
                            ))}
                          </ul>
                        )}
                      </td>
                      <td className="col-status" data-label="Status do Protocolo">
                        <span
                          className={`status-badge status-${obterCorStatus(protocolo?.status)}`}
                        >
                          {protocolo?.status?.replace(/_/g, " ") || "SEM STATUS"}
                        </span>
                        <div className="status-subinfo">
                          Entrevista: {formatarStatusEntrevista(paciente.statusEntrevistaFamiliar)}
                        </div>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {!modoTelao && pacienteSelecionado && (
        <div className="modal-overlay" onClick={() => setPacienteSelecionado(null)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>Verificação de Exames (Somente Leitura)</h2>
              <button className="modal-close" onClick={() => setPacienteSelecionado(null)}>✕</button>
            </div>
            <div className="modal-body">
              <div className="readonly-alert">
                🔒 Perfil Central: sem permissão para alterar resultados, status ou entrevista.
              </div>

              <div className="readonly-grid">
                <div>
                  <strong>Paciente:</strong> {pacienteSelecionado.nome}
                </div>
                <div>
                  <strong>CPF:</strong> {formatarCpf(pacienteSelecionado.cpf)}
                </div>
                <div>
                  <strong>Hospital:</strong> {pacienteSelecionado.hospital}
                </div>
                <div>
                  <strong>Cidade (Hospital Notificante):</strong> {pacienteSelecionado.cidade}
                </div>
                <div>
                  <strong>Status:</strong>{" "}
                  <span className={`status-badge status-${obterCorStatus(pacienteSelecionado.protocolo?.status)}`}>
                    {pacienteSelecionado.protocolo?.status?.replace(/_/g, " ") || "SEM STATUS"}
                  </span>
                </div>
                <div>
                  <strong>Exames Concluídos:</strong> {obterExamesConcluidos(pacienteSelecionado.protocolo)}/3
                </div>
                <div>
                  <strong>Resumo da Entrevista:</strong> {formatarStatusEntrevista(pacienteSelecionado.statusEntrevistaFamiliar)}
                </div>
              </div>

              <div className="action-row" style={{ justifyContent: "flex-start", margin: "1rem 0" }}>
                <button
                  className="secondary-button"
                  onClick={() => carregarRelatorioFinalPaciente(pacienteSelecionado.id)}
                  disabled={carregandoRelatorio}
                >
                  {carregandoRelatorio ? "Gerando relatório..." : "Gerar Relatório Final"}
                </button>
              </div>

              {relatorioFinalPaciente && (
                <div className="readonly-pendencias">
                  <h3>Relatório Final do Paciente</h3>
                  <div className="action-row relatorio-actions">
                    <button className="modal-report-button" onClick={exportarRelatorioCSV}>Exportar CSV</button>
                    <button className="modal-report-button" onClick={exportarRelatorioPDF}>Exportar PDF</button>
                  </div>
                  <div className="readonly-grid">
                    <div><strong>Paciente:</strong> {relatorioFinalPaciente.nomePaciente}</div>
                    <div><strong>CPF:</strong> {formatarCpf(relatorioFinalPaciente.cpf)}</div>
                    <div><strong>Status Paciente:</strong> {relatorioFinalPaciente.statusPaciente}</div>
                    <div><strong>Status Final Protocolo:</strong> {relatorioFinalPaciente.statusFinalProtocolo}</div>
                    <div><strong>Total Protocolos:</strong> {relatorioFinalPaciente.totalProtocolos}</div>
                    <div><strong>Entrevista:</strong> {formatarStatusEntrevista(relatorioFinalPaciente.statusEntrevistaFamiliar)}</div>
                  </div>
                  <p className="note" style={{ marginTop: "0.75rem" }}>
                    <strong>Conclusão:</strong> {relatorioFinalPaciente.conclusaoFinal}
                  </p>
                  {Array.isArray(relatorioFinalPaciente.protocolos) && relatorioFinalPaciente.protocolos.length > 0 && (
                    <ul className="lista-faltantes">
                      {relatorioFinalPaciente.protocolos.map((protocoloResumo) => (
                        <li key={`relatorio-protocolo-${protocoloResumo.protocoloId}`}>
                          <strong>{protocoloResumo.numeroProtocolo || `Protocolo ${protocoloResumo.protocoloId}`}</strong>
                          {` - ${protocoloResumo.statusProtocolo}`}
                          {` | Exames: ${protocoloResumo.examesRealizados}/${protocoloResumo.totalExames}`}
                          {` | Clínicos: ${protocoloResumo.examesClinicosRealizados}`}
                          {` | Complementares: ${protocoloResumo.examesComplementaresRealizados}`}
                          {` | Laboratoriais: ${protocoloResumo.examesLaboratoriaisRealizados}`}
                          <div style={{ marginTop: "0.5rem" }}>
                            <textarea
                              value={relatorioTextoPorProtocolo[protocoloResumo.protocoloId] || ""}
                              onChange={(e) => setRelatorioTextoPorProtocolo((prev) => ({
                                ...prev,
                                [protocoloResumo.protocoloId]: e.target.value
                              }))}
                              placeholder="Conclusão final editável para este protocolo"
                              rows={3}
                              style={{ width: "100%" }}
                            />
                            <button
                              className="modal-report-button"
                              onClick={() => salvarConclusaoProtocolo(protocoloResumo.protocoloId)}
                            >
                              Salvar conclusão
                            </button>
                          </div>
                          {Array.isArray(protocoloResumo.anexos) && protocoloResumo.anexos.length > 0 && (
                            <ul className="lista-faltantes" style={{ marginTop: "0.5rem" }}>
                              {protocoloResumo.anexos.map((anexo) => (
                                <li key={`anexo-relatorio-${anexo.id}`}>
                                  {anexo.nomeArquivo} ({anexo.tipoAnexo || "ANEXO"})
                                </li>
                              ))}
                            </ul>
                          )}
                        </li>
                      ))}
                    </ul>
                  )}
                </div>
              )}

              <div className="readonly-checklist">
                <h3>Checklist de Exames</h3>
                <ul>
                  <li>
                    {pacienteSelecionado.protocolo?.testeClinico1Realizado ? "✅" : "⏳"} Teste clínico 1
                  </li>
                  <li>
                    {pacienteSelecionado.protocolo?.testeClinico2Realizado ? "✅" : "⏳"} Teste clínico 2
                  </li>
                  <li>
                    {pacienteSelecionado.protocolo?.testesComplementaresRealizados ? "✅" : "⏳"} Exames complementares
                  </li>
                </ul>
              </div>

              <div className="readonly-pendencias">
                <h3>Exames Faltantes</h3>
                {obterExamesPendentes(pacienteSelecionado.protocolo).length === 0 ? (
                  <p className="pendencia-ok">Todos concluídos.</p>
                ) : (
                  <ul className="lista-faltantes">
                    {obterExamesPendentes(pacienteSelecionado.protocolo).map((item) => (
                      <li key={`pendente-${pacienteSelecionado.id}-${item}`}>{item}</li>
                    ))}
                  </ul>
                )}
              </div>

              <div className="readonly-pendencias">
                <h3>Exames Realizados (com resultado)</h3>
                {obterExamesRealizadosDetalhados(pacienteSelecionado.protocolo).length === 0 ? (
                  <p className="note">Nenhum exame com resultado registrado até o momento.</p>
                ) : (
                  <ul className="lista-faltantes">
                    {obterExamesRealizadosDetalhados(pacienteSelecionado.protocolo).map((exame) => (
                      <li key={`realizado-${pacienteSelecionado.id}-${exame.id}`}>
                        <strong>{exame.descricao || exame.tipoExame}</strong>
                        {` - ${formatarResultadoExame(exame)}`}
                        {exame.responsavel ? ` (Resp.: ${exame.responsavel})` : ""}
                        {exame.dataRealizacao
                          ? ` - ${new Date(exame.dataRealizacao).toLocaleString("pt-BR")}`
                          : ""}
                      </li>
                    ))}
                  </ul>
                )}
              </div>
            </div>
          </div>
        </div>
      )}
    </>
  );
}

export default CentralPacientesPainel;
