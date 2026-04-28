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

