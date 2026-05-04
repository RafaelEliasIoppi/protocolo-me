-- ========================================
-- RLS IMPLEMENTATION - PROTOCOLO.ME
-- ========================================
-- Este arquivo contém as migrations para habilitar
-- Row Level Security em todas as tabelas do projeto.
--
-- ⚠️ ANTES DE EXECUTAR:
-- 1. Fazer backup do banco de dados
-- 2. Testar em ambiente de staged primeiro
-- 3. Validar todas as políticas após implementar
--
-- ========================================

-- ==========================================
-- 1. TABELA: USUARIO
-- ==========================================

-- Desabilitar temporariamente para criar policies
ALTER TABLE usuario DISABLE ROW LEVEL SECURITY;

-- Habilitar RLS
ALTER TABLE usuario ENABLE ROW LEVEL SECURITY;

-- Policy 1: Admin vê todos os usuários
CREATE POLICY "admin_view_all_users" ON usuario
  FOR SELECT
  USING (
    -- Se o JWT indica um ADMIN
    (auth.jwt() ->> 'role') = 'ADMIN'
  );

-- Policy 2: Usuário vê apenas a si mesmo
CREATE POLICY "user_view_own_profile" ON usuario
  FOR SELECT
  USING (
    -- JWT subject (email) = email do usuario
    (auth.jwt() ->> 'sub') = email
  );

-- Policy 3: Central de Transplantes vê staff
CREATE POLICY "central_view_staff" ON usuario
  FOR SELECT
  USING (
    (auth.jwt() ->> 'role') = 'CENTRAL_TRANSPLANTES'
    AND role IN ('MEDICO', 'ENFERMEIRO')
  );

-- Policy 4: Admin pode atualizar
CREATE POLICY "admin_update_users" ON usuario
  FOR UPDATE
  USING (
    (auth.jwt() ->> 'role') = 'ADMIN'
  );

-- Policy 5: Usuário pode atualizar seu próprio perfil
CREATE POLICY "user_update_own_profile" ON usuario
  FOR UPDATE
  USING (
    (auth.jwt() ->> 'sub') = email
  );

-- Policy 6: Admin pode deletar
CREATE POLICY "admin_delete_users" ON usuario
  FOR DELETE
  USING (
    (auth.jwt() ->> 'role') = 'ADMIN'
  );

SELECT 'Usuario RLS policies created' AS status;

-- ==========================================
-- 2. TABELA: PACIENTE
-- ==========================================

ALTER TABLE paciente ENABLE ROW LEVEL SECURITY;

-- Policy 1: Admin vê todos
CREATE POLICY "admin_view_all_pacientes" ON paciente
  FOR SELECT
  USING (
    (auth.jwt() ->> 'role') = 'ADMIN'
  );

-- Policy 2: Central de Transplantes vê todos
CREATE POLICY "central_view_all_pacientes" ON paciente
  FOR SELECT
  USING (
    (auth.jwt() ->> 'role') = 'CENTRAL_TRANSPLANTES'
  );

-- Policy 3: Médico/Enfermeiro veem pacientes do seu hospital
CREATE POLICY "staff_view_own_hospital_pacientes" ON paciente
  FOR SELECT
  USING (
    hospital_id IN (
      SELECT hospital_id FROM usuario
      WHERE (auth.jwt() ->> 'sub') = email
        AND role IN ('MEDICO', 'ENFERMEIRO')
    )
  );

-- Policy 4: Admin atualiza
CREATE POLICY "admin_update_pacientes" ON paciente
  FOR UPDATE
  USING (
    (auth.jwt() ->> 'role') = 'ADMIN'
  );

-- Policy 5: Médico/Enfermeiro atualiza seu hospital
CREATE POLICY "staff_update_own_hospital_pacientes" ON paciente
  FOR UPDATE
  USING (
    hospital_id IN (
      SELECT hospital_id FROM usuario
      WHERE (auth.jwt() ->> 'sub') = email
        AND role IN ('MEDICO', 'ENFERMEIRO')
    )
  );

SELECT 'Paciente RLS policies created' AS status;

-- ==========================================
-- 3. TABELA: PROTOCOLO_ME
-- ==========================================

ALTER TABLE protocolo_me ENABLE ROW LEVEL SECURITY;

-- Policy 1: Admin vê todos
CREATE POLICY "admin_view_all_protocolos" ON protocolo_me
  FOR SELECT
  USING (
    (auth.jwt() ->> 'role') = 'ADMIN'
  );

-- Policy 2: Central vê todos
CREATE POLICY "central_view_all_protocolos" ON protocolo_me
  FOR SELECT
  USING (
    (auth.jwt() ->> 'role') = 'CENTRAL_TRANSPLANTES'
  );

-- Policy 3: Médico vê protocolos de seus pacientes
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

-- Policy 4: Admin atualiza
CREATE POLICY "admin_update_protocolos" ON protocolo_me
  FOR UPDATE
  USING (
    (auth.jwt() ->> 'role') = 'ADMIN'
  );

-- Policy 5: Médico atualiza seu hospital
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

SELECT 'Protocolo_me RLS policies created' AS status;

-- ==========================================
-- 4. TABELA: EXAME_ME
-- ==========================================

ALTER TABLE exame_me ENABLE ROW LEVEL SECURITY;

-- Policy 1: Admin vê todos
CREATE POLICY "admin_view_all_exames" ON exame_me
  FOR SELECT
  USING (
    (auth.jwt() ->> 'role') = 'ADMIN'
  );

-- Policy 2: Central vê todos
CREATE POLICY "central_view_all_exames" ON exame_me
  FOR SELECT
  USING (
    (auth.jwt() ->> 'role') = 'CENTRAL_TRANSPLANTES'
  );

-- Policy 3: Médico vê exames de seus pacientes
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

-- Policy 4: Admin atualiza
CREATE POLICY "admin_update_exames" ON exame_me
  FOR UPDATE
  USING (
    (auth.jwt() ->> 'role') = 'ADMIN'
  );

-- Policy 5: Médico atualiza
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

SELECT 'Exame_me RLS policies created' AS status;

-- ==========================================
-- 5. TABELA: HOSPITAL
-- ==========================================

ALTER TABLE hospital ENABLE ROW LEVEL SECURITY;

-- Policy 1: Todos podem visualizar hospitais (informação pública)
CREATE POLICY "anyone_view_hospitals" ON hospital
  FOR SELECT
  USING (true);

-- Policy 2: Apenas admin pode atualizar
CREATE POLICY "admin_update_hospitals" ON hospital
  FOR UPDATE
  USING (
    (auth.jwt() ->> 'role') = 'ADMIN'
  );

SELECT 'Hospital RLS policies created' AS status;

-- ==========================================
-- 6. TABELA: CENTRAL_TRANSPLANTES
-- ==========================================

ALTER TABLE central_transplantes ENABLE ROW LEVEL SECURITY;

-- Policy 1: Todos podem visualizar (informação pública)
CREATE POLICY "anyone_view_centrais" ON central_transplantes
  FOR SELECT
  USING (true);

-- Policy 2: Admin atualiza
CREATE POLICY "admin_update_centrais" ON central_transplantes
  FOR UPDATE
  USING (
    (auth.jwt() ->> 'role') = 'ADMIN'
  );

SELECT 'Central_transplantes RLS policies created' AS status;

-- ==========================================
-- 7. TABELA: ORGAO_DOADO
-- ==========================================

ALTER TABLE orgao_doado ENABLE ROW LEVEL SECURITY;

-- Policy 1: Admin vê todos
CREATE POLICY "admin_view_all_orgaos" ON orgao_doado
  FOR SELECT
  USING (
    (auth.jwt() ->> 'role') = 'ADMIN'
  );

-- Policy 2: Central vê todos
CREATE POLICY "central_view_all_orgaos" ON orgao_doado
  FOR SELECT
  USING (
    (auth.jwt() ->> 'role') = 'CENTRAL_TRANSPLANTES'
  );

-- Policy 3: Médico vê órgãos de seus protocolos
CREATE POLICY "staff_view_own_orgaos" ON orgao_doado
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

SELECT 'Orgao_doado RLS policies created' AS status;

-- ==========================================
-- 8. TABELA: DOACAO (se existir)
-- ==========================================
-- Se a tabela 'doacao' existir, aplicar RLS similar

ALTER TABLE doacao ENABLE ROW LEVEL SECURITY;

CREATE POLICY "admin_view_all_doacoes" ON doacao
  FOR SELECT
  USING (
    (auth.jwt() ->> 'role') = 'ADMIN'
  );

CREATE POLICY "central_view_all_doacoes" ON doacao
  FOR SELECT
  USING (
    (auth.jwt() ->> 'role') = 'CENTRAL_TRANSPLANTES'
  );

SELECT 'Doacao RLS policies created' AS status;

-- ==========================================
-- 9. TABELA: ANEXO_DOCUMENTO
-- ==========================================

ALTER TABLE anexo_documento ENABLE ROW LEVEL SECURITY;

-- Policy 1: Admin vê todos
CREATE POLICY "admin_view_all_anexos" ON anexo_documento
  FOR SELECT
  USING (
    (auth.jwt() ->> 'role') = 'ADMIN'
  );

-- Policy 2: Médico vê anexos de seus pacientes
CREATE POLICY "staff_view_own_anexos" ON anexo_documento
  FOR SELECT
  USING (
    registro_id IN (
      SELECT id FROM paciente
      WHERE hospital_id IN (
        SELECT hospital_id FROM usuario
        WHERE (auth.jwt() ->> 'sub') = email
          AND role IN ('MEDICO', 'ENFERMEIRO')
      )
    )
  );

SELECT 'Anexo_documento RLS policies created' AS status;

-- ==========================================
-- 10. TABELA: ESTATISTICA_PROTOCOLO_ME
-- ==========================================

ALTER TABLE estatistica_protocolo_me ENABLE ROW LEVEL SECURITY;

-- Policy 1: Admin vê todas
CREATE POLICY "admin_view_all_stats" ON estatistica_protocolo_me
  FOR SELECT
  USING (
    (auth.jwt() ->> 'role') = 'ADMIN'
  );

-- Policy 2: Central vê todas
CREATE POLICY "central_view_all_stats" ON estatistica_protocolo_me
  FOR SELECT
  USING (
    (auth.jwt() ->> 'role') = 'CENTRAL_TRANSPLANTES'
  );

-- Policy 3: Médico vê estatísticas de seus hospitais
CREATE POLICY "staff_view_own_stats" ON estatistica_protocolo_me
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

SELECT 'Estatistica_protocolo_me RLS policies created' AS status;

-- ==========================================
-- 11. TABELA: CENTRAL_HOSPITAIS (if exists)
-- ==========================================

ALTER TABLE central_hospitais ENABLE ROW LEVEL SECURITY;

-- Todos podem visualizar relação hospital-central
CREATE POLICY "anyone_view_central_hospitais" ON central_hospitais
  FOR SELECT
  USING (true);

-- Admin atualiza
CREATE POLICY "admin_update_central_hospitais" ON central_hospitais
  FOR UPDATE
  USING (
    (auth.jwt() ->> 'role') = 'ADMIN'
  );

SELECT 'Central_hospitais RLS policies created' AS status;

-- ==========================================
-- VALIDATION & TESTING
-- ==========================================

-- Verificar quantas policies foram criadas
SELECT
  schemaname,
  tablename,
  COUNT(*) as policy_count
FROM pg_policies
WHERE schemaname = 'public'
GROUP BY schemaname, tablename
ORDER BY tablename;

-- ==========================================
-- PRÓXIMOS PASSOS
-- ==========================================
-- ✅ 1. Executar estas migrations no Supabase SQL Editor
-- ✅ 2. Testar com usuários de diferentes roles
-- ✅ 3. Validar que médico A não vê dados de médico B
-- ✅ 4. Validar que admin vê todos
-- ✅ 5. Monitorar performance (RLS pode impactar queries complexas)
-- ✅ 6. Adicionar indexes nos campos filtrados (email, hospital_id, etc)
