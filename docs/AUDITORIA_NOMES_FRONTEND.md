# 🔴 Auditoria: Nomes Genéricos no Frontend

## Resumo Executivo

Total de funções com nomes genéricos: **20 funções**
Padrão encontrado: `handle*` ou `on*` sem especificar a ação

---

## 📋 Lista Completa por Arquivo

### 1. **login.js** (2 funções)

| Atual | Problema | Proposta |
|---|---|---|
| `handleChange` | "Handle" genérico | `atualizarCampoConta` |
| `handleSubmit` | Não deixa claro: login, registro? | `fazerLogin` ou `autenticar` |

---

### 2. **OrgaoDoadoManager.js** (4 funções)

| Atual | Problema | Proposta |
|---|---|---|
| `handleChangeForm` | Mudança de qual formulário? | `atualizarCampoFormulario` |
| `handleCriarOuAtualizarOrgao` | "Handle" desnecessário | `salvarOrgao` |
| `handleEditar` | Não especifica: editar o quê? | `editarOrgao` |
| `handleDeletar` | Não especifica: deletar o quê? | `deletarOrgao` |

---

### 3. **CentralPacienteEditForm.js** (2 funções)

| Atual | Problema | Proposta |
|---|---|---|
| `handleInputChange` | "Handle" genérico | `atualizarCampoFormulario` |
| `handleSubmit` | Não deixa claro: criar ou atualizar? | `salvarAlteracoesPaciente` |

---

### 4. **CentralTransplantesForm.js** (2 funções)

| Atual | Problema | Proposta |
|---|---|---|
| `handleChange` | "Handle" genérico | `atualizarCampoFormulario` |
| `handleSubmit` | Não especifica a ação | `salvarCentral` |

---

### 5. **EstatisticasPage.js** (4 funções)

| Atual | Problema | Proposta |
|---|---|---|
| `handleMudarAno` | "Handle" desnecessário | `selecionarAno` |
| `handleMudarAba` | "Handle" desnecessário | `selecionarAba` |
| `handleSelecionarProtocolo` | "Handle" redundante | `selecionarProtocolo` |
| `handleSalvarEstatistica` | "Handle" redundante | `salvarEstatistica` |

---

### 6. **EntrevistaFamiliarManager.js** (1 função)

| Atual | Problema | Proposta |
|---|---|---|
| `handleInputChange` | "Handle" genérico | `atualizarCampoFormulario` |

---

### 7. **UsuariosAdminPage.js** (1 função)

| Atual | Problema | Proposta |
|---|---|---|
| `handleSubmit` | Não especifica: criar ou atualizar? | `criarUsuario` |

---

### 8. **ProtocoloMEManager.js** (2 funções)

| Atual | Problema | Proposta |
|---|---|---|
| `handleChangeForm` | "Handle" genérico | `atualizarCampoFormulario` |
| `handleCriarProtocolo` | "Handle" redundante | `criarProtocolo` |

---

### 9. **HospitalForm.js** (2 funções)

| Atual | Problema | Proposta |
|---|---|---|
| `handleChange` | "Handle" genérico | `atualizarCampoFormulario` |
| `handleSubmit` | Não especifica a ação | `salvarHospital` |

---

### 10. **GerenciadorAnexos.js** (1 função)

| Atual | Problema | Proposta |
|---|---|---|
| `handleArquivoChange` | "Handle" desnecessário | `selecionarArquivo` |

---

## 📊 Análise por Padrão

### Padrão 1: `handleInputChange` (5 ocorrências)
- PacienteForm.js ✅ **JÁ RENOMEADO** para `atualizarCampoFormulario`
- CentralPacienteEditForm.js
- EntrevistaFamiliarManager.js

### Padrão 2: `handleChange` (3 ocorrências)
- login.js
- CentralTransplantesForm.js
- HospitalForm.js

### Padrão 3: `handleSubmit` (4 ocorrências)
- login.js
- CentralPacienteEditForm.js
- UsuariosAdminPage.js
- HospitalForm.js

### Padrão 4: `handleChangeForm` (2 ocorrências)
- OrgaoDoadoManager.js
- ProtocoloMEManager.js

### Outros padrões (6 ocorrências)
- handleEditar, handleDeletar, handleCriarOuAtualizarOrgao, etc.

---

## 🎯 Recomendações

### Prioridade ALTA (10 funções)
Essas precisam ser renomeadas imediatamente:

1. **handleChangeForm** (2x) → `atualizarCampoFormulario`
2. **handleInputChange** (2x) → `atualizarCampoFormulario`
3. **handleChange** (3x) → `atualizarCampoFormulario`
4. **handleEditar** → `editarOrgao`
5. **handleDeletar** → `deletarOrgao`
6. (mais 2)

### Prioridade MÉDIA (7 funções)
Nomes confusos que precisam ser específicos:

7. **handleSubmit** (4x) → Renomear de forma específica:
   - login.js: `fazerLogin` ou `autenticar`
   - CentralPacienteEditForm.js: `salvarAlteracoesPaciente`
   - UsuariosAdminPage.js: `criarUsuario`
   - HospitalForm.js: `salvarHospital`

### Prioridade BAIXA (3 funções)
Nomes razoáveis, mas podem melhorar:

8. **handleMudarAno** → `selecionarAno` (mais descritivo)
9. **handleMudarAba** → `selecionarAba` (mais descritivo)
10. **handleArquivoChange** → `selecionarArquivo` (mais direto)

---

## Resumo para Ação

**Total: 20 funções para renomear**

| Severidade | Quantidade | Ação |
|---|---|---|
| 🔴 ALTA | 10 | Renomear LOGO |
| 🟡 MÉDIA | 7 | Renomear em breve |
| 🟢 BAIXA | 3 | Renomear quando possível |

---

## Padrão Recomendado

**Use nomes que descrevem a AÇÃO:**

❌ Evitar:
```javascript
const handleClick = () => {}
const handleChange = () => {}
const handleSubmit = () => {}
```

✅ Preferir:
```javascript
const clicarBotaoSalvar = () => {}
const atualizarCampoFormulario = () => {}
const salvarPaciente = () => {}
```

---

**Quer que eu renomeie tudo agora? Posso fazer arquivo por arquivo!**
