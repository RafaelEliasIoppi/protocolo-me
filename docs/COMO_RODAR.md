# 🚀 Como Rodar o Protocolo ME

> Leia primeiro: [GUIA_MESTRA_DE_INICIO.md](GUIA_MESTRA_DE_INICIO.md). Use este documento só quando a tarefa for iniciar a aplicação.

## Opção 1: Script Automático (Recomendado)

```bash
cd /workspaces/protocolo-me
bash start.sh
```

**O que faz:**
- Inicia Backend (Spring Boot) em `http://localhost:2500`
- Inicia Frontend (React) em `http://localhost:3000`
- Ambos rodam em background
- Se as dependências do frontend não estiverem instaladas, o script executa `npm install` automaticamente antes de iniciar

---

## Opção 2: Manualmente (Dois Terminais)

### Terminal 1 - Backend
```bash
cd /workspaces/protocolo-me/backend
./mvnw spring-boot:run
```

### Terminal 2 - Frontend
```bash
cd /workspaces/protocolo-me/frontend
nvm use
npm install
npm start
```

> Observação: o frontend está configurado para Node `>=18 <24` (arquivo `.nvmrc` em `frontend/`).

---

## ✅ Verificar se está funcionando

- **Backend**: usa Supabase/PostgreSQL via variáveis de ambiente
- **Frontend**: http://localhost:3000
- **API**: http://localhost:2500/api/usuarios

### Acesso inicial local

- E-mail: `admin@protocolo.me`
- Senha: `Admin123!`
- Use esse acesso para validar o painel médico e os smoke tests locais.

## Smoke test rápido do painel médico

1. Suba backend e frontend.
2. Faça login com `admin@protocolo.me` e `Admin123!`.
3. Acesse `/medico/protocolo-me`.
4. Verifique se a listagem de protocolos carrega sem erro 500.

---

## 📝 Cadastrar Novo Usuário

No formulário de registro, use um destes **roles**:
- `MEDICO`
- `ENFERMEIRO`
- `COORDENADOR_TRANSPLANTES`
- `CENTRAL_TRANSPLANTES`
- `ADMIN`

---

## 🛑 Parar a Aplicação

```bash
# Se rodou com start.sh, pressione Ctrl+C

# Ou se rodou em background:
pkill -f "java -jar"
pkill -f "react-scripts"
```

---

## ⚙️ Configurações Importantes

- **Backend**: Porta 2500 (em `backend/src/main/resources/application.properties`)
- **Frontend**: Porta 3000 (React padrão)
- **Banco de Dados**: Supabase/PostgreSQL via `DATABASE_URL`
- **CORS**: Controlado por `app.cors.allowed-origins` no backend

---

## 🐛 Troubleshooting

| Problema | Solução |
|----------|---------|
| "Porta 2500 em uso" | `lsof -i :2500` e mate o processo |
| "Porta 3000 em uso" | `lsof -i :3000` e mate o processo |
| Frontend não conecta API | Limpe cache (Ctrl+Shift+R) e reinicie |
| Erro ao cadastrar usuário | Verifique se o role é válido |
| Erro de CORS no browser | Confira `app.cors.allowed-origins` e inclua a URL do frontend |
| Troca de senha falha | Verifique se a senha atual está correta e se a nova tem 6+ caracteres |
| Banco corrompido | Delete `backend/data/banco*` |

