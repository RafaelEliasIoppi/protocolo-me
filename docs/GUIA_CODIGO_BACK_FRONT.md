# Guia do Codigo - Backend e Frontend

## Objetivo
Este guia foi montado para voce navegar no projeto sem depender de ajuda externa toda hora.
A estrutura esta dividida por backend e frontend, com foco em classes, componentes e metodos que realmente movem o sistema.

## Versao aprofundada
Para leitura classe a classe e metodo a metodo, consulte: [GUIA_CLASSE_METODO_V2.md](GUIA_CLASSE_METODO_V2.md)

## Mapa visual
Para ver o fluxo como diagrama, consulte: [MAPA_VISUAL_DO_SISTEMA.md](MAPA_VISUAL_DO_SISTEMA.md)

## Fluxo do medico
Para acompanhar o medico em formato guiado e sequencial, consulte: [FLUXO_GUIADO_MEDICO.md](FLUXO_GUIADO_MEDICO.md)

## Checklist interativa
Para praticar marcando etapa por etapa, consulte: [CHECKLIST_INTERATIVO_MEDICO.md](CHECKLIST_INTERATIVO_MEDICO.md)

## Manual Completo de Paciente

Se você quer entender **o caminho inteiro** de salvar um paciente, este guia reúne o fluxo do front ao backend com exemplos reais.

### 1) Frontend: onde o fluxo começa

- Arquivo principal: [frontend/src/componentes/PacienteForm.js](../frontend/src/componentes/PacienteForm.js)
- Função principal: `salvarPaciente()`
- O que ela faz:
  - lê os campos do formulário
  - normaliza CPF para enviar apenas dígitos
  - converte datas vazias para `null`
  - decide entre criar ou atualizar
  - chama o serviço HTTP

#### Exemplo de payload enviado pelo frontend

```json
{
  "nome": "rafael elias",
  "cpf": "33333333333",
  "dataNascimento": "1980-11-02",
  "genero": "MASCULINO",
  "hospitalId": "1",
  "leito": "100",
  "dataInternacao": "2026-04-09",
  "diagnosticoPrincipal": "avc",
  "historicoMedico": "teste",
  "nomeResponsavel": "",
  "telefoneResponsavel": "",
  "emailResponsavel": "",
  "status": "INTERNADO",
  "statusEntrevistaFamiliar": "",
  "observacoesEntrevistaFamiliar": "",
  "dataEntrevistaFamiliar": null,
  "hospital": { "id": 1 }
}
```

#### Exemplo do trecho que prepara o envio

```javascript
const dadosPaciente = {
  ...formData,
  cpf: formData.cpf.replace(/\D/g, ''),
  dataNascimento: valorOuNull(formData.dataNascimento),
  dataInternacao: valorOuNull(formData.dataInternacao),
  dataEntrevistaFamiliar: valorOuNull(formData.dataEntrevistaFamiliar),
  status: editandoId ? formData.status : 'INTERNADO',
  hospital: { id: parseInt(formData.hospitalId) }
};
```

### 2) Serviço HTTP: a ponte entre frontend e backend

- Arquivo: [frontend/src/services/pacienteService.js](../frontend/src/services/pacienteService.js)
- O serviço traduz a ação do formulário em requisição REST.

#### Exemplos de chamadas

```javascript
await pacienteService.criar(dadosPaciente);      // POST /api/pacientes
await pacienteService.atualizar(id, dadosPaciente); // PUT /api/pacientes/{id}
await pacienteService.listar();                 // GET /api/pacientes
await pacienteService.obter(id);                // GET /api/pacientes/{id}
await pacienteService.deletar(id);              // DELETE /api/pacientes/{id}
```

### 3) Backend: controller que recebe a requisição

- Arquivo: [backend/src/main/java/back/backend/controller/PacienteController.java](../backend/src/main/java/back/backend/controller/PacienteController.java)
- Endpoints principais:
  - `POST /api/pacientes` → `criar()`
  - `PUT /api/pacientes/{id}` → `atualizar()`
  - `GET /api/pacientes` → `listarTodos()`
  - `GET /api/pacientes/{id}` → `obterPorId()`
  - `GET /api/pacientes/cpf/{cpf}` → `obterPorCpf()`
  - `DELETE /api/pacientes/{id}` → `deletar()`
  - `PATCH /api/pacientes/{id}/status` → `atualizarStatus()`
  - `GET /api/pacientes/estatisticas/resumo` → `obterEstatisticas()`

#### Exemplo de criação

```java
@Transactional
@PostMapping
public ResponseEntity<PacienteDTO> criar(@Valid @RequestBody PacienteRequestDTO request) {
    return ResponseEntity.status(201).body(
        pacienteService.criarPaciente(pacienteRequestMapper.toEntity(request))
    );
}
```

### 4) Mapper: converte DTO em entidade

- Arquivo: [backend/src/main/java/back/backend/mapper/PacienteRequestMapper.java](../backend/src/main/java/back/backend/mapper/PacienteRequestMapper.java)
- Responsabilidade:
  - transformar `PacienteRequestDTO` em `Paciente`
  - converter strings como `genero` e `status` para enums
  - montar o objeto `Hospital` usando `hospitalId`

#### Exemplo do que ele faz

```java
@Mapping(target = "hospital", expression = "java(toHospital(dto.getHospitalId()))")
@Mapping(target = "status", expression = "java(toStatus(dto.getStatus()))")
Paciente toEntity(PacienteRequestDTO dto);
```

### 5) Service: regra de negócio e validação

- Arquivo: [backend/src/main/java/back/backend/service/PacienteService.java](../backend/src/main/java/back/backend/service/PacienteService.java)
- O que acontece antes de salvar:
  - CPF é normalizado
  - status padrão é definido
  - hospital é validado
  - paciente é persistido

#### Exemplo do fluxo de criação

```java
public PacienteDTO criarPaciente(Paciente paciente) {
    paciente.setCpf(normalizarCpf(paciente.getCpf()));

    if (paciente.getStatus() == null ||
        paciente.getStatus() == Paciente.StatusPaciente.EM_PROTOCOLO_ME) {
        paciente.setStatus(Paciente.StatusPaciente.INTERNADO);
    }

    preencherHospital(paciente);
    validarPaciente(paciente);

    return toDTO(pacienteRepository.save(paciente));
}
```

#### Exemplo da normalização de CPF

```java
private String normalizarCpf(String cpf) {
    if (cpf == null || cpf.isBlank()) {
        throw new IllegalArgumentException("CPF obrigatório");
    }

    String n = cpf.replaceAll("\\D", "");

    if (n.length() != 11) {
        throw new IllegalArgumentException("CPF inválido");
    }

    return n;
}
```

### 6) Exemplo prático de erro e correção

#### Antes da correção

```json
{
  "cpf": "123.456.789-10"
}
```

#### Depois da correção

```json
{
  "cpf": "12345678910"
}
```

#### O que isso evita

- `ConstraintViolationException`
- HTTP 500 ao salvar paciente
- CPF com tamanho diferente de 11 no banco

### 7) Quando usar este manual

- quando o formulário de paciente não salva
- quando o backend retorna 500 ao criar ou editar paciente
- quando você quer ver o caminho completo front → service → controller → mapper → service → banco

---

## Fluxos Detalhados com Código Comentado

Para aprofundar em outros fluxos do sistema, use os guias gerais abaixo:

- [RELATORIO_AUDITORIA_TECNICA_BACKEND.md](RELATORIO_AUDITORIA_TECNICA_BACKEND.md): arquitetura completa e riscos técnicos.
- [GUIA_CLASSE_METODO_V2.md](GUIA_CLASSE_METODO_V2.md): leitura classe a classe e método a método.
- [MAPA_VISUAL_DO_SISTEMA.md](MAPA_VISUAL_DO_SISTEMA.md): visão em diagramas.

---

## 1) Backend (Spring Boot)

### 1.1 Ponto de entrada
- Arquivo: backend/src/main/java/back/ProtocoloMeApplication.java
- Responsabilidade:
  - Sobe a aplicacao Spring Boot.

### 1.2 Controllers (API HTTP)

#### AnexoDocumentoController
- Arquivo: backend/src/main/java/back/backend/controller/AnexoDocumentoController.java
- Metodos principais:
  - uploadAnexoExame
  - uploadAnexoEntrevista
  - listarAnexosExame
  - listarAnexosEntrevista
  - obterAnexo
  - downloadArquivo
  - deletarAnexo
  - limparAnexosExame
- Quando usar:
  - Fluxo de anexos do exame e entrevista.

#### CentralTransplantesController
- Arquivo: backend/src/main/java/back/backend/controller/CentralTransplantesController.java
- Metodos principais:
  - criarCentral
  - listarTodas
  - buscarPorId
  - buscarPorCnpj
  - buscarPorNome
  - listarPorCidade
  - listarPorEstado
  - listarPorStatus
  - atualizarCentral
  - alterarStatus
  - vincularHospital
  - removerHospital
  - deletarCentral
- Quando usar:
  - CRUD da central e relacionamento com hospitais.

#### ExameMEController
- Arquivo: backend/src/main/java/back/backend/controller/ExameMEController.java
- Metodos principais:
  - criarExame
  - listarExamePorProtocolo
  - listarExamesClinico
  - listarExamesComplementares
  - listarExamesLaboratoriais
  - buscarPorId
  - atualizarExame
  - registrarResultado
  - obterResumo
  - deletarExame
  - criarExameIncremental
- Quando usar:
  - Toda operacao de exame no protocolo de ME.

#### HospitalController
- Arquivo: backend/src/main/java/back/backend/controller/HospitalController.java
- Metodos principais:
  - criarHospital
  - listarTodos
  - buscarPorId
  - buscarPorCnpj
  - listarPorStatus
  - listarPorCidade
  - listarPorEstado
  - atualizarHospital
  - alterarStatus
  - deletarHospital

#### PacienteController
- Arquivo: backend/src/main/java/back/backend/controller/PacienteController.java
- Metodos principais:
  - criar
  - listarTodos
  - obterPorId
  - obterPorCpf
  - listarPorHospital
  - listarPorStatus
  - listarPorHospitalEStatus
  - listarPacientesEmProtocoloME
  - listarPacientesEmProtocoloMEPorHospital
  - obterRelatorioFinalPaciente
  - listarRelatoriosFinaisPacientes
  - buscarPorNome
  - buscarPorNomeEHospital
  - atualizar
  - atualizarStatus
  - deletar
  - obterEstatisticas
- Destaque:
  - Endpoint de relatorio final por paciente e em lote.

#### ProtocoloMEController
- Arquivo: backend/src/main/java/back/backend/controller/ProtocoloMEController.java
- Metodos principais:
  - criarProtocolo
  - listarTodos
  - buscarPorId
  - buscarPorNumero
  - listarPorCentral
  - listarPorStatus
  - listarPorCentralEStatus
  - listarPorPeriodo
  - listarPorHospitalOrigem
  - atualizarProtocolo
  - registrarTesteClinico1
  - registrarTesteClinico2
  - registrarTestesComplementares
  - registrarNotificacaoFamilia
  - autorizarAutopsia
  - registrarPreservacaoOrgaos
  - confirmarMorteCerebral
  - alterarStatus
  - atualizarStatusAutomatico
  - marcarParaEntrevista
  - registrarResultadoEntrevista
  - deletarProtocolo
- Destaque:
  - Centro da regra de status do protocolo e entrevista familiar.

#### UsuarioController
- Arquivo: backend/src/main/java/back/backend/controller/UsuarioController.java
- Metodos principais:
  - registrar
  - registrarAdministrador
  - login
  - obterUsuario
  - atualizarUsuario
  - deletarUsuario

#### GlobalExceptionHandler
- Arquivo: backend/src/main/java/back/backend/controller/GlobalExceptionHandler.java
- Metodos principais:
  - handleValidationExceptions
  - handleRuntimeException
- Quando usar:
  - Padronizar erro para API.

### 1.3 Services (Regras de negocio)

#### UsuarioService
- Arquivo: backend/src/main/java/back/backend/service/UsuarioService.java
- Papel:
  - Registro, busca, atualizacao e remocao de usuario.
  - Regras administrativas.

#### HospitalService
- Arquivo: backend/src/main/java/back/backend/service/HospitalService.java
- Papel:
  - Regras de CRUD e status de hospital.

#### CentralTransplantesService
- Arquivo: backend/src/main/java/back/backend/service/CentralTransplantesService.java
- Papel:
  - Regras de central e vinculo com hospitais.

#### PacienteService
- Arquivo: backend/src/main/java/back/backend/service/PacienteService.java
- Papel:
  - CRUD de paciente e filtros.
  - Estatisticas.
  - Relatorio final por paciente e em lote.
- Estruturas importantes:
  - RelatorioFinalPaciente
  - ResumoProtocoloRelatorio

#### ExameMEService
- Arquivo: backend/src/main/java/back/backend/service/ExameMEService.java
- Metodos criticos:
  - criarExame
  - listarExamesPorProtocolo
  - registrarResultado
  - atualizarExame
  - obterResumoExames
  - deletarExame
- Regras criticas:
  - Evitar duplicidade de exame por tipo dentro do mesmo protocolo.
  - Bloquear re-registro indevido de exame ja realizado.
  - Acionar atualizacao automatica do status do protocolo.

#### ProtocoloMEService
- Arquivo: backend/src/main/java/back/backend/service/ProtocoloMEService.java
- Metodos criticos:
  - criarProtocolo
  - criarProtocoloPorPacienteId
  - atualizarStatusAutomatico
  - marcarParaEntrevista
  - registrarResultadoEntrevista
  - alterarStatus
  - deletarProtocolo
- Regras criticas:
  - Protocolo nasce em NOTIFICADO.
  - Status sobe automaticamente por exames.
  - Entrevista fecha em DOACAO_AUTORIZADA ou FAMILIA_RECUSOU.
  - Sincroniza status com paciente.

#### AnexoDocumentoService
- Arquivo: backend/src/main/java/back/backend/service/AnexoDocumentoService.java
- Papel:
  - Upload/listagem/download/remocao de anexos.

### 1.4 Models (Dominio)
- Arquivos:
  - backend/src/main/java/back/backend/model/Paciente.java
  - backend/src/main/java/back/backend/model/ProtocoloME.java
  - backend/src/main/java/back/backend/model/ExameME.java
  - backend/src/main/java/back/backend/model/CentralTransplantes.java
  - backend/src/main/java/back/backend/model/Hospital.java
  - backend/src/main/java/back/backend/model/Usuario.java
  - backend/src/main/java/back/backend/model/AnexoDocumento.java
  - backend/src/main/java/back/backend/model/Role.java
- Pontos de atencao:
  - ProtocoloME contem o motor de status automatico.
  - ExameME define categorias e tipos de exame.
  - Paciente guarda espelho da entrevista familiar.

### 1.5 Repositories (Persistencia)
- Arquivos:
  - backend/src/main/java/back/backend/repository/PacienteRepository.java
  - backend/src/main/java/back/backend/repository/ProtocoloMERepository.java
  - backend/src/main/java/back/backend/repository/ExameMERepository.java
  - backend/src/main/java/back/backend/repository/HospitalRepository.java
  - backend/src/main/java/back/backend/repository/CentralTransplantesRepository.java
  - backend/src/main/java/back/backend/repository/UsuarioRepository.java
  - backend/src/main/java/back/backend/repository/AnexoDocumentoRepository.java
- Papel:
  - Consultas JPA e filtros do sistema.

### 1.6 Seguranca
- Arquivos:
  - backend/src/main/java/back/backend/security/SecurityConfig.java
  - backend/src/main/java/back/backend/security/JwtFilter.java
  - backend/src/main/java/back/backend/security/JwtUtil.java
  - backend/src/main/java/back/backend/security/PasswordConfig.java
- Papel:
  - Rotas por role, autenticacao JWT, interceptacao de request.

---

## 2) Frontend (React)

### 2.1 Entrada e roteamento
- Arquivos:
  - frontend/src/index.js
  - frontend/src/App.js
  - frontend/src/componentes/AppLayout.js
- Papel:
  - Navegacao por role.
  - GuardedRoute.

### 2.2 Fluxos principais por tela

#### Login
- Arquivo: frontend/src/componentes/login.js
- Funcoes principais:
  - validarEmail
  - handleSubmit
- Papel:
  - Login, cadastro publico e cadastro inicial admin.

#### Dashboard geral
- Arquivo: frontend/src/componentes/Dashboard.js
- Funcoes principais:
  - carregarDados
  - gerarNotificacoes
- Papel:
  - Visao resumida por role.

#### Fluxo Medico/Enfermeiro (nucleo)
- Arquivo: frontend/src/componentes/MedicoProtocoloME.js
- Funcoes principais:
  - mapearProtocolosParaPacientes
  - carregarPacientesProtocolo
  - atualizarPainelAposExame
  - carregarPacientesDisponiveis
  - iniciarProtocoloME
  - obterExamesRealizados
  - obterProximoPasso
- Papel:
  - Lista de pacientes em protocolo.
  - Acesso a exames e entrevista.

#### Exames
- Arquivo: frontend/src/componentes/ExameMEManager.js
- Funcoes principais:
  - carregarExames
  - carregarResumo
  - handleCriarExame
  - registrarResultado
  - deletarExame
  - isExameRealizado
  - isExameQueImpactaStatus
- Papel:
  - Gestao de exames e resultado.

#### Entrevista familiar
- Arquivo: frontend/src/componentes/EntrevistaFamiliarManager.js
- Funcoes principais:
  - carregarProtocolo
  - marcarParaEntrevista
  - salvarResultadoEntrevista
- Papel:
  - Etapa final decisiva apos confirmacao de ME.

#### Painel da Central
- Arquivo: frontend/src/componentes/CentralDashboardPage.js
- Funcoes principais:
  - carregarPacientesDoEstado
  - mapearProtocolosParaPacientes
  - carregarRelatorioFinalPaciente
  - exportarRelatorioCSV
  - exportarRelatorioPDF
- Papel:
  - Monitoramento e auditoria em modo leitura.
  - Geracao/exportacao de relatorio final por paciente.

#### Pacientes e cadastro
- Arquivos:
  - frontend/src/componentes/PacienteForm.js
  - frontend/src/componentes/PacientesPage.js
  - frontend/src/componentes/PacientesProtocoloMEPage.js
- Papel:
  - CRUD de paciente, filtros e acompanhamento em protocolo.

#### Hospitais e Centrais
- Arquivos:
  - frontend/src/componentes/HospitalForm.js
  - frontend/src/componentes/HospitaisPage.js
  - frontend/src/componentes/HospitalStatus.js
  - frontend/src/componentes/CentralTransplantesForm.js
  - frontend/src/componentes/CentraisPage.js
- Papel:
  - Cadastro e governanca operacional.

#### Usuarios (admin)
- Arquivo: frontend/src/componentes/UsuariosAdminPage.js
- Papel:
  - Cadastro/controle de contas e roles.

#### Anexos
- Arquivo: frontend/src/componentes/GerenciadorAnexos.js
- Papel:
  - Upload e gestao de arquivos de exame/entrevista.

### 2.3 Camada de API no frontend
- Arquivos:
  - frontend/src/api/api.js
  - frontend/src/services/apiClient.js
  - frontend/src/services/autenticarService.js
  - frontend/src/services/pacienteService.js
  - frontend/src/services/protocoloService.js
  - frontend/src/services/exameService.js
  - frontend/src/services/hospitalService.js
  - frontend/src/services/centralTransplantesService.js
  - frontend/src/services/anexoService.js
- Papel:
  - Interceptar token JWT e normalizar chamadas.

---

## 3) Ordem recomendada para estudar (sem travar)

1. Ler App.js + AppLayout.js para entender navegacao por perfil.
2. Ler MedicoProtocoloME.js para ver o fluxo principal de trabalho.
3. Ler ExameMEManager.js e EntrevistaFamiliarManager.js para entender as duas etapas criticas.
4. Ler ProtocoloMEController + ProtocoloMEService para entender regra de status.
5. Ler ExameMEController + ExameMEService para entender persistencia dos exames.
6. Ler CentralDashboardPage.js para ver monitoramento e relatorios.
7. Ler SecurityConfig.java para fechar entendimento de permissoes por role.

---

## 4) Checklist rapido para qualquer ajuste

- A tela chama o endpoint correto?
- O role logado pode usar esse endpoint?
- O backend salva e recalcula status no mesmo fluxo?
- O frontend usa chave unica no map/lista?
- O erro 401/403 tem mensagem clara para usuario?
- O comportamento foi validado com dados reais (nao so visual)?

---

## 5) Dica final
Quando surgir bug no produto, siga esta sequencia fixa:
1. Console e Network do browser
2. Endpoint no backend (curl)
3. Controller -> Service -> Model
4. Ajuste minimo
5. Revalidacao de ponta a ponta

Esse ciclo reduz 80% do retrabalho.

### Alteração de senha do usuário

- O formulário de alteração de senha foi movido do Dashboard para uma página própria.
- O componente responsável é `AlterarSenhaPage` (frontend/src/componentes/AlterarSenhaPage.js).
- O acesso é feito pelo menu lateral (link "Alterar Senha") ou diretamente pela rota `/alterar-senha`.
- O menu lateral foi atualizado para todos os perfis.
