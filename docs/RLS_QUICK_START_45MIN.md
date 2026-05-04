# 🎯 PLANO DE AÇÃO RÁPIDO - Implementar RLS Hoje

**Tempo Total:** 45 minutos
**Dificuldade:** ⭐⭐ Fácil
**Risco:** ✅ Baixo (reversível)

---

## ✋ PARE AQUI E LEIA

Seu banco de dados está **PÚBLICO**. Qualquer pessoa com token pode acessar:
- ❌ Todos os usuários (incluindo senhas hash?)
- ❌ Todos os pacientes (violação de privacidade!)
- ❌ Todos os protocolos médicos
- ❌ Todos os exames

**Isto viola LGPD e é CRÍTICO.**

A solução leva **30 minutos**.

---

## 🚀 PASSO-A-PASSO (Copy & Paste)

### **1️⃣ ANTES DE COMEÇAR (5 min)**

#### Passo 1.1: Fazer Backup

1. Abrir https://supabase.com/dashboard
2. Selecionar projeto "protocolo-me"
3. **Settings** → **Backups**
4. Clicar em **"Create a manual backup"**
5. Esperar 2-3 minutos até aparecer ✅

![Backup criado]

---

#### Passo 1.2: Abrir SQL Editor

1. No painel esquerdo, clicar em **"SQL Editor"**
2. Clicar em **"New Query"**
3. Limpar a area de texto (delete all)

![SQL Editor open]

---

### **2️⃣ COPIAR & COLAR SQL (30 min)**

Copiar **TODO ESTE BLOCO** de SQL e colar no editor Supabase:

```sql
-- ==========================================
-- RLS IMPLEMENTATION - PROTOCOLO.ME
-- ==========================================

-- ==========================================
-- 1. TABELA: USUARIO
-- ==========================================

ALTER TABLE usuario ENABLE ROW LEVEL SECURITY;

CREATE POLICY "admin_view_all_users" ON usuario
  FOR SELECT
  USING ((auth.jwt() ->> 'role') = 'ADMIN');

CREATE POLICY "user_view_own_profile" ON usuario
  FOR SELECT
  USING ((auth.jwt() ->> 'sub') = email);

CREATE POLICY "central_view_staff" ON usuario
  FOR SELECT
  USING (
    (auth.jwt() ->> 'role') = 'CENTRAL_TRANSPLANTES'
    AND role IN ('MEDICO', 'ENFERMEIRO')
  );

CREATE POLICY "admin_update_users" ON usuario
  FOR UPDATE
  USING ((auth.jwt() ->> 'role') = 'ADMIN');

CREATE POLICY "user_update_own_profile" ON usuario
  FOR UPDATE
  USING ((auth.jwt() ->> 'sub') = email);

CREATE POLICY "admin_delete_users" ON usuario
  FOR DELETE
  USING ((auth.jwt() ->> 'role') = 'ADMIN');

-- ==========================================
-- 2. TABELA: PACIENTE
-- ==========================================

ALTER TABLE paciente ENABLE ROW LEVEL SECURITY;

CREATE POLICY "admin_view_all_pacientes" ON paciente
  FOR SELECT
  USING ((auth.jwt() ->> 'role') = 'ADMIN');

CREATE POLICY "central_view_all_pacientes" ON paciente
  FOR SELECT
  USING ((auth.jwt() ->> 'role') = 'CENTRAL_TRANSPLANTES');

CREATE POLICY "staff_view_own_hospital_pacientes" ON paciente
  FOR SELECT
  USING (
    hospital_id IN (
      SELECT hospital_id FROM usuario
      WHERE (auth.jwt() ->> 'sub') = email
        AND role IN ('MEDICO', 'ENFERMEIRO')
    )
  );

CREATE POLICY "admin_update_pacientes" ON paciente
  FOR UPDATE
  USING ((auth.jwt() ->> 'role') = 'ADMIN');

CREATE POLICY "staff_update_own_hospital_pacientes" ON paciente
  FOR UPDATE
  USING (
    hospital_id IN (
      SELECT hospital_id FROM usuario
      WHERE (auth.jwt() ->> 'sub') = email
        AND role IN ('MEDICO', 'ENFERMEIRO')
    )
  );

-- ==========================================
-- 3. TABELA: PROTOCOLO_ME
-- ==========================================

ALTER TABLE protocolo_me ENABLE ROW LEVEL SECURITY;

CREATE POLICY "admin_view_all_protocolos" ON protocolo_me
  FOR SELECT
  USING ((auth.jwt() ->> 'role') = 'ADMIN');

CREATE POLICY "central_view_all_protocolos" ON protocolo_me
  FOR SELECT
  USING ((auth.jwt() ->> 'role') = 'CENTRAL_TRANSPLANTES');

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

CREATE POLICY "admin_update_protocolos" ON protocolo_me
  FOR UPDATE
  USING ((auth.jwt() ->> 'role') = 'ADMIN');

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

-- ==========================================
-- 4. TABELA: EXAME_ME
-- ==========================================

ALTER TABLE exame_me ENABLE ROW LEVEL SECURITY;

CREATE POLICY "admin_view_all_exames" ON exame_me
  FOR SELECT
  USING ((auth.jwt() ->> 'role') = 'ADMIN');

CREATE POLICY "central_view_all_exames" ON exame_me
  FOR SELECT
  USING ((auth.jwt() ->> 'role') = 'CENTRAL_TRANSPLANTES');

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

CREATE POLICY "admin_update_exames" ON exame_me
  FOR UPDATE
  USING ((auth.jwt() ->> 'role') = 'ADMIN');

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

-- ==========================================
-- 5. TABELAS RÁPIDAS
-- ==========================================

-- HOSPITAL
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
  protocolo_me_id IN (SELECT id FROM protocolo_me WHERE paciente_id IN (
    SELECT id FROM paciente WHERE hospital_id IN (
      SELECT hospital_id FROM usuario WHERE (auth.jwt() ->> 'sub') = email
        AND role IN ('MEDICO', 'ENFERMEIRO'))))
);

-- DOACAO
ALTER TABLE doacao ENABLE ROW LEVEL SECURITY;
CREATE POLICY "admin_view_all_doacoes" ON doacao FOR SELECT USING ((auth.jwt() ->> 'role') = 'ADMIN');
CREATE POLICY "central_view_all_doacoes" ON doacao FOR SELECT USING ((auth.jwt() ->> 'role') = 'CENTRAL_TRANSPLANTES');

-- ANEXO_DOCUMENTO
ALTER TABLE anexo_documento ENABLE ROW LEVEL SECURITY;
CREATE POLICY "admin_view_all_anexos" ON anexo_documento FOR SELECT USING ((auth.jwt() ->> 'role') = 'ADMIN');
CREATE POLICY "staff_view_own_anexos" ON anexo_documento FOR SELECT USING (
  registro_id IN (SELECT id FROM paciente WHERE hospital_id IN (
    SELECT hospital_id FROM usuario WHERE (auth.jwt() ->> 'sub') = email
      AND role IN ('MEDICO', 'ENFERMEIRO')))
);

-- ESTATISTICA_PROTOCOLO_ME
ALTER TABLE estatistica_protocolo_me ENABLE ROW LEVEL SECURITY;
CREATE POLICY "admin_view_all_stats" ON estatistica_protocolo_me FOR SELECT USING ((auth.jwt() ->> 'role') = 'ADMIN');
CREATE POLICY "central_view_all_stats" ON estatistica_protocolo_me FOR SELECT USING ((auth.jwt() ->> 'role') = 'CENTRAL_TRANSPLANTES');
CREATE POLICY "staff_view_own_stats" ON estatistica_protocolo_me FOR SELECT USING (
  protocolo_me_id IN (SELECT id FROM protocolo_me WHERE paciente_id IN (
    SELECT id FROM paciente WHERE hospital_id IN (
      SELECT hospital_id FROM usuario WHERE (auth.jwt() ->> 'sub') = email
        AND role IN ('MEDICO', 'ENFERMEIRO'))))
);

-- CENTRAL_HOSPITAIS
ALTER TABLE central_hospitais ENABLE ROW LEVEL SECURITY;
CREATE POLICY "anyone_view_central_hospitais" ON central_hospitais FOR SELECT USING (true);
CREATE POLICY "admin_update_central_hospitais" ON central_hospitais FOR UPDATE USING ((auth.jwt() ->> 'role') = 'ADMIN');

-- VALIDAÇÃO
SELECT COUNT(*) as tabelas_com_rls FROM pg_tables
WHERE schemaname = 'public' AND rowsecurity = true;
-- Esperado: 11
```

**Agora:**
1. COLAR TODO O CÓDIGO ACIMA no Supabase SQL Editor
2. Posicionar cursor no final
3. Clicar **"Run"** (Ctrl+Enter)
4. Esperar executar (pode levar 1-2 minutos)

![Run SQL]

---

### **3️⃣ VALIDAR EXECUÇÃO (5 min)**

Após clicar "Run", você deve ver:

```
✅ Query successful (11 rows)
```

Isto significa que as 11 tabelas foram protegidas!

Se vir ERRO, reportar aqui qual foi exatamente.

---

### **4️⃣ TESTAR SE FUNCIONOU (5 min)**

Executar esta query de validação:

```sql
SELECT
  schemaname,
  tablename,
  rowsecurity
FROM pg_tables
WHERE schemaname = 'public'
ORDER BY tablename;
```

**Resultado esperado:**
```
public | anexo_documento | t
public | central_hospitais | t
public | central_transplantes | t
public | doacao | t
public | estatistica_protocolo_me | t
public | exame_me | t
public | hospital | t
public | orgao_doado | t
public | paciente | t
public | protocolo_me | t
public | usuario | t
```

Se todos têm `t` (true), você está pronto! ✅

---

## ✅ FIM!

Parabéns! 🎉

**Você implementou:**
- ✅ RLS em 11 tabelas
- ✅ Isolamento automático de dados
- ✅ Proteção LGPD

**Que foi feito:**
- ✅ Admin vê TODOS os dados
- ✅ Médico A vê APENAS Hospital A
- ✅ Médico B vê APENAS Hospital B
- ✅ Enfermeiro isolado por hospital
- ✅ Central vê tudo (exceto outros roles)

---

## ⚠️ PRÓXIMOS PASSOS

1. **Testar aplicação normalmente**
   - [ ] Logar com admin
   - [ ] Logar com médico
   - [ ] Verificar que dados estão filtrados

2. **Performance**
   - [ ] Queries ficaram lentas? Se sim, ver GUIA_IMPLEMENTACAO_RLS.md seção "Adicionar Indexes"

3. **Próxima semana:**
   - [ ] Adicionar isolamento no backend também
   - [ ] Implementar auditoria
   - [ ] Rate limiting no login

---

## 📖 MAIS INFORMAÇÕES

Se quiser entender TUDO que foi feito:

Arquivo | Descrição
--------|----------
[RELATORIO_FINAL_VERIFICACAO_USUARIOS_RLS.md](./RELATORIO_FINAL_VERIFICACAO_USUARIOS_RLS.md) | Resumo visual (este!)
[RESUMO_SEGURANCA_ACOES_IMEDIATAS.md](./RESUMO_SEGURANCA_ACOES_IMEDIATAS.md) | Executivo (ações + ROI)
[RELATORIO_SEGURANCA_USUARIOS_RLS.md](./RELATORIO_SEGURANCA_USUARIOS_RLS.md) | Análise técnica profunda (50 pág)
[GUIA_IMPLEMENTACAO_RLS.md](./GUIA_IMPLEMENTACAO_RLS.md) | Guia detalhado com troubleshooting
[CHECKLIST_TESTES_RLS.md](./CHECKLIST_TESTES_RLS.md) | 42 testes para validar

---

## 🆘 Problemas?

Se algo deu errado:

1. **Verificar erro exato** no Supabase
2. **Reverter:** `ALTER TABLE usuario DISABLE ROW LEVEL SECURITY;`
3. **Restaurar backup:** Settings → Backups → Restore
4. **Consultar:** GUIA_IMPLEMENTACAO_RLS.md seção "Problemas"

---

**Pronto? 🚀**

Executar SQL agora e me avisar quando terminar!
