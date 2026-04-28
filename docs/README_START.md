# Como iniciar a aplicação

**Status Atual:** Backend e Frontend rodando com as últimas melhorias ✅

## Portas Configuradas
- **Backend (Spring Boot):** http://localhost:2500
- **Frontend (React):** http://localhost:3000

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
# Porta: 2500
```

**Terminal 2 - Frontend:**
```bash
cd frontend
npm start
# Porta: 3000
```

## Pré-requisitos

- Java 17+
- Node.js 14+
- npm ou yarn

## Funcionalidades Principais

### 🧪 Painel de Exames Melhorado
- Inserção e atualização de exames em tempo real
- Sincronização automática de flags (testeClinico1/2, complementares)
- Limpeza automática de mensagens após 5 segundos
- Feedback visual de carregamento

### 📊 Dashboard do Protocolo
- Visualização em tempo real das flags de exames
- Progresso dos testes clínicos
- Status automático do protocolo
- Controle de liberação de entrevista familiar

## Acesso Rápido

| Funcionalidade | URL | Descrição |
|---|---|---|
| Home | http://localhost:3000 | Página inicial |
| Login | http://localhost:3000/login | Autenticação |
| Protocolo ME | http://localhost:3000/medico/protocolo-me | Gerenciar protocolos |
| H2 Database | http://localhost:2500/h2-console | Console do banco (dev) |

## Para parar

Se usar o script ou make, pressione `Ctrl+C` ou execute:
```bash
make stop
```

## Página de Alteração de Senha

Agora existe uma página dedicada para alteração de senha do usuário:
- Acesse pelo menu lateral ("Alterar Senha") ou diretamente em `/alterar-senha`.
- O formulário foi removido do Dashboard para melhor organização e segurança.

**Como acessar:**
- Clique em "Alterar Senha" no menu lateral após login.
- Preencha a senha atual, nova senha e confirmação.
- Mensagens de sucesso e erro são exibidas na tela.

Essa mudança melhora a experiência do usuário e separa claramente as funções administrativas das funções de perfil pessoal.

## 🚀 Orientação de Desenvolvimento

### Com erro ao salvar paciente?

**Comece aqui**:
1. [GUIA_CODIGO_BACK_FRONT.md](GUIA_CODIGO_BACK_FRONT.md) - Manual único com o caminho completo do front até o backend, exemplos e endpoints

### Outros documentos úteis

- [INDICE_DOCUMENTOS.md](INDICE_DOCUMENTOS.md) - Índice completo de toda documentação
- [CHECKLIST_DEBUG_RAPIDO.md](CHECKLIST_DEBUG_RAPIDO.md) - Diagnóstico rápido de problemas
- [COMO_RODAR.md](COMO_RODAR.md) - Mais detalhes sobre configuração

