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
