# Relatório Técnico de Auditoria - Backend Protocolo-ME

## Visão Geral

O backend do Protocolo-ME é uma API REST Spring Boot voltada ao gerenciamento de protocolo de morte encefálica, cadastro de pacientes, usuários, hospitais, centrais de transplantes, exames, órgãos doados e anexos.

A arquitetura é clássica em camadas: controllers expõem a API, services concentram regra de negócio, repositories fazem persistência JPA/PostgreSQL, mappers fazem a ponte DTO-entidade e a camada de segurança usa JWT com autorização por roles. O sistema não possui integração ativa com Gmail ou Telegram no código atual. Campos de e-mail existem como dados de domínio, mas não há adaptadores, bots, SMTP ou serviços de notificação para esses canais.

## Mapa de Componentes

- [SecurityConfig.java](../backend/src/main/java/back/backend/security/SecurityConfig.java): define CORS, CSRF desabilitado, sessão stateless, cadeia de segurança e regras de acesso por role.
- [JwtFilter.java](../backend/src/main/java/back/backend/security/JwtFilter.java) e [JwtUtil.java](../backend/src/main/java/back/backend/security/JwtUtil.java): extraem, validam e interpretam JWT, alimentando o contexto de segurança.
- [UsuarioController.java](../backend/src/main/java/back/backend/controller/UsuarioController.java) e [UsuarioService.java](../backend/src/main/java/back/backend/service/UsuarioService.java): cadastro, login, alteração de senha e bootstrap de admin.
- [ProtocoloMEController.java](../backend/src/main/java/back/backend/controller/ProtocoloMEController.java) e [ProtocoloMEService.java](../backend/src/main/java/back/backend/service/ProtocoloMEService.java): criação, consulta e evolução do protocolo, incluindo sincronização com paciente e central.
- [PacienteController.java](../backend/src/main/java/back/backend/controller/PacienteController.java) e [PacienteService.java](../backend/src/main/java/back/backend/service/PacienteService.java): operação de pacientes, status, listagens e relatórios.
- [CentralTransplantesController.java](../backend/src/main/java/back/backend/controller/CentralTransplantesController.java) e [CentralTransplantesService.java](../backend/src/main/java/back/backend/service/CentralTransplantesService.java): gestão de centrais, vínculos com hospitais e estatísticas.
- [ExameMEService.java](../backend/src/main/java/back/backend/service/ExameMEService.java) e [AnexoDocumentoService.java](../backend/src/main/java/back/backend/service/AnexoDocumentoService.java): exames clínicos/complementares/laboratoriais e anexos persistidos em banco e filesystem.
- [ExportacaoSNTService.java](../backend/src/main/java/back/backend/service/ExportacaoSNTService.java): exporta CSV para o formato de integração SNT.
- Repositórios como [ProtocoloMERepository.java](../backend/src/main/java/back/backend/repository/ProtocoloMERepository.java), [AnexoDocumentoRepository.java](../backend/src/main/java/back/backend/repository/AnexoDocumentoRepository.java) e [OrgaoDoadoRepository.java](../backend/src/main/java/back/backend/repository/OrgaoDoadoRepository.java) sustentam a persistência e consultas.
- Mappers como [ProtocoloMapper.java](../backend/src/main/java/back/backend/mapper/ProtocoloMapper.java), [UsuarioRequestMapper.java](../backend/src/main/java/back/backend/mapper/UsuarioRequestMapper.java) e [PacienteMapper.java](../backend/src/main/java/back/backend/mapper/PacienteMapper.java) traduzem DTOs para entidades e vice-versa.

## Fluxo de Dados (Lógica de Negócio)

1. O request entra por um controller REST, por exemplo `UsuarioController` ou `ProtocoloMEController`.
2. A camada de service valida as regras de negócio, busca entidades relacionadas e aplica mudanças de estado.
3. O repositório JPA persiste a alteração ou executa consultas com carregamento de relacionamentos.
4. O retorno é convertido por MapStruct para DTO e devolvido ao cliente.
5. Na segurança, o `JwtFilter` intercepta as requisições protegidas, valida o token e popula o contexto de autenticação.

No caso do protocolo, o caminho principal é: criação do protocolo a partir do paciente, associação com a central padrão, atualização do status do paciente para `EM_PROTOCOLO_ME`, consultas detalhadas com `EntityGraph` e sincronização de estados entre protocolo, paciente, exames e órgãos doados.

No caso de anexos, o fluxo grava o arquivo em disco, salva metadados no banco e depois usa o relacionamento persistido para consultas por exame ou por protocolo.

## Pontos Críticos e Riscos

- A política de segurança está toda concentrada em `SecurityConfig`; qualquer mudança de rota exige atenção para não deixar brechas ou bloqueios indevidos.
- O `JwtFilter` rejeita token inválido com 401, mas não existe refresh, revogação ou blacklist de sessão.
- `ProtocoloMEService` usa a primeira central cadastrada como central padrão; isso funciona, mas é um acoplamento frágil com a ordem dos dados.
- `AnexoDocumentoService` faz persistência dupla em disco e banco sem mecanismo transacional forte entre os dois mundos; isso pode gerar arquivos órfãos.
- Vários repositórios dependem de nomes de relacionamento JPA; mudanças em entidades podem quebrar queries em runtime.
- O sistema não tem integração real com Gmail ou Telegram, então não há canal automático de notificação para eventos críticos.

## Sugestões de Refatoração

- Extrair políticas de autorização para uma configuração mais modular ou para constantes centralizadas.
- Criar um serviço explícito de notificações se Gmail ou Telegram for requisito real, em vez de usar apenas campos de e-mail como dados passivos.
- Separar a lógica de criação e transição de protocolo em métodos menores, reduzindo acoplamento em `ProtocoloMEService`.
- Reforçar a consistência entre filesystem e banco nos anexos com limpeza periódica de órfãos e tratamento de falha mais explícito.
- Preferir consultas/repositórios mais explícitos para reduzir dependência de nomes transitórios de propriedades.
- Adicionar testes de integração focados em CORS, JWT e transições de protocolo, que são os pontos de maior risco de regressão.

## Observação Final

Este relatório descreve o estado atual do código e deve ser atualizado sempre que a arquitetura, segurança ou os fluxos de protocolo mudarem.
