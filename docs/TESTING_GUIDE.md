1# Guia de Testes - Protocolo ME

> Leia primeiro: [GUIA_MESTRA_DE_INICIO.md](GUIA_MESTRA_DE_INICIO.md). Este documento é a referência secundária para validação.

## Como usar este guia

Este guia foi pensado para teste guiado e contínuo.

Fluxo recomendado:
1. Você pede a próxima etapa.
2. Eu executo ou preparo a verificação da etapa.
3. Eu te retorno com o resultado objetivo: passou, falhou ou ficou pendente.
4. Você me cobra a próxima etapa.

Se algo falhar, eu paro naquela etapa, mostro a causa provável e sigo para a correção antes de avançar.

## Roteiro de teste ponta a ponta

### Fase 0: Preparação

- Confirmar que backend e frontend estão disponíveis.
- Confirmar a versão de Java e Node compatíveis com o projeto.
- Garantir que não existam alterações locais inesperadas nos arquivos que serão testados.

### Fase 1: Backend isolado

Objetivo: provar que a API sobe, compila e responde corretamente sem depender do frontend.

Checklist:
- [ ] Compilação do backend com sucesso.
- [ ] Testes do backend com sucesso.
- [ ] Aplicação sobe sem erro de inicialização.
- [ ] Endpoint de login responde.
- [ ] Endpoint de listagem principal responde.
- [ ] Regras de segurança não bloqueiam rotas públicas indevidas.

### Fase 2: Frontend isolado

Objetivo: provar que a interface compila e consegue conversar com a API.

Checklist:
- [ ] Build do frontend com sucesso.
- [ ] Suítes de teste do frontend com sucesso.
- [ ] Tela inicial carrega.
- [ ] Login funciona.
- [ ] Rotas protegidas redirecionam corretamente.

### Fase 3: Fluxos essenciais do sistema

Objetivo: validar os caminhos mais importantes do negócio do começo ao fim.

Fluxos:
- [ ] Login e logout.
- [ ] Cadastro e edição de paciente.
- [ ] Cadastro e listagem de hospital.
- [ ] Gestão de usuários.
- [ ] Troca de senha do usuário logado.
- [ ] Cadastro e acompanhamento de protocolo ME.
- [ ] Cadastro e validação de exames.
- [ ] Upload e consulta de anexos.

### Fase 4: Validações de negócio e segurança

Objetivo: checar se as regras críticas continuam consistentes.

Checklist:
- [ ] Campos obrigatórios rejeitam entrada inválida.
- [ ] CPF, e-mail e telefone seguem as regras esperadas.
- [ ] CORS funciona com a origem do frontend.
- [ ] JWT expira e protege rotas autenticadas.
- [ ] Perfis de usuário respeitam permissões.

### Fase 5: Revisão visual e de experiência

Objetivo: garantir que a aplicação continua usável.

Checklist:
- [ ] Layout carrega sem erro visual.
- [ ] Mensagens de erro são claras.
- [ ] Botões e estados de carregamento aparecem corretamente.
- [ ] Fluxos principais funcionam em telas comuns.

### Fase 6: Encerramento

Objetivo: fechar a rodada de testes com um resumo claro.

Checklist:
- [ ] Tudo que foi testado ficou registrado.
- [ ] Tudo que falhou foi apontado com causa provável.
- [ ] Próximo passo ficou definido.

## Estado Atual

Este guia complementa o guia mestre e concentra os comandos de teste do projeto.

Status validado:
- Backend: 1 teste passando
- Frontend: 4 suítes, 19 testes passando

## Como executar

### Backend

```bash
cd backend
./mvnw clean test
```

### Frontend

```bash
cd frontend
npm test -- --watchAll=false --runInBand
```

### Build

```bash
cd backend
./mvnw -DskipTests package

cd ../frontend
npm run build
```

## Testes existentes

### Backend
- src/test/java/back/TransportadoraApplicationTests.java

### Frontend
- src/services/autenticarService.test.js
- src/services/pacienteService.test.js
- src/services/hospitalService.test.js
- src/componentes/Dashboard.test.js

## Scripts auxiliares

Na raiz do repositório:
- run-tests.sh
- run-tests.bat

## CI

A execução automática está definida em:
- .github/workflows/ci.yml

Fluxo:
- Em push e pull request para main
- Roda testes backend e frontend
- Roda build backend e frontend

## Notas importantes

- Arquivos locais de banco (backend/data/*.db) são artefatos de runtime e não devem ser versionados.
- Para testes frontend em ambiente CI, usar sempre `--watchAll=false --runInBand`.

## Smoke tests manuais (últimas alterações)

### 1. Gestão de usuários (ADMIN e COORDENADOR_TRANSPLANTES)

1. Entrar com perfil `ADMIN` ou `COORDENADOR_TRANSPLANTES`.
2. Abrir tela de gestão de usuários.
3. Validar listagem, criação, edição e redefinição de senha de usuário.
4. Confirmar mensagens amigáveis para erros de validação.

### 2. Troca de senha do usuário logado

1. Entrar com qualquer perfil autenticado.
2. No dashboard, preencher senha atual + nova senha + confirmação.
3. Validar sucesso quando dados corretos.
4. Validar erro quando confirmação divergir, senha atual incorreta ou nova senha curta.

### 3. Cadastro de centrais e erros de duplicidade

1. Criar central com nome/CNPJ inéditos.
2. Tentar criar outra com mesmo nome ou CNPJ.
3. Confirmar retorno com mensagem clara de duplicidade.

### 4. Segurança e CORS

1. Verificar que o login retorna `token` e `tokenExpiraEm`.
2. Validar que origem do frontend utilizada está listada em `app.cors.allowed-origins`.

## Como me cobrar durante a execução

Use mensagens curtas como estas:

- "testa a fase 1"
- "mostra o resultado do backend"
- "segue para o frontend"
- "para e me diz o que falhou"

Eu vou responder sempre com:
- o que foi verificado
- se passou ou falhou
- qual é o próximo passo
