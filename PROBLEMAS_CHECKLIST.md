# ⚡ CHECKLIST RÁPIDO - Problemas Encontrados

## 🔴 CRÍTICOS (7 problemas)

### Typos e Nomenclatura (3)
- [ ] **Paciente.java:58** - `telefonoResponsavel` → `telefoneResponsavel` (português correto)
  - Afeta: [PacienteService.java:46](backend/src/main/java/back/backend/service/PacienteService.java#L46)
  
- [ ] **ExameME.java:39** - `resultado_positivo` → `resultadoPositivo` (camelCase)
  - Afeta: [ExameMEService.java:70,83](backend/src/main/java/back/backend/service/ExameMEService.java#L70), [ExameMEController.java:99](backend/src/main/java/back/backend/controller/ExameMEController.java#L99), [ProtocoloMEService.java:98](backend/src/main/java/back/backend/service/ProtocoloMEService.java#L98)

- [ ] **ExameMEService.java:119** - `exames_Clinicos` → `examesClinicos` (camelCase)
  - Afeta: [ExameMEService.java:108,130,131](backend/src/main/java/back/backend/service/ExameMEService.java#L108)

### Arquivos Duplicados (1)
- [ ] **Deletar [JwUtil.java](backend/src/main/java/back/security/JwUtil.java)** - Obsoleto, renomeado para JwtUtil.java

### Validações Incompletas (3)
- [ ] **PacienteService.java:153** - Adicionar validação de:
  - CPF (formato)
  - Email responsável
  - Telefone (formato)
  - Datas (consistência)

- [ ] **ExameMEService.java:25** - Adicionar validação de:
  - FK protocoloME existe
  - Categoria válida
  - TipoExame válido

- [ ] **CentralTransplantesService.java** - Adicionar validação de:
  - CNPJ (formato)
  - Email (formato)
  - Telefone (formato)

---

## ✅ OK - SEM PROBLEMAS

### Frontend URLs
- ✅ Arquivo [api.js](frontend/src/api/api.js) - URLs relativas
- ✅ Proxy [package.json](frontend/package.json) - http://localhost:2500
- ✅ Nenhuma referência a localhost:8080 ou :3000

### Endpoints
- ✅ PacienteController - CRUD completo
- ✅ HospitalController - CRUD completo
- ✅ ProtocoloMEController - Endpoints OK
- ✅ ExameMEController - Endpoints OK
- ✅ CentralTransplantesController - CRUD OK

### Configuração
- ✅ application.properties - OK (porta 2500)
- ✅ package.json - OK

---

## 📊 ESTATÍSTICAS

| Item | Quantidade |
|------|-----------|
| Typos encontrados | 3 |
| Arquivos duplicados | 1 |
| Validações faltando | 3 |
| Problemas CRÍTICOS | 4 |
| Problemas ALTOS | 3 |
| Áreas OK | 8 |

---

## 🎯 PRÓXIMAS AÇÕES

1. **Renomear campos** (10 min):
   ```bash
   # Buscar e substituir nos 7 arquivos afetados
   ```

2. **Deletar arquivo** (1 min):
   ```bash
   rm backend/src/main/java/back/security/JwUtil.java
   ```

3. **Adicionar validações** (30-45 min):
   - PacienteService: validar CPF, email, telefone, datas
   - ExameMEService: validar FK e enums
   - CentralTransplantesService: validar CNPJ, email, telefone

4. **Testes** (15 min):
   ```bash
   mvn test
   npm test
   ```

5. **Segurança** (5 min):
   ```bash
   npm audit fix
   ```

---

**Detalhes:** Ver [AUDITORIA_COMPLETA_PROBLEMAS.md](AUDITORIA_COMPLETA_PROBLEMAS.md)
