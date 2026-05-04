# 🔒 Relatório de Segurança - Usuários & RLS

**Data:** 04/05/2026
**Status:** ⚠️ **CRÍTICO - Implementação RLS necessária**
**Severidade:** **ALTA** - Banco de dados sem isolamento de dados

---

## 📋 ÍNDICE

1. [Análise de Segurança Atual](#análise-de-segurança-atual)
2. [Fluxos de Usuários Verificados](#fluxos-de-usuários-verificados)
3. [Vulnerabilidades Identificadas](#vulnerabilidades-identificadas)
4. [Implementação de RLS](#implementação-de-rls)
5. [Plano de Ação](#plano-de-ação)

---

## 📊 Análise de Segurança Atual

### ✅ **O QUE ESTÁ BOM**

#### 1. **Autenticação & Tokens JWT**
```
✅ HS256 - Algoritmo robusto
✅ Secret key - Mínimo 32 caracteres
✅ Token ID único - UUIDs para rastreamento
✅ Issuer validado - Previne token forjado
✅ Expiração - 10 horas por padrão
✅ Stateless - Sem sessão no servidor
```

**Localização:** `/backend/src/main/java/back/backend/security/JwtUtil.java`

#### 2. **Criptografia de Senha**
```
✅ BCrypt com strength configurável
✅ Validação: mínimo 6 caracteres
✅ Matching correto em autenticação
```

**Localização:** `/backend/src/main/java/back/backend/security/PasswordConfig.java`

#### 3. **Validação de Entrada**
```
✅ Email validado em criação/edição
✅ E-mail duplicado bloqueado
✅ Proteção do admin principal (backend + frontend)
✅ DTO com @NotBlank em campos críticos
```

**DTOs Protegidas:**
- `UsuarioRequestDTO` - @NotBlank em email e nome
- `AlterarSenhaDTO` - @NotBlank em todas as senhas
- `ResetSenhaDTO` - Validação de comprimento

#### 4. **Controle de Acesso (RBAC)**
```
✅ Roles bem definidas: ADMIN, MEDICO, ENFERMEIRO, etc
✅ SecurityConfig com autorização por endpoint
✅ Permissões granulares por método HTTP
```

**Exemplo SecurityConfig:**
```java
.requestMatchers("/api/usuarios/**").hasAnyRole("ADMIN", "CENTRAL_TRANSPLANTES")
.requestMatchers(HttpMethod.GET, "/api/pacientes/**").hasAnyRole("ADMIN", "MEDICO", ...)
```

#### 5. **Admin Principal Protegido**
```
✅ Backend: UsuarioService.isAdminPrincipal() verifica role + email
✅ Frontend: Bloqueio de desativação na UI
✅ Config: app.seed.admin-email = admin@protocolo.me
```

---

### ❌ **O QUE PRECISA MELHORAR (CRÍTICO)**

#### **1. ROW LEVEL SECURITY (RLS) - NÃO IMPLEMENTADO**

**Aviso do Supabase:**
```
❌ Table public.usuario - RLS disabled
❌ Table public.paciente - RLS disabled
❌ Table public.exame_me - RLS disabled
❌ Table public.protocolo_me - RLS disabled
❌ Table public.hospital - RLS disabled
❌ Table public.central_transplantes - RLS disabled
❌ 11 tabelas sem RLS habilitado
```

**Risco:**
- Um usuário autenticado poderia **acessar diretamente o banco** via Supabase client
- Sem RLS, a segurança depende **100% do backend**
- Se o frontend usar supabase-js diretamente → **bypass total**
- Um admin malicioso poderia ler dados de outros usuários

**Exemplo de Exploit:**
```javascript
// Sem RLS, isso funcionaria mesmo sem autorização
const { data } = await supabase
  .from('usuario')
  .select('*')  // Lê TODOS os usuários incluindo senhas (se retornadas)
```

#### **2. Falta de Isolamento de Dados por Usuário**

**Problema:**
- Não há filtro automático no banco baseado na identidade
- Backend é responsável por **todas** as validações de acesso
- Sem RLS como "fallback seguro" do backend

**Exemplo Crítico:**
```java
// Paciente Service lista todos os pacientes
public List<PacienteDTO> listarTodos() {
  return pacienteRepository.findAll()  // Sem filtro!
      .stream()
      .map(this::toDTO)
      .toList();
}
// Depende 100% do @hasRole("MEDICO") no endpoint
```

#### **3. Sem Auditoria em Nível de Banco**

**Não há:**
- Trigger para log de změny
- Soft delete para rastreamento
- Timestamp automático de alterações (tem via JPA, mas não em banco)

---

## 🔄 Fluxos de Usuários Verificados

### **1. CRIAÇÃO DE USUÁRIO**

```
┌─────────────────────────────────────────────────────────┐
│ Frontend: UsuariosAdminPage.js                          │
│ - Valida: nome, email, senha (≥6 chars)                 │
│ - Normaliza: email (trim + lowercase)                    │
│ - Revoga: campos com XSS potencial? ✓ React safe        │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│ API: POST /api/usuarios/admin/registrar                 │
│ - Público? ✓ Sim (bootstrap permite primeiro admin)     │
│ - Validation: @Valid na UsuarioRequestDTO               │
│ - DTO valida: @NotBlank email, nome                      │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│ Backend: UsuarioService.registrarAdmin()                │
│ ✅ Valida permissão: ADMIN ou bootstrap                  │
│ ✅ Normaliza email: trim().toLowerCase()                 │
│ ✅ Valida email duplicado                                │
│ ✅ Valida comprimento: senha ≥ 6 chars                   │
│ ✅ Codifica senha: BCrypt                                │
│ ✅ Role padrão: MEDICO se não informado                  │
│ ✅ Timestamps: dataCriacao + dataAtualizacao             │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│ Database: INSERT INTO usuario (...)                      │
│ ❌ **SEM RLS** - Qualquer um com acesso ao banco lê    │
│ ⚠️ Supabase permite conexão direta se não tem RLS       │
└─────────────────────────────────────────────────────────┘
```

**Resultado:** ✅ Criação segura se usar backend; ❌ Insegura se usar supabase-js direto

---

### **2. AUTENTICAÇÃO (LOGIN)**

```
┌───────────────────────────────────────┐
│ POST /api/usuarios/login              │
│ { email, senha }                       │
└───────────────────────────────────────┘
           ↓
┌───────────────────────────────────────┐
│ UsuarioService.autenticar()           │
│ ✅ Normaliza email                    │
│ ✅ Busca por email no banco           │
│ ✅ Valida: usuario.ativo == true      │
│ ✅ Valida: BCrypt.matches()           │
│ ❌ Sem proteção contra brute force    │
└───────────────────────────────────────┘
           ↓
┌───────────────────────────────────────┐
│ JwtUtil.gerarToken()                  │
│ Claims: { email, role }                │
│ ✅ Assinado com secret (32+ chars)    │
│ ✅ Expira em 10h                        │
│ ✅ UUID único como ID                 │
└───────────────────────────────────────┘
           ↓
┌───────────────────────────────────────┐
│ RetornaAuthResponseDTO                │
│ { token, expiraEm, usuario }           │
│ ✅ Não retorna senha                  │
│ ✅ Timestamp em ms                     │
└───────────────────────────────────────┘
```

**Resultado:** ✅ Seguro em todas as camadas; Sem brute force = Ponto fraco

---

### **3. EDIÇÃO DE USUÁRIO (PUT)**

```
┌──────────────────────────────────────────────┐
│ Frontend: FormEdicao                         │
│ ✅ Valida: nome, email formato, nova senha  │
│ ✅ Bloqueia: desativar admin principal      │
│ ✅ Normaliza: email (trim + lowercase)       │
└──────────────────────────────────────────────┘
             ↓
┌──────────────────────────────────────────────┐
│ PUT /api/usuarios/{id}                       │
│ @auth: hasRole("ADMIN", "CENTRAL")           │
│ Validation: @Valid UsuarioRequestDTO         │
└──────────────────────────────────────────────┘
             ↓
┌──────────────────────────────────────────────┐
│ UsuarioService.atualizarUsuario()            │
│ ✅ Busca usuario por ID                      │
│ ✅ Bloqueia desativar admin principal        │
│ ✅ Atualiza apenas campos fornecidos         │
│ ✅ Normaliza email se fornecido              │
│ ❌ Sem validação: usuário só edita a si?    │
│    (Depende de ROLE no backend)              │
└──────────────────────────────────────────────┘
```

**Resultado:** ⚠️ **Risco:** ADMIN pode editar qualquer usuário, sem auditoria de quem mudou

---

### **4. REDEFINIÇÃO DE SENHA**

```
PATCH /api/usuarios/{id}/senha
┌──────────────────────────────────────────────┐
│ Payload: { senhaNova }                       │
│ ✅ Auth: hasRole("ADMIN", "CENTRAL")         │
│ ✅ DTO: @Valid com @NotBlank                 │
└──────────────────────────────────────────────┘
             ↓
┌──────────────────────────────────────────────┐
│ UsuarioService.redefinirSenha()              │
│ ✅ Valida: senha ≥ 6 chars                   │
│ ✅ Usa: BCrypt para codificar                │
│ ⚠️ Sem confirmação do usuário                │
│ ⚠️ Sem email de notificação                  │
└──────────────────────────────────────────────┘
```

**Resultado:** ⚠️ ADMIN pode redefinir senha sem consentimento; Sem auditoria

---

### **5. ALTERNAR STATUS (Ativar/Desativar)**

```
PATCH /api/usuarios/{id}/status
┌──────────────────────────────────────────────┐
│ Frontend: alternarStatusUsuario()             │
│ ✅ Bloqueia: admin principal                 │
│ ✅ Monta payload completo (não apenas ativo) │
│ Payload: { nome, email, role, ativo, ... }   │
└──────────────────────────────────────────────┘
             ↓
┌──────────────────────────────────────────────┐
│ PUT /api/usuarios/{id}                       │
│ @auth: hasRole("ADMIN", "CENTRAL")           │
│ JSON: { ..., ativo: false }                  │
└──────────────────────────────────────────────┘
             ↓
┌──────────────────────────────────────────────┐
│ UsuarioService.atualizarUsuario()            │
│ ✅ Valida: isAdminPrincipal() bloqueado      │
│ ✅ Atualiza: usuario.ativo = dados.ativo     │
│ ✅ Timestamp: dataAtualizacao atualizado     │
│ ❌ Sem log: quem desativou?                  │
└──────────────────────────────────────────────┘
```

**Resultado:** ✅ Admin protegido; ❌ Sem auditoria de quem fez a mudança

---

## 🚨 Vulnerabilidades Identificadas

### **CRÍTICA #1: RLS Desabilitado**

| Aspecto | Status | Impacto |
|---------|--------|--------|
| RLS em usuario | ❌ Disabled | Um usuário pode ler/escrever qualquer registro |
| RLS em paciente | ❌ Disabled | Vazamento de dados de pacientes |
| RLS em protocolo_me | ❌ Disabled | Acesso irrestritto a protocolos |
| RLS em exame_me | ❌ Disabled | Exames médicos expostos |
| **Total de tabelas afetadas** | **11/11** | **Crítico** |

**Solução:** Implementar RLS com políticas de isolamento por usuário

---

### **CRÍTICA #2: Sem Isolamento de Dados por Hospital/Central**

**Problema:** Um médico de hospital A **pode acessar** dados de hospital B

**Exemplo:**
```java
// PacienteService.listarTodos() retorna TODOS os pacientes
// Backend filtra por @hasRole("MEDICO")
// Mas MÉ DICO A vê TODOS os médicos, não apenas do hospital dele
```

**Solução:** Adicionar filtros automáticos baseados em `hospitalId` do usuário

---

### **MÉDIA #3: Sem Auditoria (Quem fez quê?)**

| Ação | Log? | Rastreamento |
|------|------|--------------|
| Criar usuário | ❌ Não | Quem criou? Quando? |
| Editar usuário | ❌ Não | Quem mudou email/role? |
| Desativar usuário | ❌ Não | Razão? Autorização? |
| Redefinir senha | ❌ Não | Quem resetou? |
| DeleteAlso usário | ❌ Não | Quem e por quê? |

**Solução:** Adicionar auditoria (tabela `audit_log` ou Supabase triggers)

---

### **MÉDIA #4: Sem Proteção Contra Brute Force**

| Requisição | Limite | Proteção |
|------------|--------|----------|
| Login / POST /api/usuarios/login | ∞ | ❌ Nenhuma |
| Tentativas falhadas | Ilimitadas | ❌ Sem bloqueio |
| Tempo de espera | 0 | ❌ Sem delay |

**Impacto:** Um atacante pode testar 1000s de senha/segundo

**Solução:** Implementar rate limiting com Redis ou Spring Security

---

### **BAIXA #5: Email sem Confirmação**

| Processo | Validação |
|----------|-----------|
| Registrar usuário | Email não validado |
| Mudar email | Sem confirmação do novo |
| Email pode ser inválido | Sim, não há teste |

**Solução:** Implementar email confirmation flow

---

## 🛡️ Implementação de RLS

### **O que é RLS (Row Level Security)?**

RLS é uma política de banco de dados que automaticamente filtra linhas baseado na identidade do usuário:

```sql
-- SEM RLS
SELECT * FROM usuario;  -- Um admin vê TODOS

-- COM RLS
SELECT * FROM usuario;  -- Um médico vê APENAS seu registro
  WHERE id = current_user_id;
```

### **Estratégia RLS para Protocolo.me**

#### **Tabela: usuario**

```sql
-- 1. Habilitar RLS
ALTER TABLE usuario ENABLE ROW LEVEL SECURITY;

-- 2. Política: Admin vê todos
CREATE POLICY "admin_can_view_all_users" ON usuario
  FOR SELECT
  USING (
    auth.jwt() ->> 'role' = 'ADMIN'
    OR auth.uid() = id::text
  );

-- 3. Política: Usuário vê apenas a si mesmo
CREATE POLICY "users_view_own" ON usuario
  FOR SELECT
  USING (auth.uid() = email OR auth.jwt() ->> 'role' = 'ADMIN');

-- 4. Política: Central de Transplantes vê médicos/enfermeiros
CREATE POLICY "central_view_staff" ON usuario
  FOR SELECT
  USING (
    auth.jwt() ->> 'role' = 'CENTRAL_TRANSPLANTES'
    AND role IN ('MEDICO', 'ENFERMEIRO')
  );
```

#### **Tabela: paciente**

```sql
ALTER TABLE paciente ENABLE ROW LEVEL SECURITY;

-- Médico/Enfermeiro vê apenas pacientes do seu hospital
CREATE POLICY "staff_view_own_hospital" ON paciente
  FOR SELECT
  USING (
    hospital_id IN (
      SELECT hospital_id FROM usuario
      WHERE email = auth.jwt() ->> 'sub'
    )
  );

-- Admin vê todos
CREATE POLICY "admin_view_all_pacientes" ON paciente
  FOR SELECT
  USING (auth.jwt() ->> 'role' = 'ADMIN');
```

#### **Tabela: exame_me**

```sql
ALTER TABLE exame_me ENABLE ROW LEVEL SECURITY;

-- Médico vê exames de seus pacientes
CREATE POLICY "staff_view_exames" ON exame_me
  FOR SELECT
  USING (
    protocolo_me_id IN (
      SELECT id FROM protocolo_me
      WHERE paciente_id IN (
        SELECT id FROM paciente
        WHERE hospital_id IN (
          SELECT hospital_id FROM usuario
          WHERE email = auth.jwt() ->> 'sub'
        )
      )
    )
  );
```

---

## 📋 Plano de Ação

### **Fase 1: RLS Supabase (Imediato - CRÍTICO)**

- [ ] Criar migrations SQL para habilitar RLS em todas as 11 tabelas
- [ ] Definir policies para cada tabela (usuário, paciente, protocolo, etc)
- [ ] Testar acesso com usuarios_role diferente
- [ ] Documentar políticas

**Tempo Estimado:** 2-3 horas

---

### **Fase 2: Isolamento de Dados Backend (Curto Prazo)**

- [ ] Adicionar filtros de `hospitalId` em todo PackService
- [ ] Adicionar `usuarioId` em `PacienteService`, `ProtocoloService`
- [ ] Criar `@CurrentUser` annotation para extrair usuário do JWT
- [ ] Filtrar automaticamente em `findAll()` queries

**Tempo Estimado:** 3-4 horas

---

### **Fase 3: Auditoria (Curto Prazo)**

- [ ] Criar tabela `audit_log(id, usuario_id, acao, tabela, registro_id, antes, depois, timestamp)`
- [ ] Adicionar `@Audit` interceptor em todos os Services
- [ ] Implementar soft delete (coluna `deleted_at`)
- [ ] Dashboard de auditoria no frontend

**Tempo Estimado:** 4-6 horas

---

### **Fase 4: Rate Limiting (Médio Prazo)**

- [ ] Implementar Spring Security Rate Limiting
- [ ] Configurar max 5 tentativas de login/5 minutos
- [ ] Add 10-second delay após falhas

**Tempo Estimado:** 1-2 horas

---

### **Fase 5: Email Confirmation (Médio Prazo)**

- [ ] Criar tabela `email_confirmation`
- [ ] Implementar endpoint para confirmar email
- [ ] Enviar email com link de confirmação

**Tempo Estimado:** 3-4 horas

---

## ✅ Checklist de Segurança

| Item | Status | Prioridade |
|------|--------|----------|
| RLS em usuario | ❌ | 🔴 CRÍTICA |
| RLS em pacientes | ❌ | 🔴 CRÍTICA |
| RLS em exame_me | ❌ | 🔴 CRÍTICA |
| RLS em protocolos | ❌ | 🔴 CRÍTICA |
| Isolamento por hospital | ❌ | 🟠 ALTA |
| Auditoria (quem fez?) | ❌ | 🟠 ALTA |
| Rate limiting login | ❌ | 🟠 ALTA |
| Email confirmation | ❌ | 🟡 MÉDIA |
| Soft delete | ❌ | 🟡 MÉDIA |
| JWT token refresh | ❌ | 🟡 MÉDIA |

---

## 📝 Conclusão

**Status Atual:** ⚠️ **INSEGURO PARA PRODUÇÃO**

- ✅ Autenticação JWT é sólida
- ✅ Criptografia de senha é boa
- ✅ RBAC é bem implementado
- ✅ Admin principal é protegido
- ❌ **RLS é CRÍTICO** - Deve ser implementado IMEDIATAMENTE
- ❌ Sem isolamento de dados por usuário
- ❌ Sem auditoria de ações
- ❌ Sem rate limiting

**Recomendação:** Não considerar como em produção até Phase 1 (RLS) estar completa.

---

**Próximo Passo:** Confirmar se deseja implementar RLS no Supabase agora?
