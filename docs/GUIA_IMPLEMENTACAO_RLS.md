# 🔐 Guia Prático: Implementar RLS no Supabase

**Status:** 📋 Passo-a-passo para implementação
**Tempo Estimado:** 30-45 minutos
**Nível:** Intermediário

---

## 📌 Resumo Executivo

- **O Problema:** Suas 11 tabelas estão públicas (SEM RLS) - qualquer um com acesso ao banco pode ler todos os dados
- **A Solução:** Implementar Row-Level Security (RLS) para isolar dados por usuário/hospital
- **O Benefício:** Proteção adicional no nível do banco de dados (segunda linha de defesa)

---

## 🚀 Pré-Requisitos

1. ✅ Acesso ao console Supabase do projeto
2. ✅ Permissão de admin no Supabase
3. ✅ Backup recente do banco (recomendado)
4. ✅ Ambiente de testes para validar RLS antes de ir ao prod

---

## 📋 Passo 1: Acessar Supabase SQL Editor

1. Ir para: [supabase.com/dashboard](https://supabase.com/dashboard)
2. Selecionar seu projeto "protocolo-me"
3. No menu à esquerda, clicar em **"SQL Editor"**
4. Clicar em **"New Query"**

![Supabase SQL Editor]

---

## 📋 Passo 2: Fazer Backup (Segurança Primeiro)

```sql
-- Examinar as tabelas atuais
SELECT tablename FROM pg_tables
WHERE schemaname = 'public'
ORDER BY tablename;

-- Saída esperada:
-- anexo_documento
-- central_hospitais
-- central_transplantes
-- doacao
-- estatistica_protocolo_me
-- exame_me
-- hospital
-- orgao_doado
-- paciente
-- protocolo_me
-- usuario
```

**No Supabase Dashboard:**
- Ir para **Settings → Backups**
- Clicar em **Create a manual backup**
- Esperar completar

---

## 📋 Passo 3: Implementar RLS (Fase 1 - CRÍTICA)

### **3a. Tabela: USUARIO**

Copiar e colar no SQL Editor:

```sql
-- ==========================================
-- USUARIO - RLS POLICIES
-- ==========================================

ALTER TABLE usuario ENABLE ROW LEVEL SECURITY;

-- Admin vê todos os usuários
CREATE POLICY "admin_view_all_users" ON usuario
  FOR SELECT
  USING ((auth.jwt() ->> 'role') = 'ADMIN');

-- Usuário vê apenas a si mesmo
CREATE POLICY "user_view_own_profile" ON usuario
  FOR SELECT
  USING ((auth.jwt() ->> 'sub') = email);

-- Central vê médicos e enfermeiros
CREATE POLICY "central_view_staff" ON usuario
  FOR SELECT
  USING (
    (auth.jwt() ->> 'role') = 'CENTRAL_TRANSPLANTES'
    AND role IN ('MEDICO', 'ENFERMEIRO')
  );

-- Admin pode atualizar usuários
CREATE POLICY "admin_update_users" ON usuario
  FOR UPDATE
  USING ((auth.jwt() ->> 'role') = 'ADMIN');

-- Usuário atualiza seu próprio perfil
CREATE POLICY "user_update_own_profile" ON usuario
  FOR UPDATE
  USING ((auth.jwt() ->> 'sub') = email);

-- Admin pode deletar
CREATE POLICY "admin_delete_users" ON usuario
  FOR DELETE
  USING ((auth.jwt() ->> 'role') = 'ADMIN');
```

**Clicar em "Run"** ✅

---

### **3b. Tabela: PACIENTE**

```sql
-- ==========================================
-- PACIENTE - RLS POLICIES
-- ==========================================

ALTER TABLE paciente ENABLE ROW LEVEL SECURITY;

-- Admin vê todos
CREATE POLICY "admin_view_all_pacientes" ON paciente
  FOR SELECT
  USING ((auth.jwt() ->> 'role') = 'ADMIN');

-- Central vê todos
CREATE POLICY "central_view_all_pacientes" ON paciente
  FOR SELECT
  USING ((auth.jwt() ->> 'role') = 'CENTRAL_TRANSPLANTES');

-- Médico/Enfermeiro veem pacientes de seu hospital
CREATE POLICY "staff_view_own_hospital_pacientes" ON paciente
  FOR SELECT
  USING (
    hospital_id IN (
      SELECT hospital_id FROM usuario
      WHERE (auth.jwt() ->> 'sub') = email
        AND role IN ('MEDICO', 'ENFERMEIRO')
    )
  );

-- Admin atualiza
CREATE POLICY "admin_update_pacientes" ON paciente
  FOR UPDATE
  USING ((auth.jwt() ->> 'role') = 'ADMIN');

-- Staff atualiza seu hospital
CREATE POLICY "staff_update_own_hospital_pacientes" ON paciente
  FOR UPDATE
  USING (
    hospital_id IN (
      SELECT hospital_id FROM usuario
      WHERE (auth.jwt() ->> 'sub') = email
        AND role IN ('MEDICO', 'ENFERMEIRO')
    )
  );
```

**Clicar em "Run"** ✅

---

### **3c. Tabela: PROTOCOLO_ME**

```sql
-- ==========================================
-- PROTOCOLO_ME - RLS POLICIES
-- ==========================================

ALTER TABLE protocolo_me ENABLE ROW LEVEL SECURITY;

-- Admin vê todos
CREATE POLICY "admin_view_all_protocolos" ON protocolo_me
  FOR SELECT
  USING ((auth.jwt() ->> 'role') = 'ADMIN');

-- Central vê todos
CREATE POLICY "central_view_all_protocolos" ON protocolo_me
  FOR SELECT
  USING ((auth.jwt() ->> 'role') = 'CENTRAL_TRANSPLANTES');

-- Médico vê protocolos de seus pacientes
CREATE POLICY "staff_view_own_hospital_protocolos" ON protocolo_me
  FOR SELECT
  USING (
    paciente_id IN (
      SELECT id FROM paciente
      WHERE hospital_id IN (
        SELECT hospital_id FROM usuario
        WHERE (auth.jwt() ->> 'sub') = email
          AND role IN ('MEDICO', 'ENFERMEIRO')
      )
    )
  );

-- Admin atualiza
CREATE POLICY "admin_update_protocolos" ON protocolo_me
  FOR UPDATE
  USING ((auth.jwt() ->> 'role') = 'ADMIN');

-- Staff atualiza
CREATE POLICY "staff_update_own_hospital_protocolos" ON protocolo_me
  FOR UPDATE
  USING (
    paciente_id IN (
      SELECT id FROM paciente
      WHERE hospital_id IN (
        SELECT hospital_id FROM usuario
        WHERE (auth.jwt() ->> 'sub') = email
          AND role IN ('MEDICO', 'ENFERMEIRO')
      )
    )
  );
```

**Clicar em "Run"** ✅

---

### **3d. Tabela: EXAME_ME**

```sql
-- ==========================================
-- EXAME_ME - RLS POLICIES
-- ==========================================

ALTER TABLE exame_me ENABLE ROW LEVEL SECURITY;

-- Admin vê todos
CREATE POLICY "admin_view_all_exames" ON exame_me
  FOR SELECT
  USING ((auth.jwt() ->> 'role') = 'ADMIN');

-- Central vê todos
CREATE POLICY "central_view_all_exames" ON exame_me
  FOR SELECT
  USING ((auth.jwt() ->> 'role') = 'CENTRAL_TRANSPLANTES');

-- Médico vê exames de seus pacientes
CREATE POLICY "staff_view_own_hospital_exames" ON exame_me
  FOR SELECT
  USING (
    protocolo_me_id IN (
      SELECT id FROM protocolo_me
      WHERE paciente_id IN (
        SELECT id FROM paciente
        WHERE hospital_id IN (
          SELECT hospital_id FROM usuario
          WHERE (auth.jwt() ->> 'sub') = email
            AND role IN ('MEDICO', 'ENFERMEIRO')
        )
      )
    )
  );

-- Admin atualiza
CREATE POLICY "admin_update_exames" ON exame_me
  FOR UPDATE
  USING ((auth.jwt() ->> 'role') = 'ADMIN');

-- Staff atualiza
CREATE POLICY "staff_update_exames" ON exame_me
  FOR UPDATE
  USING (
    protocolo_me_id IN (
      SELECT id FROM protocolo_me
      WHERE paciente_id IN (
        SELECT id FROM paciente
        WHERE hospital_id IN (
          SELECT hospital_id FROM usuario
          WHERE (auth.jwt() ->> 'sub') = email
            AND role IN ('MEDICO', 'ENFERMEIRO')
        )
      )
    )
  );
```

**Clicar em "Run"** ✅

---

### **3e. Tabelas Restantes (RÁPIDO)**

Execute este script para as outras 7 tabelas:

```sql
-- HOSPITAL (usuários podem visualizar todos, admin atualiza)
ALTER TABLE hospital ENABLE ROW LEVEL SECURITY;
CREATE POLICY "anyone_view_hospitals" ON hospital FOR SELECT USING (true);
CREATE POLICY "admin_update_hospitals" ON hospital FOR UPDATE USING ((auth.jwt() ->> 'role') = 'ADMIN');

-- CENTRAL_TRANSPLANTES
ALTER TABLE central_transplantes ENABLE ROW LEVEL SECURITY;
CREATE POLICY "anyone_view_centrais" ON central_transplantes FOR SELECT USING (true);
CREATE POLICY "admin_update_centrais" ON central_transplantes FOR UPDATE USING ((auth.jwt() ->> 'role') = 'ADMIN');

-- ORGAO_DOADO
ALTER TABLE orgao_doado ENABLE ROW LEVEL SECURITY;
CREATE POLICY "admin_view_all_orgaos" ON orgao_doado FOR SELECT USING ((auth.jwt() ->> 'role') = 'ADMIN');
CREATE POLICY "central_view_all_orgaos" ON orgao_doado FOR SELECT USING ((auth.jwt() ->> 'role') = 'CENTRAL_TRANSPLANTES');
CREATE POLICY "staff_view_own_orgaos" ON orgao_doado FOR SELECT USING (
  protocolo_me_id IN (
    SELECT id FROM protocolo_me
    WHERE paciente_id IN (
      SELECT id FROM paciente
      WHERE hospital_id IN (
        SELECT hospital_id FROM usuario
        WHERE (auth.jwt() ->> 'sub') = email AND role IN ('MEDICO', 'ENFERMEIRO')
      )
    )
  )
);

-- DOACAO
ALTER TABLE doacao ENABLE ROW LEVEL SECURITY;
CREATE POLICY "admin_view_all_doacoes" ON doacao FOR SELECT USING ((auth.jwt() ->> 'role') = 'ADMIN');
CREATE POLICY "central_view_all_doacoes" ON doacao FOR SELECT USING ((auth.jwt() ->> 'role') = 'CENTRAL_TRANSPLANTES');

-- ANEXO_DOCUMENTO
ALTER TABLE anexo_documento ENABLE ROW LEVEL SECURITY;
CREATE POLICY "admin_view_all_anexos" ON anexo_documento FOR SELECT USING ((auth.jwt() ->> 'role') = 'ADMIN');
CREATE POLICY "staff_view_own_anexos" ON anexo_documento FOR SELECT USING (
  registro_id IN (
    SELECT id FROM paciente
    WHERE hospital_id IN (
      SELECT hospital_id FROM usuario
      WHERE (auth.jwt() ->> 'sub') = email AND role IN ('MEDICO', 'ENFERMEIRO')
    )
  )
);

-- ESTATISTICA_PROTOCOLO_ME
ALTER TABLE estatistica_protocolo_me ENABLE ROW LEVEL SECURITY;
CREATE POLICY "admin_view_all_stats" ON estatistica_protocolo_me FOR SELECT USING ((auth.jwt() ->> 'role') = 'ADMIN');
CREATE POLICY "central_view_all_stats" ON estatistica_protocolo_me FOR SELECT USING ((auth.jwt() ->> 'role') = 'CENTRAL_TRANSPLANTES');
CREATE POLICY "staff_view_own_stats" ON estatistica_protocolo_me FOR SELECT USING (
  protocolo_me_id IN (
    SELECT id FROM protocolo_me
    WHERE paciente_id IN (
      SELECT id FROM paciente
      WHERE hospital_id IN (
        SELECT hospital_id FROM usuario
        WHERE (auth.jwt() ->> 'sub') = email AND role IN ('MEDICO', 'ENFERMEIRO')
      )
    )
  )
);

-- CENTRAL_HOSPITAIS
ALTER TABLE central_hospitais ENABLE ROW LEVEL SECURITY;
CREATE POLICY "anyone_view_central_hospitais" ON central_hospitais FOR SELECT USING (true);
CREATE POLICY "admin_update_central_hospitais" ON central_hospitais FOR UPDATE USING ((auth.jwt() ->> 'role') = 'ADMIN');
```

**Clicar em "Run"** ✅

---

## 📋 Passo 4: Validar RLS

```sql
-- Verificar que RLS foi habilitado em todas as tabelas
SELECT
  schemaname,
  tablename,
  rowsecurity
FROM pg_tables
WHERE schemaname = 'public'
ORDER BY tablename;

-- Esperado: rowsecurity = true para todas

-- Contar políticas criadas
SELECT
  schemaname,
  tablename,
  COUNT(*) as policy_count
FROM pg_policies
WHERE schemaname = 'public'
GROUP BY schemaname, tablename
ORDER BY tablename DESC;

-- Esperado: ~50 policies no total
```

**Resultado esperado:**
- ✅ usuario: RLS enabled, 6 policies
- ✅ paciente: RLS enabled, 5 policies
- ✅ protocolo_me: RLS enabled, 5 policies
- ✅ exame_me: RLS enabled, 5 policies
- ✅ hospital: RLS enabled, 2 policies
- ✅ E mais 6 tabelas...

---

## ⚠️ Passo 5: Testar RLS (Crítico!)

### **Teste 1: Admin vê tudo**

```javascript
// No console do navegador ou no postman
const token = localStorage.getItem('token'); // seu JWT de admin

const response = await fetch('https://seus-supabase.com/api/usuarios', {
  headers: { 'Authorization': `Bearer ${token}` }
});

// Esperado: Lista de TODOS os usuários
```

### **Teste 2: Médico A não vê Médico B**

```javascript
// Médico A (hospital_id = 1)
const medico_a_token = "jwt_medico_a";

const response = await fetch('https://seus-supabase.com/api/pacientes', {
  headers: { 'Authorization': `Bearer ${medico_a_token}` }
});

// Esperado: APENAS pacientes com hospital_id = 1
// NÃO deve retornar pacientes de outros hospitais
```

### **Teste 3: RLS bloqueia acesso direto**

```sql
-- Se você tentar rodar esta query E NÃO FOR ADMIN:
SELECT * FROM paciente WHERE hospital_id = 999;

-- Esperado: 0 linhas (RLS filtra automaticamente)
```

---

## 🚨 Passo 6: Possíveis Problemas & Soluções

### **Problema 1: "Permission denied for schema public"**

**Causa:** Usuário de app não tem permissão

**Solução:**
```sql
-- Run como ADMIN (via Supabase)
GRANT USAGE ON SCHEMA public TO authenticated;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO authenticated;
```

---

### **Problema 2: Queries ficam MUITO lentas**

**Causa:** RLS com JOINs complexos pesa

**Solução:** Adicionar indexes
```sql
-- Criar índices para as colunas usadas no RLS
CREATE INDEX idx_usuario_email ON usuario(email);
CREATE INDEX idx_usuario_hospital_id ON usuario(hospital_id);
CREATE INDEX idx_paciente_hospital_id ON paciente(hospital_id);
CREATE INDEX idx_protocolo_me_paciente_id ON protocolo_me(paciente_id);
CREATE INDEX idx_exame_me_protocolo_id ON exame_me(protocolo_me_id);

ANALYZE;  -- Recalcular plano de execução
```

---

### **Problema 3: RLS funciona mas JWT não passa o 'role'**

**Causa:** Backend não está codificando o role no JWT

**Solução:** Verificar `/backend/src/main/java/back/backend/security/JwtUtil.java`

```java
// VERIFICAR se o role está sendo adicionado ao JWT:
public String gerarToken(String email, String role) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("role", role);  // ✅ Isto está aqui?
    claims.put("type", "access");
    return createToken(claims, email);
}
```

Se não estiver, adicionar a coluna `role` ao JWT decoder no `JwtFilter`.

---

## ✅ Passo 7: Checklist Final

- [ ] Backup criado no Supabase
- [ ] RLS habilitado em todas as 11 tabelas
- [ ] Policies criadas e testadas
- [ ] Admin consegue ver todos os dados
- [ ] Médico A não vê dados de Médico B
- [ ] Performance aceitável (queries < 200ms)
- [ ] Indexes criados nas colunas críticas
- [ ] Documentação atualizada

---

## 📝 Próximos Passos

### **Fase 2 (Curto Prazo):**
- [ ] Adicionar filtros de `hospitalId` em `PacienteService`
- [ ] Criar `@CurrentUser` annotation no backend
- [ ] Filtrar automaticamente por usuário logado

### **Fase 3 (Médio Prazo):**
- [ ] Implementar auditoria (quem fez quê?)
- [ ] Criar tabela de `audit_log`
- [ ] Rate limiting para login

### **Fase 4 (Longo Prazo):**
- [ ] Email confirmation flow
- [ ] JWT refresh tokens
- [ ] Two-factor authentication

---

## 🆘 Suporte

Caso encontre problemas:

1. **Verificar logs do Supabase:**
   - Dashboard → Logs → Database (tabela, statement, etc)

2. **Testar queries manualmente:**
   - SQL Editor → Test queries sem JWT
   - Verificar se RLS está bloqueando

3. **Documentação:**
   - [Supabase RLS Docs](https://supabase.com/docs/guides/auth/row-level-security)
   - [PostgreSQL RLS](https://www.postgresql.org/docs/current/ddl-rowsecurity.html)

---

**Status:** Pronto para implementar? Qualquer dúvida, avisar! 🚀
