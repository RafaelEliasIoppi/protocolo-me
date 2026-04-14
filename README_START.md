# Como iniciar a aplicação

Existem várias formas de iniciar o Backend e o Frontend:

## Opção 1: Script Bash (recomendado)

```bash
chmod +x start.sh
./start.sh
```

Isso inicia ambos os serviços em paralelo:
- **Backend**: http://localhost:2500
- **Frontend**: http://localhost:3000

## Opção 2: Usando Make

```bash
make start
```

**Comandos Make disponíveis:**
```bash
make backend      # Inicia apenas o Backend
make frontend     # Inicia apenas o Frontend
make stop         # Para ambos os serviços
```

## Opção 3: Manualmente

**Terminal 1 - Backend:**
```bash
cd backend
./mvnw spring-boot:run
```

**Terminal 2 - Frontend:**
```bash
cd frontend
npm start
```

## Pré-requisitos

- Java 17+
- Node.js 14+
- npm ou yarn

## Para parar

Se usar o script ou make, pressione `Ctrl+C` ou execute:
```bash
make stop
```
