.PHONY: start stop backend frontend help

help:
	@echo "Comandos disponíveis:"
	@echo "  make start      - Inicia Backend e Frontend"
	@echo "  make backend    - Inicia apenas o Backend"
	@echo "  make frontend   - Inicia apenas o Frontend"
	@echo "  make stop       - Para ambos os serviços"

start:
	@echo "Iniciando Backend e Frontend..."
	@cd backend && ./mvnw spring-boot:run &
	@sleep 5
	@cd frontend && npm start

backend:
	@echo "Iniciando Backend..."
	@cd backend && ./mvnw spring-boot:run

frontend:
	@echo "Iniciando Frontend..."
	@cd frontend && npm start

stop:
	@echo "Parando todos os serviços..."
	@pkill -f "spring-boot:run" || true
	@pkill -f "react-scripts" || true
	@echo "Serviços parados!"
