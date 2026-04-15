#!/bin/bash

# Script para executar todos os testes do projeto Protocolo ME

echo "======================================"
echo "Testes do Protocolo ME"
echo "======================================"

# Cores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Função para imprimir status
print_status() {
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ $1${NC}"
    else
        echo -e "${RED}✗ $1${NC}"
        exit 1
    fi
}

# Verificar argumentos
if [ "$1" == "backend" ]; then
    echo -e "${YELLOW}Executando testes do backend...${NC}"
    cd backend
    mvn test
    print_status "Testes do backend"
    cd ..

elif [ "$1" == "frontend" ]; then
    echo -e "${YELLOW}Executando testes do frontend...${NC}"
    cd frontend
    npm test -- --coverage
    print_status "Testes do frontend"
    cd ..

elif [ "$1" == "coverage" ]; then
    echo -e "${YELLOW}Gerando relatório de cobertura...${NC}"
    
    echo "Cobertura Backend..."
    cd backend
    mvn test jacoco:report
    print_status "Coverage backend"
    cd ..
    
    echo "Cobertura Frontend..."
    cd frontend
    npm test -- --coverage --watchAll=false
    print_status "Coverage frontend"
    cd ..
    
    echo -e "${GREEN}Relatórios de cobertura gerados!${NC}"

elif [ "$1" == "unit" ]; then
    echo -e "${YELLOW}Executando apenas testes unitários...${NC}"
    
    echo "Backend - Testes de serviço..."
    cd backend
    mvn test -Dtest="*ServiceTest"
    print_status "Testes unitários backend"
    cd ..
    
    echo "Frontend - Testes de serviço..."
    cd frontend
    npm test -- --testPathPattern="service\.test\.js"
    print_status "Testes unitários frontend"
    cd ..

elif [ "$1" == "integration" ]; then
    echo -e "${YELLOW}Executando testes de integração...${NC}"
    
    echo "Backend - Testes de controller..."
    cd backend
    mvn test -Dtest="*ControllerTest"
    print_status "Testes de integração backend"
    cd ..
    
    echo "Frontend - Testes de componente..."
    cd frontend
    npm test -- --testPathPattern="\.test\.js" --testPathIgnorePatterns="service"
    print_status "Testes de integração frontend"
    cd ..

elif [ "$1" == "all" ] || [ -z "$1" ]; then
    echo -e "${YELLOW}Executando todos os testes...${NC}"
    
    echo "Backend..."
    cd backend
    mvn clean test
    print_status "Testes backend completos"
    cd ..
    
    echo "Frontend..."
    cd frontend
    npm test -- --watchAll=false
    print_status "Testes frontend completos"
    cd ..
    
    echo -e "${GREEN}Todos os testes executados com sucesso!${NC}"

else
    echo "Uso: ./run-tests.sh [backend|frontend|coverage|unit|integration|all]"
    echo ""
    echo "Opções:"
    echo "  backend     - Executar apenas testes do backend"
    echo "  frontend    - Executar apenas testes do frontend"
    echo "  coverage    - Gerar relatórios de cobertura"
    echo "  unit        - Executar apenas testes unitários"
    echo "  integration - Executar apenas testes de integração"
    echo "  all         - Executar todos os testes (padrão)"
fi
