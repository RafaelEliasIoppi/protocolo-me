# Guia Classe Metodo V2

## Objetivo
Este documento aprofunda o estudo em formato de consulta rapida.
Foco atual: PacienteController, PacienteService, ProtocoloMEController, ProtocoloMEService, ExameMEController, ExameMEService, MedicoProtocoloME, ExameMEManager e EntrevistaFamiliarManager.

## Como usar este guia
1. Abra a assinatura indicada no link.
2. Leia o bloco de codigo completo da funcao.
3. Volte para este guia e siga para a proxima funcao do fluxo.

---

## 1) Backend detalhado

### 1.1 PacienteController
Arquivo base: [backend/src/main/java/back/backend/controller/PacienteController.java](backend/src/main/java/back/backend/controller/PacienteController.java)

#### Criacao e leitura
- [criar](backend/src/main/java/back/backend/controller/PacienteController.java#L26)
  - Entrada: corpo Paciente.
  - Acao: chama criarPaciente no service.
  - Saida: 201 com paciente criado.
  - Erro comum: 400 quando validacao falha.

- [listarTodos](backend/src/main/java/back/backend/controller/PacienteController.java#L40)
  - Acao: lista todos os pacientes.
  - Saida: 200 com array.

- [obterPorId](backend/src/main/java/back/backend/controller/PacienteController.java#L49)
  - Rota protegida com id numerico.
  - Saida: 200 com paciente.
  - Erro comum: 404 se id inexistente.

- [obterPorCpf](backend/src/main/java/back/backend/controller/PacienteController.java#L63)
  - Busca por CPF.
  - Erro comum: 404 se nao existir.

#### Filtros
- [listarPorHospital](backend/src/main/java/back/backend/controller/PacienteController.java#L77)
  - Lista por hospitalId.

- [listarPorStatus](backend/src/main/java/back/backend/controller/PacienteController.java#L91)
  - Converte string para enum StatusPaciente.
  - Erro comum: 400 para status invalido.

- [listarPorHospitalEStatus](backend/src/main/java/back/backend/controller/PacienteController.java#L106)
  - Filtro combinado.

- [listarPacientesEmProtocoloME](backend/src/main/java/back/backend/controller/PacienteController.java#L123)
  - Retorna apenas pacientes que ja entraram no protocolo.

- [listarPacientesEmProtocoloMEPorHospital](backend/src/main/java/back/backend/controller/PacienteController.java#L132)
  - Mesmo filtro acima, restrito ao hospital.

#### Relatorio final
- [obterRelatorioFinalPaciente](backend/src/main/java/back/backend/controller/PacienteController.java#L146)
  - Rota: GET por paciente.
  - Chama gerarRelatorioFinalPaciente.

- [listarRelatoriosFinaisPacientes](backend/src/main/java/back/backend/controller/PacienteController.java#L160)
  - Rota: GET em lote.
  - Chama gerarRelatoriosFinaisPacientes.

#### Busca textual
- [buscarPorNome](backend/src/main/java/back/backend/controller/PacienteController.java#L169)
  - Usa query param nome.

- [buscarPorNomeEHospital](backend/src/main/java/back/backend/controller/PacienteController.java#L183)
  - Busca nome dentro de um hospital.

#### Atualizacao e remocao
- [atualizar](backend/src/main/java/back/backend/controller/PacienteController.java#L199)
  - Atualiza campos do paciente.

- [atualizarStatus](backend/src/main/java/back/backend/controller/PacienteController.java#L213)
  - Atualiza apenas status via PATCH.

- [deletar](backend/src/main/java/back/backend/controller/PacienteController.java#L231)
  - Remove paciente por id.

#### Estatisticas
- [obterEstatisticas](backend/src/main/java/back/backend/controller/PacienteController.java#L245)
  - Resumo quantitativo por status.

---

### 1.2 PacienteService
Arquivo base: [backend/src/main/java/back/backend/service/PacienteService.java](backend/src/main/java/back/backend/service/PacienteService.java)

#### CRUD
- [criarPaciente](backend/src/main/java/back/backend/service/PacienteService.java#L33)
  - Define status inicial como INTERNADO quando necessario.
  - Preenche hospitalOrigem automaticamente.
  - Valida regras de negocio antes de salvar.

- [atualizarPaciente](backend/src/main/java/back/backend/service/PacienteService.java#L45)
  - Atualizacao parcial campo a campo.
  - Sincroniza hospitalOrigem quando aplicavel.

- [atualizarStatus](backend/src/main/java/back/backend/service/PacienteService.java#L74)
  - Atualiza apenas status do paciente.

- [obterPacientePorId](backend/src/main/java/back/backend/service/PacienteService.java#L83)
  - Fonte unica para buscas por id com excecao padrao.

- [obterPacientePorCpf](backend/src/main/java/back/backend/service/PacienteService.java#L91)
  - Busca por CPF com excecao padrao.

- [listarTodos](backend/src/main/java/back/backend/service/PacienteService.java#L99)
- [deletarPaciente](backend/src/main/java/back/backend/service/PacienteService.java#L166)

#### Consultas de filtro
- [listarPorHospital](backend/src/main/java/back/backend/service/PacienteService.java#L106)
- [listarPorStatus](backend/src/main/java/back/backend/service/PacienteService.java#L115)
- [listarPorHospitalEStatus](backend/src/main/java/back/backend/service/PacienteService.java#L122)
- [procurarPorNome](backend/src/main/java/back/backend/service/PacienteService.java#L131)
- [procurarPorNomeEHospital](backend/src/main/java/back/backend/service/PacienteService.java#L141)
- [listarPacientesEmProtocoloME](backend/src/main/java/back/backend/service/PacienteService.java#L150)
- [listarPacientesEmProtocoloMEPorHospital](backend/src/main/java/back/backend/service/PacienteService.java#L157)

#### Estatisticas
- [obterEstatisticas](backend/src/main/java/back/backend/service/PacienteService.java#L174)
  - Conta por status e devolve DTO PacienteStatisticas.

#### Relatorio final
- [gerarRelatorioFinalPaciente](backend/src/main/java/back/backend/service/PacienteService.java#L184)
  - Carrega paciente.
  - Ordena protocolos por data.
  - Gera resumo por protocolo.
  - Conta exames realizados e pendentes por categoria.
  - Determina status final e conclusao final.

- [gerarRelatoriosFinaisPacientes](backend/src/main/java/back/backend/service/PacienteService.java#L258)
  - Loop em todos pacientes chamando metodo individual.

- [exameRealizado](backend/src/main/java/back/backend/service/PacienteService.java#L269)
  - Regra de realizacao:
  - Considera resultado texto, resultado booleano ou data de realizacao.

- [obterConclusaoFinal](backend/src/main/java/back/backend/service/PacienteService.java#L281)
  - Traduz status do protocolo para frase final de negocio.

#### Validacoes internas
- [validarPaciente](backend/src/main/java/back/backend/service/PacienteService.java#L311)
  - Campos obrigatorios.
  - Data de nascimento valida.
  - CPF unico.

- [preencherHospitalOrigemSeNecessario](backend/src/main/java/back/backend/service/PacienteService.java#L343)
  - Autocompleta hospitalOrigem para manter consistencia.

---

## 2) Frontend detalhado

### 2.1 MedicoProtocoloME
Arquivo base: [frontend/src/componentes/MedicoProtocoloME.js](frontend/src/componentes/MedicoProtocoloME.js)

- [MedicoProtocoloME](frontend/src/componentes/MedicoProtocoloME.js#L7)
  - Componente principal do fluxo medico e enfermeiro.
  - Mantem estados de lista, modal, formulario e aba ativa.

#### Mapeamento e erros
- [mapearProtocolosParaPacientes](frontend/src/componentes/MedicoProtocoloME.js#L31)
  - Deduplica por paciente usando Map.
  - Mantem protocolo mais recente por paciente.
  - Evita key duplicada no render.

- [tratarErroAutenticacaoOuPermissao](frontend/src/componentes/MedicoProtocoloME.js#L60)
  - Trata 401 com mensagem de sessao.
  - Trata 403 com mensagem de permissao.
  - Reduz erro generico e melhora depuracao de acesso.

#### Carga de dados
- [carregarPacientesProtocolo](frontend/src/componentes/MedicoProtocoloME.js#L82)
  - Busca protocolos em /api/protocolos-me.
  - Aplica deduplicacao e filtros de status.

- [atualizarPainelAposExame](frontend/src/componentes/MedicoProtocoloME.js#L96)
  - Recarrega lista completa e sincroniza protocolo selecionado.
  - Evita chamada por id que estava gerando 403 em cascata.

- [carregarPacientesDisponiveis](frontend/src/componentes/MedicoProtocoloME.js#L111)
  - Busca internados e filtra quem ainda nao tem protocolo.

#### Acao de negocio
- [iniciarProtocoloME](frontend/src/componentes/MedicoProtocoloME.js#L123)
  - Valida formulario.
  - Cria protocolo com status NOTIFICADO.
  - Recarrega listas e limpa formulario.

#### Render e experiencia
- [obterBadgeStatus](frontend/src/componentes/MedicoProtocoloME.js#L158)
  - Define cor e label por status.

- [formatarStatusEntrevista](frontend/src/componentes/MedicoProtocoloME.js#L170)
  - Traduz status tecnico para texto amigavel.

- [obterExamesRealizados](frontend/src/componentes/MedicoProtocoloME.js#L181)
  - Conta os 3 marcos principais de exame.

- [obterProximoPasso](frontend/src/componentes/MedicoProtocoloME.js#L189)
  - Regra de orientacao por status.
  - Guia operador para a acao seguinte no fluxo.

---

## 3) Fluxo ponta a ponta mais importante

### 3.1 Relatorio final
1. Frontend Central aciona endpoint de relatorio do paciente.
2. Controller chama gerarRelatorioFinalPaciente.
3. Service consolida protocolos e exames.
4. API devolve resumo final com conclusao textual.

### 3.2 Protocolo no medico
1. Tela carrega /api/protocolos-me.
2. mapearProtocolosParaPacientes deduplica por paciente.
3. Usuario abre Exames ou Entrevista no modal.
4. Ao registrar exame, atualizarPainelAposExame recarrega lista geral.

---

## 4) Roteiro de estudo de 40 minutos

1. Ler PacienteController do topo ate estatisticas.
2. Ler PacienteService com foco em gerarRelatorioFinalPaciente.
3. Ler MedicoProtocoloME inteiro e marcar as funcoes de carga.
4. Simular no browser: iniciar protocolo, registrar exame, abrir entrevista.
5. Conferir no Network cada endpoint chamado na sequencia.

---

## 5) Proxima expansao sugerida

### 5.1 ProtocoloMEController
Arquivo base: [backend/src/main/java/back/backend/controller/ProtocoloMEController.java](backend/src/main/java/back/backend/controller/ProtocoloMEController.java)

- [criarProtocolo](backend/src/main/java/back/backend/controller/ProtocoloMEController.java#L25)
  - Cria protocolo por payload e inicializa fluxo de ME.

- [listarTodos](backend/src/main/java/back/backend/controller/ProtocoloMEController.java#L53)
- [buscarPorId](backend/src/main/java/back/backend/controller/ProtocoloMEController.java#L60)
- [buscarPorNumero](backend/src/main/java/back/backend/controller/ProtocoloMEController.java#L68)
- [listarPorCentral](backend/src/main/java/back/backend/controller/ProtocoloMEController.java#L76)
- [listarPorStatus](backend/src/main/java/back/backend/controller/ProtocoloMEController.java#L87)
- [listarPorCentralEStatus](backend/src/main/java/back/backend/controller/ProtocoloMEController.java#L99)
- [listarPorPeriodo](backend/src/main/java/back/backend/controller/ProtocoloMEController.java#L113)
- [listarPorHospitalOrigem](backend/src/main/java/back/backend/controller/ProtocoloMEController.java#L122)

- [atualizarProtocolo](backend/src/main/java/back/backend/controller/ProtocoloMEController.java#L129)
  - Ajusta dados complementares do protocolo.

- [registrarTesteClinico1](backend/src/main/java/back/backend/controller/ProtocoloMEController.java#L140)
- [registrarTesteClinico2](backend/src/main/java/back/backend/controller/ProtocoloMEController.java#L151)
- [registrarTestesComplementares](backend/src/main/java/back/backend/controller/ProtocoloMEController.java#L162)
  - Endpoints de marcos que alimentam o motor de status.

- [registrarNotificacaoFamilia](backend/src/main/java/back/backend/controller/ProtocoloMEController.java#L175)
- [autorizarAutopsia](backend/src/main/java/back/backend/controller/ProtocoloMEController.java#L186)
- [registrarPreservacaoOrgaos](backend/src/main/java/back/backend/controller/ProtocoloMEController.java#L197)
- [confirmarMorteCerebral](backend/src/main/java/back/backend/controller/ProtocoloMEController.java#L208)

- [alterarStatus](backend/src/main/java/back/backend/controller/ProtocoloMEController.java#L219)
- [atualizarStatusAutomatico](backend/src/main/java/back/backend/controller/ProtocoloMEController.java#L235)
- [marcarParaEntrevista](backend/src/main/java/back/backend/controller/ProtocoloMEController.java#L246)
- [registrarResultadoEntrevista](backend/src/main/java/back/backend/controller/ProtocoloMEController.java#L257)
- [deletarProtocolo](backend/src/main/java/back/backend/controller/ProtocoloMEController.java#L271)

### 5.2 ProtocoloMEService
Arquivo base: [backend/src/main/java/back/backend/service/ProtocoloMEService.java](backend/src/main/java/back/backend/service/ProtocoloMEService.java)

#### Nucleo de sincronizacao e status
- [exameRealizado](backend/src/main/java/back/backend/service/ProtocoloMEService.java#L33)
  - Regra unica para decidir se exame conta como realizado.

- [sincronizarStatusPacienteComProtocolo](backend/src/main/java/back/backend/service/ProtocoloMEService.java#L45)
  - Espelha o status final do protocolo no paciente.

- [sincronizarEntrevistaPaciente](backend/src/main/java/back/backend/service/ProtocoloMEService.java#L85)
  - Replica status e observacoes da entrevista no paciente.

#### Criacao e estrutura inicial
- [criarProtocolo](backend/src/main/java/back/backend/service/ProtocoloMEService.java#L104)
- [criarProtocoloPorPacienteId](backend/src/main/java/back/backend/service/ProtocoloMEService.java#L158)
- [gerarNumeroProtocolo](backend/src/main/java/back/backend/service/ProtocoloMEService.java#L182)
- [preencherExamesAutomaticamente](backend/src/main/java/back/backend/service/ProtocoloMEService.java#L198)

#### Consultas
- [listarTodos](backend/src/main/java/back/backend/service/ProtocoloMEService.java#L261)
- [buscarPorId](backend/src/main/java/back/backend/service/ProtocoloMEService.java#L266)
- [buscarPorNumeroProtocolo](backend/src/main/java/back/backend/service/ProtocoloMEService.java#L271)
- [listarPorCentral](backend/src/main/java/back/backend/service/ProtocoloMEService.java#L276)
- [listarPorStatus](backend/src/main/java/back/backend/service/ProtocoloMEService.java#L283)
- [listarPorCentralEStatus](backend/src/main/java/back/backend/service/ProtocoloMEService.java#L288)
- [listarPorPeriodo](backend/src/main/java/back/backend/service/ProtocoloMEService.java#L295)
- [listarPorHospitalOrigem](backend/src/main/java/back/backend/service/ProtocoloMEService.java#L300)

#### Etapas do protocolo
- [atualizarProtocolo](backend/src/main/java/back/backend/service/ProtocoloMEService.java#L305)
- [registrarTesteClinico1](backend/src/main/java/back/backend/service/ProtocoloMEService.java#L320)
- [registrarTesteClinico2](backend/src/main/java/back/backend/service/ProtocoloMEService.java#L329)
- [registrarTestesComplementares](backend/src/main/java/back/backend/service/ProtocoloMEService.java#L338)
- [registrarNotificacaoFamilia](backend/src/main/java/back/backend/service/ProtocoloMEService.java#L348)
- [autorizarAutopsia](backend/src/main/java/back/backend/service/ProtocoloMEService.java#L357)
- [registrarPreservacaoOrgaos](backend/src/main/java/back/backend/service/ProtocoloMEService.java#L365)
- [confirmarMorteCerebral](backend/src/main/java/back/backend/service/ProtocoloMEService.java#L374)
- [atualizarStatusAutomatico](backend/src/main/java/back/backend/service/ProtocoloMEService.java#L386)
- [marcarParaEntrevista](backend/src/main/java/back/backend/service/ProtocoloMEService.java#L424)
- [registrarResultadoEntrevista](backend/src/main/java/back/backend/service/ProtocoloMEService.java#L442)
- [alterarStatus](backend/src/main/java/back/backend/service/ProtocoloMEService.java#L472)
- [deletarProtocolo](backend/src/main/java/back/backend/service/ProtocoloMEService.java#L482)

### 5.3 ExameMEController
Arquivo base: [backend/src/main/java/back/backend/controller/ExameMEController.java](backend/src/main/java/back/backend/controller/ExameMEController.java)

- [criarExame](backend/src/main/java/back/backend/controller/ExameMEController.java#L22)
- [listarExamePorProtocolo](backend/src/main/java/back/backend/controller/ExameMEController.java#L33)
- [listarExamesClinico](backend/src/main/java/back/backend/controller/ExameMEController.java#L44)
- [listarExamesComplementares](backend/src/main/java/back/backend/controller/ExameMEController.java#L55)
- [listarExamesLaboratoriais](backend/src/main/java/back/backend/controller/ExameMEController.java#L66)
- [buscarPorId](backend/src/main/java/back/backend/controller/ExameMEController.java#L77)
- [atualizarExame](backend/src/main/java/back/backend/controller/ExameMEController.java#L85)
- [registrarResultado](backend/src/main/java/back/backend/controller/ExameMEController.java#L96)
- [obterResumo](backend/src/main/java/back/backend/controller/ExameMEController.java#L111)
- [deletarExame](backend/src/main/java/back/backend/controller/ExameMEController.java#L122)
- [criarExameIncremental](backend/src/main/java/back/backend/controller/ExameMEController.java#L133)

### 5.4 ExameMEService
Arquivo base: [backend/src/main/java/back/backend/service/ExameMEService.java](backend/src/main/java/back/backend/service/ExameMEService.java)

- [exameRealizado](backend/src/main/java/back/backend/service/ExameMEService.java#L25)
  - Criterio uniforme para considerar exame completo.

- [criarExame](backend/src/main/java/back/backend/service/ExameMEService.java#L38)
  - Impede duplicidade por tipo no mesmo protocolo.

- [listarExamesPorProtocolo](backend/src/main/java/back/backend/service/ExameMEService.java#L77)
- [listarExamePorCategoria](backend/src/main/java/back/backend/service/ExameMEService.java#L84)
- [listarExamesClinico](backend/src/main/java/back/backend/service/ExameMEService.java#L91)
- [listarExamesComplementares](backend/src/main/java/back/backend/service/ExameMEService.java#L96)
- [listarExamesLaboratoriais](backend/src/main/java/back/backend/service/ExameMEService.java#L101)
- [buscarPorId](backend/src/main/java/back/backend/service/ExameMEService.java#L106)
- [atualizarExame](backend/src/main/java/back/backend/service/ExameMEService.java#L111)
- [registrarResultado](backend/src/main/java/back/backend/service/ExameMEService.java#L128)
  - Atualiza resultado e aciona recalculo de status do protocolo.

- [deletarExame](backend/src/main/java/back/backend/service/ExameMEService.java#L152)
- [obterResumoExames](backend/src/main/java/back/backend/service/ExameMEService.java#L160)

### 5.5 ExameMEManager
Arquivo base: [frontend/src/componentes/ExameMEManager.js](frontend/src/componentes/ExameMEManager.js)

- [ExameMEManager](frontend/src/componentes/ExameMEManager.js#L6)
  - Componente de criacao, resultado, filtro e exclusao de exames.

- [carregarExames](frontend/src/componentes/ExameMEManager.js#L89)
- [carregarResumo](frontend/src/componentes/ExameMEManager.js#L101)
- [filtrarExames](frontend/src/componentes/ExameMEManager.js#L110)
- [handleChangeForm](frontend/src/componentes/ExameMEManager.js#L118)
- [handleCriarExame](frontend/src/componentes/ExameMEManager.js#L126)
  - Nao permite criar novamente tipo de exame ja existente.

- [registrarResultado](frontend/src/componentes/ExameMEManager.js#L172)
  - Envia resultado via endpoint dedicado por exame.

- [deletarExame](frontend/src/componentes/ExameMEManager.js#L201)
- [getCategoriaLabel](frontend/src/componentes/ExameMEManager.js#L217)
- [getCorCategoria](frontend/src/componentes/ExameMEManager.js#L222)
- [getTipoLabel](frontend/src/componentes/ExameMEManager.js#L227)
- [isExameRealizado](frontend/src/componentes/ExameMEManager.js#L232)
- [isExameQueImpactaStatus](frontend/src/componentes/ExameMEManager.js#L240)

### 5.6 EntrevistaFamiliarManager
Arquivo base: [frontend/src/componentes/EntrevistaFamiliarManager.js](frontend/src/componentes/EntrevistaFamiliarManager.js)

- [EntrevistaFamiliarManager](frontend/src/componentes/EntrevistaFamiliarManager.js#L6)
  - Gerencia etapa final da entrevista familiar dentro do protocolo.

- [carregarProtocolo](frontend/src/componentes/EntrevistaFamiliarManager.js#L22)
- [handleInputChange](frontend/src/componentes/EntrevistaFamiliarManager.js#L39)
- [marcarParaEntrevista](frontend/src/componentes/EntrevistaFamiliarManager.js#L47)
- [salvarResultadoEntrevista](frontend/src/componentes/EntrevistaFamiliarManager.js#L62)

## 6) Expansao Central e Painel

### 6.1 CentralDashboardPage
Arquivo base: [frontend/src/componentes/CentralDashboardPage.js](frontend/src/componentes/CentralDashboardPage.js)

- [CentralDashboardPage](frontend/src/componentes/CentralDashboardPage.js#L5)
  - Painel de monitoramento da Central em modo somente leitura.

- [obterNomeHospital](frontend/src/componentes/CentralDashboardPage.js#L25)
  - Resolve nome de hospital em cascata para evitar campo vazio.

- [mapearProtocolosParaPacientes](frontend/src/componentes/CentralDashboardPage.js#L35)
  - Converte lista de protocolos em cards por paciente no painel.

- [carregarPacientesDoEstado](frontend/src/componentes/CentralDashboardPage.js#L52)
  - Busca /api/protocolos-me e atualiza painel com polling.
  - Trata 403 como acesso de leitura sem permissão de escrita.

- [obterExamesPendentes](frontend/src/componentes/CentralDashboardPage.js#L78)
- [obterExamesConcluidos](frontend/src/componentes/CentralDashboardPage.js#L107)
  - Calculam progresso com regra alinhada ao backend.

- [obterExamesRealizadosDetalhados](frontend/src/componentes/CentralDashboardPage.js#L133)
- [formatarResultadoExame](frontend/src/componentes/CentralDashboardPage.js#L146)
  - Montam visualização detalhada dos exames já executados.

- [obterCorStatus](frontend/src/componentes/CentralDashboardPage.js#L162)
- [formatarStatusEntrevista](frontend/src/componentes/CentralDashboardPage.js#L181)
  - Traduzem estado técnico para visual de operação.

- [abrirVisualizacaoSomenteLeitura](frontend/src/componentes/CentralDashboardPage.js#L192)
  - Abre modal com dados do paciente e protocolo em modo de auditoria.

- [carregarRelatorioFinalPaciente](frontend/src/componentes/CentralDashboardPage.js#L206)
  - Busca relatório final por paciente.

- [gerarNomeArquivoRelatorio](frontend/src/componentes/CentralDashboardPage.js#L219)
- [baixarBlob](frontend/src/componentes/CentralDashboardPage.js#L225)
- [exportarRelatorioCSV](frontend/src/componentes/CentralDashboardPage.js#L237)
- [exportarRelatorioPDF](frontend/src/componentes/CentralDashboardPage.js#L296)
  - Exportação de relatório para CSV e impressão em formato PDF.

### 6.2 PacientesProtocoloMEPage
Arquivo base: [frontend/src/componentes/PacientesProtocoloMEPage.js](frontend/src/componentes/PacientesProtocoloMEPage.js)

- [PacientesProtocoloMEPage](frontend/src/componentes/PacientesProtocoloMEPage.js#L5)
  - Tela de listagem de pacientes que já estão em protocolo ME.

- [carregarPacientesProtocoloME](frontend/src/componentes/PacientesProtocoloMEPage.js#L13)
  - Busca lista geral ou filtrada por hospital.

- [carregarHospitais](frontend/src/componentes/PacientesProtocoloMEPage.js#L32)
  - Carrega opções para filtro de hospital.

- [handleFiltroHospitalChange](frontend/src/componentes/PacientesProtocoloMEPage.js#L48)
  - Reexecuta busca conforme seleção de hospital.

- [formatarStatusEntrevista](frontend/src/componentes/PacientesProtocoloMEPage.js#L58)
  - Exibe status da entrevista em linguagem operacional.

### 6.3 CentralTransplantesController
Arquivo base: [backend/src/main/java/back/backend/controller/CentralTransplantesController.java](backend/src/main/java/back/backend/controller/CentralTransplantesController.java)

- [criarCentral](backend/src/main/java/back/backend/controller/CentralTransplantesController.java#L22)
- [listarTodas](backend/src/main/java/back/backend/controller/CentralTransplantesController.java#L33)
- [buscarPorId](backend/src/main/java/back/backend/controller/CentralTransplantesController.java#L40)
- [buscarPorCnpj](backend/src/main/java/back/backend/controller/CentralTransplantesController.java#L48)
- [buscarPorNome](backend/src/main/java/back/backend/controller/CentralTransplantesController.java#L56)
- [listarPorCidade](backend/src/main/java/back/backend/controller/CentralTransplantesController.java#L64)
- [listarPorEstado](backend/src/main/java/back/backend/controller/CentralTransplantesController.java#L71)
- [listarPorStatus](backend/src/main/java/back/backend/controller/CentralTransplantesController.java#L78)
- [atualizarCentral](backend/src/main/java/back/backend/controller/CentralTransplantesController.java#L90)
- [alterarStatus](backend/src/main/java/back/backend/controller/CentralTransplantesController.java#L101)
- [vincularHospital](backend/src/main/java/back/backend/controller/CentralTransplantesController.java#L117)
- [removerHospital](backend/src/main/java/back/backend/controller/CentralTransplantesController.java#L130)
- [deletarCentral](backend/src/main/java/back/backend/controller/CentralTransplantesController.java#L143)

### 6.4 CentralTransplantesService
Arquivo base: [backend/src/main/java/back/backend/service/CentralTransplantesService.java](backend/src/main/java/back/backend/service/CentralTransplantesService.java)

- [criarCentral](backend/src/main/java/back/backend/service/CentralTransplantesService.java#L22)
  - Impede central duplicada por CNPJ.

- [listarTodas](backend/src/main/java/back/backend/service/CentralTransplantesService.java#L30)
- [buscarPorId](backend/src/main/java/back/backend/service/CentralTransplantesService.java#L35)
- [buscarPorCnpj](backend/src/main/java/back/backend/service/CentralTransplantesService.java#L40)
- [buscarPorNome](backend/src/main/java/back/backend/service/CentralTransplantesService.java#L45)
- [listarPorCidade](backend/src/main/java/back/backend/service/CentralTransplantesService.java#L50)
- [listarPorEstado](backend/src/main/java/back/backend/service/CentralTransplantesService.java#L55)
- [listarPorStatus](backend/src/main/java/back/backend/service/CentralTransplantesService.java#L60)

- [atualizarCentral](backend/src/main/java/back/backend/service/CentralTransplantesService.java#L65)
- [alterarStatus](backend/src/main/java/back/backend/service/CentralTransplantesService.java#L86)
- [vincularHospital](backend/src/main/java/back/backend/service/CentralTransplantesService.java#L95)
- [removerHospital](backend/src/main/java/back/backend/service/CentralTransplantesService.java#L113)
- [deletarCentral](backend/src/main/java/back/backend/service/CentralTransplantesService.java#L127)

## 7) Expansao Autenticacao e Seguranca

### 7.1 SecurityConfig
Arquivo base: [backend/src/main/java/back/backend/security/SecurityConfig.java](backend/src/main/java/back/backend/security/SecurityConfig.java)

- [configure](backend/src/main/java/back/backend/security/SecurityConfig.java#L28)
  - Define a matriz de permissões por role e método HTTP.
  - Marca API como stateless e injeta JwtFilter antes do UsernamePasswordAuthenticationFilter.
  - Ponto central para investigar 401 e 403 por rota.

- [corsConfigurationSource](backend/src/main/java/back/backend/security/SecurityConfig.java#L76)
  - Configura CORS para métodos e headers aceitos.
  - Mantém allowCredentials false quando origem está em curinga.

### 7.2 JwtFilter
Arquivo base: [backend/src/main/java/back/backend/security/JwtFilter.java](backend/src/main/java/back/backend/security/JwtFilter.java)

- [doFilterInternal](backend/src/main/java/back/backend/security/JwtFilter.java#L29)
  - Lê header Authorization no formato Bearer.
  - Extrai usuário pelo token e valida assinatura/expiração.
  - Popula SecurityContext para liberar autorização por role nas rotas.

### 7.3 api.js
Arquivo base: [frontend/src/api/api.js](frontend/src/api/api.js)

- [criação do client axios](frontend/src/api/api.js#L6)
  - Define baseURL e timeout padrão da aplicação.

- [interceptor de request](frontend/src/api/api.js#L11)
  - Injeta token JWT do localStorage no header Authorization.

- [interceptor de response](frontend/src/api/api.js#L22)
  - Ao receber 401 (exceto login), limpa sessão e redireciona para /login.

### 7.4 apiClient.js
Arquivo base: [frontend/src/services/apiClient.js](frontend/src/services/apiClient.js)

- [import do client compartilhado](frontend/src/services/apiClient.js#L1)
- [export default api](frontend/src/services/apiClient.js#L3)
  - Camada de alias única para consumo do client configurado.

### 7.5 login.js
Arquivo base: [frontend/src/componentes/login.js](frontend/src/componentes/login.js)

- [Login](frontend/src/componentes/login.js#L4)
  - Componente de entrada com login, cadastro restrito e cadastro do primeiro admin.

- [validarEmail](frontend/src/componentes/login.js#L15)
  - Validação básica de formato antes de enviar request.

- [handleSubmit](frontend/src/componentes/login.js#L20)
  - Orquestra três fluxos:
  - cadastro do primeiro administrador,
  - cadastro restrito (médico/enfermeiro),
  - login com persistência da sessão via serviço de autenticação.

## 8) Autenticacao de Usuario

### 8.1 JwtUtil
Arquivo base: [backend/src/main/java/back/backend/security/JwtUtil.java](backend/src/main/java/back/backend/security/JwtUtil.java)

- [generateToken](backend/src/main/java/back/backend/security/JwtUtil.java#L25)
  - Gera token para UserDetails com subject = username.

- [gerarToken](backend/src/main/java/back/backend/security/JwtUtil.java#L30)
  - Gera token do login contendo role nas claims.

- [createToken](backend/src/main/java/back/backend/security/JwtUtil.java#L35)
  - Centraliza criação do JWT com expiração de 10 horas.

- [validateToken](backend/src/main/java/back/backend/security/JwtUtil.java#L42)
- [isTokenValid](backend/src/main/java/back/backend/security/JwtUtil.java#L47)
- [extractUsername](backend/src/main/java/back/backend/security/JwtUtil.java#L55)
- [extractExpiration](backend/src/main/java/back/backend/security/JwtUtil.java#L59)
- [extractClaim](backend/src/main/java/back/backend/security/JwtUtil.java#L63)
- [extractAllClaims](backend/src/main/java/back/backend/security/JwtUtil.java#L67)
- [isTokenExpired](backend/src/main/java/back/backend/security/JwtUtil.java#L71)

### 8.2 UsuarioController
Arquivo base: [backend/src/main/java/back/backend/controller/UsuarioController.java](backend/src/main/java/back/backend/controller/UsuarioController.java)

 Cadastro restrito a MEDICO e ENFERMEIRO.

- [registrarAdministrador](backend/src/main/java/back/backend/controller/UsuarioController.java#L54)
  - Trata o primeiro ADMIN e o cadastro administrativo posterior.
  - Exige autenticação como admin quando já existir pelo menos um administrador.

- [login](backend/src/main/java/back/backend/controller/UsuarioController.java#L84)
  - Valida email e senha.
  - Gera token com role e devolve payload do usuário.

- [obterUsuario](backend/src/main/java/back/backend/controller/UsuarioController.java#L110)
- [atualizarUsuario](backend/src/main/java/back/backend/controller/UsuarioController.java#L120)
- [deletarUsuario](backend/src/main/java/back/backend/controller/UsuarioController.java#L147)

### 8.3 UsuarioService
Arquivo base: [backend/src/main/java/back/backend/service/UsuarioService.java](backend/src/main/java/back/backend/service/UsuarioService.java)

- [loadUserByUsername](backend/src/main/java/back/backend/service/UsuarioService.java#L25)
  - Integra Spring Security com email como username.
  - Converte Role do domínio em authorities.

- [registrar](backend/src/main/java/back/backend/service/UsuarioService.java#L35)
  - Impede email duplicado.
  - Codifica senha e registra timestamps.

- [countUsuarios](backend/src/main/java/back/backend/service/UsuarioService.java#L45)
- [countAdmins](backend/src/main/java/back/backend/service/UsuarioService.java#L49)
- [findByEmail](backend/src/main/java/back/backend/service/UsuarioService.java#L53)
- [findById](backend/src/main/java/back/backend/service/UsuarioService.java#L57)
- [atualizar](backend/src/main/java/back/backend/service/UsuarioService.java#L61)
- [deletar](backend/src/main/java/back/backend/service/UsuarioService.java#L66)

### 8.4 autenticarService
Arquivo base: [frontend/src/services/autenticarService.js](frontend/src/services/autenticarService.js)

- [login](frontend/src/services/autenticarService.js#L4)
  - Faz POST no backend e salva token e usuario no localStorage.

- [registrar](frontend/src/services/autenticarService.js#L12)
- [registrarAdmin](frontend/src/services/autenticarService.js#L17)
- [obterUsuarioAtual](frontend/src/services/autenticarService.js#L22)
- [logout](frontend/src/services/autenticarService.js#L32)
- [isAutenticado](frontend/src/services/autenticarService.js#L37)

### 8.5 AppLayout
Arquivo base: [frontend/src/componentes/AppLayout.js](frontend/src/componentes/AppLayout.js)

- [AppLayout](frontend/src/componentes/AppLayout.js#L4)
  - Define o shell principal com sidebar e area de conteudo.
  - Mostra links conforme role do usuario.
  - Aplica navegação separada para Central, Médico, Enfermeiro e Admin.

## 9) Proxima expansao sugerida

Para manter o mesmo nivel deste guia, os proximos arquivos recomendados sao:
- [backend/src/main/java/back/backend/model/Usuario.java](backend/src/main/java/back/backend/model/Usuario.java)
- [backend/src/main/java/back/backend/model/Role.java](backend/src/main/java/back/backend/model/Role.java)
- [frontend/src/App.js](frontend/src/App.js)
- [frontend/src/routes.js](frontend/src/routes.js)
- [frontend/src/services/hospitalService.js](frontend/src/services/hospitalService.js)
