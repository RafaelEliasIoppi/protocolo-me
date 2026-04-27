# Guia de Testes - Protocolo ME

## Estado Atual

Este guia é a referência principal de testes do projeto.

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
