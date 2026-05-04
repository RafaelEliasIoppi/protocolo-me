# ✅ Checklist de Testes - Row-Level Security (RLS)

**Status:** Pré-implementação
**Responsável:** QA / Dev
**Tempo Estimado:** 1-2 horas

---

## 📋 Testes Funcionais - RLS Habilitado

### **Test Suite 1: Tabela USUARIO**

| # | Cenário | Dado | Esperado | Status |
|---|---------|------|----------|--------|
| 1.1 | Admin lista todos usuários | GET /api/usuarios com ADMIN token | Retorna TODOS os usuários | [ ] |
| 1.2 | Médico lista usuários | GET /api/usuarios com MEDICO token | Retorna ERRO 403 (sem acesso) | [ ] |
| 1.3 | Usuário vê seu próprio perfil | GET /api/usuarios com seu token | Retorna apenas seu registro | [ ] |
| 1.4 | Central vê médicos | GET /api/usuarios com CENTRAL token | Retorna apenas MEDICO + ENFERMEIRO | [ ] |
| 1.5 | Admin edita usuário | PUT /api/usuarios/2 com ADMIN | Sucesso (200) | [ ] |
| 1.6 | Médico edita outro | PUT /api/usuarios/3 com MEDICO | Erro (403) | [ ] |
| 1.7 | Usuário edita a si próprio | PUT /api/usuarios/1 (seu ID) | Sucesso (200) | [ ] |

---

### **Test Suite 2: Tabela PACIENTE**

| # | Cenário | Dado | Esperado | Status |
|---|---------|------|----------|--------|
| 2.1 | Admin vê TODOS os pacientes | GET /api/pacientes com ADMIN | Lista completa | [ ] |
| 2.2 | Médico A vê pacientes hospital 1 | GET /api/pacientes (hospital_id=1) | Apenas hospital 1 | [ ] |
| 2.3 | Médico A não vê hospital 2 | GET /api/pacientes / busca por hospital 2 | Erro 403 | [ ] |
| 2.4 | Enfermeiro vê pacientes seu hospital | GET /api/pacientes (seu hospital) | Sucesso | [ ] |
| 2.5 | Central vê TODOS pacientes | GET /api/pacientes com CENTRAL | Lista completa | [ ] |
| 2.6 | Médico cria paciente no seu hospital | POST /api/pacientes (hospital=seu) | Sucesso (201) | [ ] |
| 2.7 | Médico tenta criar em outro hospital | POST /api/pacientes (hospital=outro) | Erro (403) | [ ] |
| 2.8 | Admin edita qualquer paciente | PUT /api/pacientes/5 (outro hospital) | Sucesso (200) | [ ] |

---

### **Test Suite 3: Tabela PROTOCOLO_ME**

| # | Cenário | Dado | Esperado | Status |
|---|---------|------|----------|--------|
| 3.1 | Admin vê TODOS os protocolos | GET /api/protocolos-me com ADMIN | Lista completa | [ ] |
| 3.2 | Médico vê protocolos seus pacientes | GET /api/protocolos-me (seu hospital) | Apenas seus | [ ] |
| 3.3 | Médico A não vê protocolos de A B | GET /api/protocolos-me / filtro outro hospital | Erro 403 ou lista vazia | [ ] |
| 3.4 | Central vê TODOS protocolos | GET /api/protocolos-me com CENTRAL | Lista completa | [ ] |
| 3.5 | Médico cria protocolo seu paciente | POST /api/protocolos-me (paciente seu) | Sucesso | [ ] |
| 3.6 | Médico tenta criar protocolo outro hospital | POST /api/protocolos-me (paciente outro) | Erro (403) | [ ] |

---

### **Test Suite 4: Tabela EXAME_ME**

| # | Cenário | Dado | Esperado | Status |
|---|---------|------|----------|--------|
| 4.1 | Admin vê TODOS exames | GET /api/exames-me com ADMIN | Lista completa | [ ] |
| 4.2 | Médico vê exames seus protocolos | GET /api/exames-me (seu hospital) | Apenas seus | [ ] |
| 4.3 | Médico não vê exames outro hospital | GET /api/exames-me / filtro outro | Erro ou vazio | [ ] |
| 4.4 | Enfermeiro vê exames seu hospital | GET /api/exames-me (seu hospital) | Sucesso | [ ] |
| 4.5 | Central vê TODOS exames | GET /api/exames-me com CENTRAL | Lista completa | [ ] |

---

### **Test Suite 5: Admin Principal Protegido**

| # | Cenário | Dado | Esperado | Status |
|---|---------|------|----------|--------|
| 5.1 | Tentar desativar admin | PUT /api/usuarios/1 { ativo: false } | Erro: "Não é possível desativar admin principal" | [ ] |
| 5.2 | Tentar via UI (frontend) | Clicar botão desativar em admin | Erro na UI | [ ] |
| 5.3 | Admin consegue editar a si mesmo | PUT /api/usuarios/1 { nome: novo } | Sucesso (nome atualizado) | [ ] |
| 5.4 | Admin consegue editar senha | PATCH /api/usuarios/1/senha | Sucesso | [ ] |

---

## 🔒 Testes de Segurança - Isolamento de Dados

### **Test Suite 6: Isolamento Horizontal (dados do mesmo role)**

| # | Teste | Comando | Risco | Status |
|---|-------|---------|------|--------|
| 6.1 | Médico A lê pacientes de Médico B | `SELECT * FROM paciente WHERE hospital_id != meu_hospital` | Alto | [ ] |
| 6.2 | Médico A edita paciente de Médico B | `UPDATE paciente SET nome='Novo' WHERE hospital_id != meu_hospital` | Crítico | [ ] |
| 6.3 | Médico A deleta protocolo de Médico B | `DELETE FROM protocolo_me WHERE hospital_id != meu_hospital` | Crítico | [ ] |
| 6.4 | Enfermeiro acessa dados admin | SQL direto no console | Crítico | [ ] |

---

### **Test Suite 7: Isolamento Vertical (bypass de role)**

| # | Teste | Comando | Risco | Status |
|---|-------|---------|------|--------|
| 7.1 | Médico toma permissão ADMIN | JWT forjado com `role: ADMIN` | Crítico | [ ] |
| 7.2 | Paciente acessa dados usuários | SQL direto sem token | Crítico | [ ] |
| 7.3 | Token expirado = sem acesso | Usar JWT com data expiração passada | Alto | [ ] |
| 7.4 | JWT sem secret = rejeitado | JWT sem assinatura válida | Alto | [ ] |

---

## ⚡ Testes de Performance - Impacto do RLS

| # | Query | Sem RLS | Com RLS | Diferença | Status |
|---|-------|---------|---------|-----------|--------|
| 8.1 | SELECT * FROM usuario (100 records) | <10ms | <50ms | OK se <100ms | [ ] |
| 8.2 | SELECT * FROM paciente com JOINs | <20ms | <80ms | OK se <200ms | [ ] |
| 8.3 | INSERT novo paciente | <10ms | <20ms | OK se <50ms | [ ] |
| 8.4 | UPDATE protocolo | <10ms | <30ms | OK se <100ms | [ ] |

---

## 🧪 Testes Manuais (Exploratory)

### **Teste 9: Login & Token**

```javascript
// 1. Login com admin@protocolo.me
const admin = await login('admin@protocolo.me', 'Admin123!');
console.assert(admin.role === 'ADMIN', 'Admin role OK');

// 2. Login com médico
const medico = await login('medico@hospital.com', 'senha123');
console.assert(medico.role === 'MEDICO', 'Médico role OK');

// 3. Validar JWT
const decoded = jwt.decode(token);
console.assert(decoded.role === 'MEDICO', 'JWT role OK');
console.assert(decoded.sub === 'medico@hospital.com', 'JWT email OK');
```

**Resultado:** [ ] Pass / [ ] Fail

---

### **Teste 10: Filtros Automáticos**

```javascript
// Médico A de Hospital 1
const medicoA = { email: 'medico.a@hospital1.com', hospital_id: 1 };

// Query: Listar pacientes
const pacientes = await api.get('/api/pacientes');

// Validação
const todosDo1 = pacientes.every(p => p.hospital_id === 1);
console.assert(todosDo1, 'RLS filtrou hospital 1 automaticamente');
```

**Resultado:** [ ] Pass / [ ] Fail

---

### **Teste 11: Auditoria (Preparação para Fase 3)**

```sql
-- Verificar se há logging de mudanças (atualmente NÃO existe)
SELECT COUNT(*) as has_audit_table
FROM information_schema.tables
WHERE table_name = 'audit_log';

-- Esperado: 0 (será criado em Fase 3)
-- Importante: Documentar mudanças feitas e por quem manualmente
```

**Resultado:** [ ] Pass / [ ] Fail

---

## 📝 Matriz de Cobertura

| Componente | Teste Funcional | Teste Segurança | Teste Performance | Total |
|-----------|-----------------|-----------------|-------------------|-------|
| Usuario | 1.1-1.7 (7) | 6.1-7.4 (8) | 8.1 (1) | 16 |
| Paciente | 2.1-2.8 (8) | 6.2-6.4 (3) | 8.2 (1) | 12 |
| Protocolo | 3.1-3.6 (6) | 6.3 (1) | 8.3 (1) | 8 |
| Exame | 4.1-4.5 (5) | - | 8.4 (1) | 6 |
| **TOTAL** | **26 testes** | **12 testes** | **4 testes** | **42 testes** |

---

## 🎯 Critério de Aceição

✅ **Passar em todos os 42 testes** para considerar RLS pronto para produção

### **Testes Críticos (DEVEM PASSAR):**
- [ ] 1.1 - Admin lista todos usuários
- [ ] 2.2 - Médico vê apenas seu hospital
- [ ] 2.3 - Médico não vê outro hospital
- [ ] 3.2 - Médico vê apenas seus protocolos
- [ ] 5.1 - Admin principal não pode ser desativado
- [ ] 6.2 - Médico não pode editar outro hospital
- [ ] 7.1 - JWT forjado é rejeitado
- [ ] 8.2 - Performance aceitável com RLS

### **Resultado:** [ ] PRONTO PARA PRODUÇÃO / [ ] FALHAS ENCONTRADAS

---

## 📌 Próximas Fases após RLS

### **Fase 2: Isolamento Backend (Após RLS)**
- [ ] Adicionar `@CurrentUser` annotation
- [ ] Filtrar automaticamente por `hospitalId`
- [ ] Duplicação de proteção (RLS + Backend)

### **Fase 3: Auditoria (Após RLS)**
- [ ] Criar tabela `audit_log`
- [ ] Implementar trigger
- [ ] Dashboard de auditoria

### **Fase 4: Rate Limiting (Após RLS)**
- [ ] Proteção contra brute force
- [ ] Max 5 tentativas de login

---

## 📊 Resultado Final

**Data:** ___________
**Executado por:** ___________
**Status Geral:** ___________

**Testes Passaram:** ____ / 42

**Observações:**
```
[Escrever aqui qualquer problema encontrado]
```

---

**Assinado:** ___________________ Data: ___________

---

## 🚀 Próximo Passo

Após passar em TODOS os testes, ✅ RLS está pronto para produção!

Se encontrar falhas:
1. Reportar qual teste falhou
2. Anexar screenshot de erro
3. Descrever comportamento esperado vs obtido
4. Aguardar correção
5. Re-executar teste
