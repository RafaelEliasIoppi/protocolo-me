@echo off
REM Script para executar testes no Windows

setlocal enabledelayedexpansion

echo ======================================
echo Testes do Protocolo ME
echo ======================================

set BACKEND_TESTS=mvn test
set FRONTEND_TESTS=npm test -- --watchAll=false
set BACKEND_COVERAGE=mvn test jacoco:report
set FRONTEND_COVERAGE=npm test -- --coverage --watchAll=false

if "%1%"=="backend" (
    echo Executando testes do backend...
    cd backend
    %BACKEND_TESTS%
    cd ..
) else if "%1%"=="frontend" (
    echo Executando testes do frontend...
    cd frontend
    %FRONTEND_TESTS%
    cd ..
) else if "%1%"=="coverage" (
    echo Gerando relatório de cobertura...
    
    echo Cobertura Backend...
    cd backend
    %BACKEND_COVERAGE%
    cd ..
    
    echo Cobertura Frontend...
    cd frontend
    %FRONTEND_COVERAGE%
    cd ..
    
    echo Relatórios de cobertura gerados!
) else if "%1%"=="unit" (
    echo Executando apenas testes unitários...
    
    echo Backend - Testes de serviço...
    cd backend
    mvn test -Dtest="*ServiceTest"
    cd ..
    
    echo Frontend - Testes de serviço...
    cd frontend
    npm test -- --testPathPattern="service\.test\.js" --watchAll=false
    cd ..
) else if "%1%"=="integration" (
    echo Executando testes de integração...
    
    echo Backend - Testes de controller...
    cd backend
    mvn test -Dtest="*ControllerTest"
    cd ..
    
    echo Frontend - Testes de componente...
    cd frontend
    npm test -- --testPathPattern="\.test\.js" --testPathIgnorePatterns="service" --watchAll=false
    cd ..
) else (
    echo Executando todos os testes...
    
    echo Backend...
    cd backend
    mvn clean test
    cd ..
    
    echo Frontend...
    cd frontend
    %FRONTEND_TESTS%
    cd ..
    
    echo Todos os testes executados!
)

endlocal
