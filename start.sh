#!/bin/bash

# Script para iniciar Backend e Frontend simultaneamente
# Backend (Spring Boot) roda em http://localhost:2500
# Frontend (React) roda em http://localhost:3000

set -e

echo "🚀 Iniciando Backend e Frontend..."
echo ""

# Cores para output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Inicia Backend em background
echo -e "${BLUE}▶ Iniciando Backend (Spring Boot) na porta 2500...${NC}"
cd backend
./mvnw spring-boot:run &
BACKEND_PID=$!
cd ..

# Aguarda um tempo para o backend iniciar
sleep 5

# Inicia Frontend em background
echo -e "${BLUE}▶ Iniciando Frontend (React) na porta 3000...${NC}"
cd frontend

if [ ! -d node_modules ]; then
	echo -e "${BLUE}▶ Dependências do frontend não encontradas. Executando npm install...${NC}"
	npm install
fi

npm start &
FRONTEND_PID=$!
cd ..

echo ""
echo -e "${GREEN}✓ Aplicação iniciada com sucesso!${NC}"
echo ""
echo "📍 Backend:  http://localhost:2500"
echo "📍 Frontend: http://localhost:3000"
echo ""
echo "Para parar, pressione Ctrl+C ou execute: kill $BACKEND_PID $FRONTEND_PID"
echo ""

# Mantém os processos rodando
wait
