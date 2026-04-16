# 🚀 Como Rodar o Protocolo ME

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
npm install
npm start
```

---

## ✅ Verificar se está funcionando

- **Backend**: http://localhost:2500/h2-console
- **Frontend**: http://localhost:3000
- **API**: http://localhost:2500/api/usuarios

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
- **Banco de Dados**: H2 em `backend/data/banco.mv.db`
- **CORS**: Habilitado para todas as origins (`*`)

---

## 🐛 Troubleshooting

| Problema | Solução |
|----------|---------|
| "Porta 2500 em uso" | `lsof -i :2500` e mate o processo |
| "Porta 3000 em uso" | `lsof -i :3000` e mate o processo |
| Frontend não conecta API | Limpe cache (Ctrl+Shift+R) e reinicie |
| Erro ao cadastrar usuário | Verifique se o role é válido |
| Banco corrompido | Delete `backend/data/banco*` |

