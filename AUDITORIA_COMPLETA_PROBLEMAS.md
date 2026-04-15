# 🔍 AUDITORIA COMPLETA DE PROBLEMAS - protocolo-me

**Data:** 15 de Abril de 2026  
**Status:** ❌ 7 PROBLEMAS CRÍTICOS/IMPORTANTES ENCONTRADOS

---

## 📊 RESUMO EXECUTIVO

| Categoria | Severidade | Quantidade | Status |
|-----------|-----------|-----------|--------|
| Typos/Nomenclatura | 🔴 CRÍTICO | 3 | NÃO CORRIGIDO |
| Arquivos Duplicados | 🔴 CRÍTICO | 1 | NÃO CORRIGIDO |
| Validações Incompletas | 🟠 ALTO | 3 | NÃO CORRIGIDO |
| URLs Frontend | 🟢 OK | 0 | ✅ CORRIGIDO |
| Endpoints | 🟢 OK | 0 | ✅ CORRIGIDO |
| Configuração | 🟢 OK | 0 | ✅ CORRIGIDO |
| **TOTAL** | | **7** | |

---

## 🔴 PROBLEMAS CRÍTICOS

### 1. **TYPO: `telefonoResponsavel` em Paciente.java**

**Localização:** [backend/src/main/java/back/backend/model/Paciente.java](backend/src/main/java/back/backend/model/Paciente.java#L58)

**Problema:**
- Campo se chama `telefonoResponsavel` mas deveria ser `telefoneResponsavel` (português correto)
- "Telefone" é a grafia correta em português, não "telefono" (que é espanhol)
- Afeta os seguintes arquivos:
  - [Paciente.java](backend/src/main/java/back/backend/model/Paciente.java#L58) - declaração
  - [PacienteService.java](backend/src/main/java/back/backend/service/PacienteService.java#L46) - getter/setter

**Impacto:** Nomenclatura inconsistente com o padrão português, pode confundir desenvolvedores

**Correção Recomendada:**
```java
// ANTES
private String telefonoResponsavel;
public String getTelefonoResponsavel() { return telefonoResponsavel; }
public void setTelefonoResponsavel(String telefonoResponsavel) { this.telefonoResponsavel = telefonoResponsavel; }

// DEPOIS
private String telefoneResponsavel;
public String getTelefoneResponsavel() { return telefoneResponsavel; }
public void setTelefoneResponsavel(String telefoneResponsavel) { this.telefoneResponsavel = telefoneResponsavel; }
```

---

### 2. **NAMING INCONSISTENCY: `resultado_positivo` em ExameME.java**

**Localização:** [backend/src/main/java/back/backend/model/ExameME.java](backend/src/main/java/back/backend/model/ExameME.java#L39)

**Problema:**
- Campo usa underscore: `resultado_positivo`
- Violação do padrão camelCase usado no resto do código
- Deveria ser `resultadoPositivo`
- Afeta múltiplos arquivos:
  - [ExameME.java](backend/src/main/java/back/backend/model/ExameME.java#L39) - declaração
  - [ExameMEService.java](backend/src/main/java/back/backend/service/ExameMEService.java#L70) (2x)
  - [ExameMEService.java](backend/src/main/java/back/backend/service/ExameMEService.java#L83) (2x)
  - [ProtocoloMEService.java](backend/src/main/java/back/backend/service/ProtocoloMEService.java#L98)
  - [ExameMEController.java](backend/src/main/java/back/backend/controller/ExameMEController.java#L99)

**Impacto:** Inconsistência de estilo, dificulta manutenção

**Correção Recomendada:**
```java
// ANTES
private Boolean resultado_positivo;
public Boolean getResultado_positivo() { return resultado_positivo; }
public void setResultado_positivo(Boolean resultado_positivo) { this.resultado_positivo = resultado_positivo; }

// DEPOIS
private Boolean resultadoPositivo;
public Boolean getResultadoPositivo() { return resultadoPositivo; }
public void setResultadoPositivo(Boolean resultadoPositivo) { this.resultadoPositivo = resultadoPositivo; }
```

---

### 3. **NAMING INCONSISTENCY: `exames_Clinicos` em ExameMEService.java**

**Localização:** [backend/src/main/java/back/backend/service/ExameMEService.java](backend/src/main/java/back/backend/service/ExameMEService.java#L119)

**Problema:**
- Campo `exames_Clinicos` usa underscore (SNAKE_CASE)
- Outros campos usam camelCase: `examesComplementares`, `examesLaboratoriais`
- Inconsistência de nomenclatura na classe `ExameResumo`
- Afeta:
  - [ExameMEService.java L119](backend/src/main/java/back/backend/service/ExameMEService.java#L119)
  - [ExameMEService.java L130](backend/src/main/java/back/backend/service/ExameMEService.java#L130) (2x)
  - [ExameMEService.java L131](backend/src/main/java/back/backend/service/ExameMEService.java#L131) (2x)
  - [ExameMEService.java L108](backend/src/main/java/back/backend/service/ExameMEService.java#L108)

**Impacto:** Inconsistência dentro da classe, confunde usuários da API

**Correção Recomendada:**
```java
// ANTES
private int exames_Clinicos;
public int getExames_Clinicos() { return exames_Clinicos; }
public void setExames_Clinicos(int exames_Clinicos) { this.exames_Clinicos = exames_Clinicos; }

// DEPOIS
private int examesClinicos;
public int getExamesClinicos() { return examesClinicos; }
public void setExamesClinicos(int examesClinicos) { this.examesClinicos = examesClinicos; }
```

---

### 4. **ARQUIVO DUPLICADO: JwUtil.java ainda existe**

**Localização:** [backend/src/main/java/back/security/JwUtil.java](backend/src/main/java/back/security/JwUtil.java)

**Problema:**
- Arquivo foi renomeado para `JwtUtil.java` mas o original `JwUtil.java` não foi deletado
- Causa duplicação de código
- Pode causar confusão em imports
- Ambos os arquivos existem:
  - [JwUtil.java](backend/src/main/java/back/security/JwUtil.java) - ❌ OBSOLETO
  - [JwtUtil.java](backend/src/main/java/back/security/JwtUtil.java) - ✅ CORRETO

**Impacto:** Duplicação de código, potencial fonte de bugs

**Correção Recomendada:**
```bash
# Deletar arquivo obsoleto
rm backend/src/main/java/back/security/JwUtil.java
```

---

## 🟠 PROBLEMAS DE ALTO IMPACTO

### 5. **VALIDAÇÕES INCOMPLETAS: Paciente**

**Localização:** [backend/src/main/java/back/backend/service/PacienteService.java#L153](backend/src/main/java/back/backend/service/PacienteService.java#L153)

**Problema:**
Método `validarPaciente()` implementa apenas validações básicas. Faltam:

- ❌ Validação de formato de CPF (apenas verifica se não é vazio)
- ❌ Validação de email do responsável (se fornecido)
- ❌ Validação de formato de telefone
- ❌ Validação de consistência de datas:
  - `dataNascimento` não pode ser posterior a `dataAtual - 18 anos` (se maior de idade)
  - `dataInternacao` deve ser posterior a `dataNascimento`
- ❌ Validação de idade mínima

**Impacto:** Dados inconsistentes no banco de dados

---

### 6. **VALIDAÇÕES INCOMPLETAS: ExameME**

**Localização:** [backend/src/main/java/back/backend/service/ExameMEService.java#L25](backend/src/main/java/back/backend/service/ExameMEService.java#L25)

**Problema:**
Método `criarExame()` não valida:

- ❌ Se `protocoloME` existe (pode causar FK constraint violation)
- ❌ Se `categoria` é válida
- ❌ Se `tipoExame` é válido
- ❌ Validações de dados obrigatórios antes de salvar

**Impacto:** Erro ao inserir dados inválidos, sem mensagem clara

---

### 7. **VALIDAÇÕES INCOMPLETAS: CentralTransplantes**

**Localização:** [backend/src/main/java/back/backend/service/CentralTransplantesService.java](backend/src/main/java/back/backend/service/CentralTransplantesService.java)

**Problema:**
Sem validação de:

- ❌ Formato de CNPJ
- ❌ Formato de email
- ❌ Formato de telefone
- ❌ Dados obrigatórios (cidade, estado, etc.)

**Impacto:** Dados inconsistentes, violação de regras de negócio

---

## 🟢 ÁREAS OK (SEM PROBLEMAS)

### ✅ URLs Frontend
- ✅ Arquivo [frontend/src/api/api.js](frontend/src/api/api.js) usa URLs relativas (`/api`)
- ✅ Proxy configurado em [package.json](frontend/package.json#L21) para `http://localhost:2500`
- ✅ Nenhuma referência a `http://localhost:8080` nos arquivos fonte
- ✅ Nenhuma referência a `http://localhost:3000` nos arquivos fonte
- ℹ️ Referência a `http://localhost` em arquivo minificado (build) é do axios - não crítico

### ✅ Endpoints Controllers
- ✅ [PacienteController.java](backend/src/main/java/back/backend/controller/PacienteController.java) - CRUD completo
- ✅ [HospitalController.java](backend/src/main/java/back/backend/controller/HospitalController.java) - CRUD completo
- ✅ [ProtocoloMEController.java](backend/src/main/java/back/backend/controller/ProtocoloMEController.java) - Endpoints específicos OK
- ✅ [ExameMEController.java](backend/src/main/java/back/backend/controller/ExameMEController.java) - Endpoints OK
- ✅ [CentralTransplantesController.java](backend/src/main/java/back/backend/controller/CentralTransplantesController.java) - CRUD OK

### ✅ Configuração
- ✅ [application.properties](backend/src/main/resources/application.properties) - Porta correta (2500)
- ✅ [package.json](frontend/package.json) - Dependências OK
- ✅ Nenhum TODO, FIXME, XXX crítico em código-fonte

---

## 📋 ARQUIVOS AFETADOS (Por Severidade)

### 🔴 CRÍTICOS (Devem ser corrigidos)

| Arquivo | Linha(s) | Problema | Tipo |
|---------|---------|----------|------|
| [Paciente.java](backend/src/main/java/back/backend/model/Paciente.java) | 58 | telefonoResponsavel → telefoneResponsavel | TYPO |
| [PacienteService.java](backend/src/main/java/back/backend/service/PacienteService.java) | 46 | getTelefonoResponsavel() | TYPO |
| [ExameME.java](backend/src/main/java/back/backend/model/ExameME.java) | 39, 204-209 | resultado_positivo → resultadoPositivo | NAMING |
| [ExameMEService.java](backend/src/main/java/back/backend/service/ExameMEService.java) | 70, 78, 83, 108, 119-131 | resultado_positivo / exames_Clinicos | NAMING |
| [ExameMEController.java](backend/src/main/java/back/backend/controller/ExameMEController.java) | 99, 102 | resultado_positivo | NAMING |
| [ProtocoloMEService.java](backend/src/main/java/back/backend/service/ProtocoloMEService.java) | 98 | resultado_positivo | NAMING |
| [JwUtil.java](backend/src/main/java/back/security/JwUtil.java) | - | Arquivo duplicado/obsoleto | DUPLICAÇÃO |

### 🟠 ALTO (Recomendado corrigir)

| Arquivo | Problema | Severidade |
|---------|----------|-----------|
| [PacienteService.java](backend/src/main/java/back/backend/service/PacienteService.java#L153) | Validação de CPF, email, telefone, datas | IMPORTANTE |
| [ExameMEService.java](backend/src/main/java/back/backend/service/ExameMEService.java#L25) | Validação de FK, categorias, tipos | IMPORTANTE |
| [CentralTransplantesService.java](backend/src/main/java/back/backend/service/CentralTransplantesService.java) | Validação de CNPJ, email, telefone | IMPORTANTE |

---

## 🎯 RECOMENDAÇÕES DE CORREÇÃO

### Prioridade 1: IMEDIATA (Quebra funcionalidade)
1. ✅ Corrigir typo `telefonoResponsavel` → `telefoneResponsavel`
2. ✅ Corrigir naming `resultado_positivo` → `resultadoPositivo`
3. ✅ Corrigir naming `exames_Clinicos` → `examesClinicos`
4. ✅ Deletar arquivo `JwUtil.java` obsoleto

### Prioridade 2: IMPORTANTE (Melhoria de qualidade)
1. Adicionar validações de CPF em `PacienteService`
2. Adicionar validações de FK em `ExameMEService`
3. Adicionar validações em `CentralTransplantesService`

### Prioridade 3: BOM PARA MELHORAR (Segurança)
1. Executar `npm audit fix` para vulnerabilidades
2. Revisar CORS em `SecurityConfig` (atualmente aberto)
3. Implementar validação de políticas de senhas

---

## 📝 NOTAS

- **Nenhum TODO/FIXME/XXX crítico encontrado** em código-fonte Java
- **Frontend URLs corrigidas** corretamente em auditoria anterior
- **Endpoints estão OK** - nenhum faltando
- **Dados hardcoded**: Nenhum encontrado em locais críticos
- **Console.log/console.error**: Presentes (normais para desenvolvimento React)

---

## 🔗 REFERÊNCIAS RÁPIDAS

**Arquivos principais afetados:**
- [backend/src/main/java/back/backend/model/Paciente.java](backend/src/main/java/back/backend/model/Paciente.java)
- [backend/src/main/java/back/backend/model/ExameME.java](backend/src/main/java/back/backend/model/ExameME.java)
- [backend/src/main/java/back/backend/service/PacienteService.java](backend/src/main/java/back/backend/service/PacienteService.java)
- [backend/src/main/java/back/backend/service/ExameMEService.java](backend/src/main/java/back/backend/service/ExameMEService.java)
- [backend/src/main/java/back/backend/controller/ExameMEController.java](backend/src/main/java/back/backend/controller/ExameMEController.java)
- [backend/src/main/java/back/security/JwUtil.java](backend/src/main/java/back/security/JwUtil.java) ❌ DELETAR

**Configuração:**
- [frontend/package.json](frontend/package.json)
- [backend/src/main/resources/application.properties](backend/src/main/resources/application.properties)

---

**Gerado em:** 15 de Abril de 2026  
**Por:** Auditoria Automática do Workspace
