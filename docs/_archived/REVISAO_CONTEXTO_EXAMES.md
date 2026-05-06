# 📋 REVISÃO COMPLETA: Contexto de Exames + Nova Regra de Validação

**Data:** 04/05/2026
**Status:** ⚠️ CRÍTICO - Inconsistências Encontradas

---

## 🔴 PROBLEMAS IDENTIFICADOS

### 1. **Lógica de "Exames Concluídos" está INCORRETA**

**Localização:** `frontend/src/services/centralDashboardService.js`

**Problema:**
```javascript
// ❌ ERRADO: Verifica apenas dataRealizacao
const clinicosRealizados = protocolo.exames.filter(
  (e) => e?.categoria === "CLINICO" && !!e?.dataRealizacao
).length;
```

**Impacto:**
- Central Dashboard mostra "3 exames concluídos" quando apenas "realizados"
- Mensagem é enganosa, pois validação é o novo requisito
- Pendências mostram "Teste clínico 1" mas não especificam se precisa ser feito ou validado

**Solução necessária:**
```javascript
// ✅ CORRETO: Verificar statusValidacao
const clinicosValidados = protocolo.exames.filter(
  (e) => e?.categoria === "CLINICO" && e?.statusValidacao === "VALIDADO"
).length;
```

---

### 2. **Falta apneia na contagem de exames**

**Localização:** `frontend/src/services/centralDashboardService.js` e `CentralPacientesPainel.js`

**Problema:**
```javascript
// ❌ ERRADO: Conta apenas 3 exames (2 clínicos + 1 complementar)
// Ignora APNEIA que é obrigatória agora
if (clinicosRealizados >= 1) concluidos += 1;
if (clinicosRealizados >= 2) concluidos += 1;
if (complementaresRealizados >= 1) concluidos += 1;
return concluidos; // Máximo = 3
```

**Impacto:**
- Apneia não aparece como requisito de validação
- Display mostra "3/3" completo mas falta apneia
- UI da Central enganada sobre requisitos

**Novo requisito:**
- Teste clínico 1 ✅
- Teste clínico 2 ✅
- Teste de Apneia ✅ (OBRIGATÓRIO - está faltando na contagem!)
- Exame complementar ✅

Total = **4 exames**, não 3!

---

### 3. **Pendências não diferencia entre "Realizado" vs "Validado"**

**Localização:** `CentralPacientesPainel.js` - Lista de pendências

**Problema:**
Mostra:
```
❌ Teste clínico 1
❌ Teste clínico 2
❌ Exames complementares
```

Mas deveria mostrar:
```
⏳ Teste clínico 1 - AGUARDANDO VALIDAÇÃO
⏳ Teste clínico 2 - AGUARDANDO VALIDAÇÃO
⏳ Apneia - AGUARDANDO VALIDAÇÃO
⏳ Exame complementar - AGUARDANDO VALIDAÇÃO
```

---

### 4. **ExameMEManager.js não persiste `statusValidacao`**

**Localização:** `frontend/src/componentes/ExameMEManager.js`

**Problema:**
- Quando médico cria exame, `statusValidacao` não é enviado
- Backend recebe exame mas campo `statusValidacao` vira `PENDENTE` automaticamente
- Exemplo:
```javascript
const payload = {
  protocoloId,
  tipoExame: 'APNEIA_TEST',
  resultado: 'POSITIVO'
  // ❌ FALTANDO: statusValidacao
};
```

**Impacto:**
- Todos os exames começam como `PENDENTE`
- Não há forma de médico indicar que está pronto para validação

---

### 5. **Falta UI para Central validar exames**

**Localização:** `CentralDashboardPage.js` / `CentralPacientesPainel.js`

**Problema:**
- Endpoints de validação foram criados (`/validar/teste-clinico-1`, etc)
- Mas **nenhuma UI** foi implementada para a Central usar esses endpoints
- Central não consegue clicar para validar exames

**Necessário:**
- Botão "Validar" em cada exame
- Diálogo para confirmar validação
- Observações opcionais de validação
- Feedback visual após validação

---

### 6. **ExameMEDTO não foi atualizado com campos de validação**

**Localização:** `backend/src/main/java/back/backend/dto/ExameMEDTO.java`

**Problema:**
- ExameME tem `statusValidacao`, `validadoPor`, `dataValidacao`, `observacoesValidacao`
- Mas o DTO não foi atualizado
- Frontend não recebe esses dados

**Falta:**
```java
private StatusValidacao statusValidacao;
private String validadoPor;
private LocalDateTime dataValidacao;
private String observacoesValidacao;

// + getters/setters
```

---

### 7. **Mapper ExameME não mapeia campos de validação**

**Localização:** `backend/src/main/java/back/backend/mapper/ExameMapper.java`

**Problema:**
- Mapper não copia campos de validação da entidade para DTO
- Dados são perdidos na conversão

**Necessário:**
```java
public ExameMEDTO toDTO(ExameME entity) {
    // ... campos existentes ...
    dto.setStatusValidacao(entity.getStatusValidacao());
    dto.setValidadoPor(entity.getValidadoPor());
    dto.setDataValidacao(entity.getDataValidacao());
    dto.setObservacoesValidacao(entity.getObservacoesValidacao());
    return dto;
}
```

---

## 📊 RESUMO DAS MUDANÇAS NECESSÁRIAS

| # | Arquivo | Tipo | Prioridade | Status |
|---|---------|------|------------|--------|
| 1 | `centralDashboardService.js` | Frontend Service | 🔴 CRÍTICO | ❌ Não feito |
| 2 | `CentralPacientesPainel.js` | Frontend UI | 🔴 CRÍTICO | ❌ Não feito |
| 3 | `ExameMEDTO.java` | Backend DTO | 🔴 CRÍTICO | ❌ Não feito |
| 4 | `ExameMapper.java` | Backend Mapper | 🔴 CRÍTICO | ❌ Não feito |
| 5 | `CentralDashboardPage.js` | Frontend UI | 🟠 ALTO | ❌ Não feito |
| 6 | `ExameMEManager.js` | Frontend Form | 🟠 ALTO | ❌ Não feito |

---

## 🛠️ PLANO DE AÇÃO SEQUENCIAL

### Fase 1: Backend - Dados (CRÍTICO)
- [ ] Atualizar `ExameMEDTO.java` com campos de validação
- [ ] Atualizar `ExameMapper.java` para mapear validação
- [ ] Validar que endpoints de validação funcionam

### Fase 2: Frontend Service - Lógica (CRÍTICO)
- [ ] Reescrever `obterExamesPendentes()` para considerar statusValidacao
- [ ] Adicionar `obterExamesValidados()` - novo
- [ ] Adicionar `obterExamesAguardandoValidacao()` - novo
- [ ] Atualizar `obterExamesConcluidos()` para contar 4 (incluir apneia)
- [ ] Atualizar `obterResumoStatusExames()` para novo formato

### Fase 3: Frontend UI - Exibição (ALTO)
- [ ] Atualizar `CentralPacientesPainel.js` - mostrar status de validação
- [ ] Adicionar coluna "Status de Validação" na tabela
- [ ] Implementar botões de validação por exame
- [ ] Criar diálogo de validação com observações

### Fase 4: Frontend - Integração (MÉDIO)
- [ ] Atualizar `CentralDashboardPage.js` - chamar endpoints de validação
- [ ] Atualizar feedback visual após validação
- [ ] Adicionar confirmação de validação em lote

---

## 📝 EXEMPLO DE NOVO FLUXO (após correções)

### Médico cria exame
```javascript
criarExame() {
  const payload = {
    protocoloId: 123,
    tipoExame: 'APNEIA_TEST',
    resultado: 'POSITIVO'
    // statusValidacao: "PENDENTE" (automático no backend)
  };
  await exameService.criar(payload);
}
```

### Central Dashboard mostra:
```
Paciente: João Silva
├─ Teste Clínico 1: ⏳ PENDENTE VALIDAÇÃO
├─ Teste Clínico 2: ⏳ PENDENTE VALIDAÇÃO
├─ Apneia: ⏳ PENDENTE VALIDAÇÃO
└─ Exame Complementar: ⏳ PENDENTE VALIDAÇÃO

[Botão] Validar Teste Clínico 1
[Botão] Validar Teste Clínico 2
[Botão] Validar Apneia
[Botão] Validar Exame Complementar
```

### Central clica "Validar Apneia"
```javascript
POST /api/protocolo/123/validar/apneia
  ?validadoPor=Central_A
  &observacoes=Valor OK

Response:
{
  "id": 123,
  "apneiaValidada": true,
  "dataValidacaoApneia": "2026-05-04T14:30:00",
  "validadosPor": "Central_A",
  "status": "EM_PROCESSO"
}
```

### Central Dashboard atualiza:
```
└─ Apneia: ✅ VALIDADO (14:30 por Central_A)
```

### Quando 4 exames VALIDADOS:
```
Entrevista Familiar: 🟢 LIBERADA ✅
```

---

## 🔗 INTERDEPENDÊNCIAS

```
ExameME (modelo)
    ↓
ExameMEDTO (serialização)
    ↓
ExameMapper (conversão)
    ↓
ExameMEService (lógica)
    ↓
ExameMEController (API)
    ↓
exameService.js (chamadas API)
    ↓
centralDashboardService.js (agregação de dados)
    ↓
CentralPacientesPainel.js (exibição)
    ↓
MedicoProtocoloME.js (lógica de entrevista)
```

**Se algo faltar em uma camada, tudo acima quebra! ⚠️**

---

## ✅ CHECKLIST DE VALIDAÇÃO FINAL

Após fazer as correções, validar:

- [ ] `ExameMEDTO` contém campos de validação
- [ ] `ExameMapper` mapeia todos os campos
- [ ] `curl /api/exames-me/protocolo/1` retorna `statusValidacao`
- [ ] `centralDashboardService.obterExamesValidados()` funciona
- [ ] `CentralPacientesPainel` mostra status de validação visualmente
- [ ] Botões de validação aparecem para Central
- [ ] `POST /api/protocolo/1/validar/apneia` funciona
- [ ] Após validar, UI atualiza automaticamente
- [ ] `MedicoProtocoloME.entrevistaLiberada()` retorna true após 4 VALIDADOS
- [ ] Mensagens de erro são claras

---

*Documento revisor gerado em 04/05/2026*
