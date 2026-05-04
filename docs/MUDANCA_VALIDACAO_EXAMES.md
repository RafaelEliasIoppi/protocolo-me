# Nova Regra: Validação de Exames para Entrevista Familiar

**Data da Mudança:** 04/05/2026
**Versão:** 2.0

---

## 📌 Resumo da Mudança

A partir desta versão, a regra para disponibilizar a **Entrevista Familiar** foi alterada:

### **ANTES:**
- Exames apenas precisavam estar **REALIZADOS**
- Requisito: 2 testes clínicos + 1 exame complementar

### **AGORA:**
- Exames precisam estar **VALIDADOS pela Central**
- Requisito: 2 testes clínicos + teste de apneia + 1 exame complementar (todos VALIDADOS)
- A Central passa a ter novo papel de validar todos os exames realizados

---

## 🔄 Novo Fluxo

```
┌─────────────────────────────────────┐
│ 1. MÉDICO REALIZA EXAMES            │
│    - Teste Clínico 1 ✓              │
│    - Teste Clínico 2 ✓              │
│    - Teste de Apneia ✓              │
│    - Exame Complementar ✓           │
└─────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────┐
│ 2. CENTRAL RECEBE NOTIFICAÇÃO       │
│    (Exames estão REALIZADOS)        │
└─────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────┐
│ 3. CENTRAL VALIDA EXAMES            │
│    statusValidacao = VALIDADO       │
│    validadoPor = nome_central       │
│    dataValidacao = agora            │
└─────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────┐
│ 4. ENTREVISTA LIBERADA ✅           │
│    (Quando 4 exames VALIDADOS)      │
│    Médico pode abrir entrevista     │
└─────────────────────────────────────┘
```

---

## 🛠️ Implementação Técnica

### Backend

#### Novo Enum em `ExameME.java`

```java
public enum StatusValidacao {
    PENDENTE("Pendente de validação", "Exame realizado, aguardando validação"),
    VALIDADO("Validado", "Exame validado pela central"),
    REJEITADO("Rejeitado", "Exame rejeitado pela central"),
    REALIZADO("Realizado", "Exame realizado mas não validado");

    // ... getters ...
}
```

#### Novos Campos em `ProtocoloME.java`

```java
// Campos de validação adicionados
private Boolean testeClinico1Validado = false;
private LocalDateTime dataValidacaoTesteClinico1;

private Boolean testeClinico2Validado = false;
private LocalDateTime dataValidacaoTesteClinico2;

private Boolean testesComplementaresValidados = false;
private LocalDateTime dataValidacaoTesteComplementar;

private Boolean apneiaValidada = false;
private LocalDateTime dataValidacaoApneia;

private String validadosPor;
private LocalDateTime dataValidacaoGeral;
```

#### Métodos Atualizados em `ProtocoloME.java`

```java
public boolean estaProntoParaEntrevista() {
    // ✅ NOVA REGRA: Exames devem estar VALIDADOS
    return Boolean.TRUE.equals(testeClinico1Validado) &&
           Boolean.TRUE.equals(testeClinico2Validado) &&
           Boolean.TRUE.equals(testesComplementaresValidados) &&
           Boolean.TRUE.equals(apneiaValidada) &&
           dataConfirmacaoME != null;
}
```

#### Novos Endpoints em `ProtocoloMEController.java`

```
POST /api/protocolo/{id}/validar/teste-clinico-1
  ?validadoPor=nome&observacoes=texto

POST /api/protocolo/{id}/validar/teste-clinico-2
  ?validadoPor=nome&observacoes=texto

POST /api/protocolo/{id}/validar/testes-complementares
  ?validadoPor=nome&observacoes=texto

POST /api/protocolo/{id}/validar/apneia
  ?validadoPor=nome&observacoes=texto

POST /api/protocolo/{id}/validar/exame/{exameId}
  ?validado=true&validadoPor=nome&observacoes=texto
```

### Frontend

#### Função Atualizada em `MedicoProtocoloME.js`

```javascript
// Nova função para verificar validação
const examesObrigatoriosValidados = (protocolo) =>
    Boolean(protocolo?.testeClinico1Validado)
    && Boolean(protocolo?.testeClinico2Validado)
    && Boolean(protocolo?.testesComplementaresValidados)
    && Boolean(protocolo?.apneiaValidada);

// Lógica atualizada de entrevista
const entrevistaLiberada = (protocolo) => {
    if (examesObrigatoriosValidados(protocolo)) {
        // Entrevista pode ser aberta ✅
    }
};
```

#### Mensagens de Erro Atualizadas

**ANTES:** "Conclua 2 testes clínicos e 1 exame complementar"

**AGORA:** "Exames precisam ser VALIDADOS pela central: 2 testes clínicos + apneia + 1 exame complementar"

---

## 📊 Status nas Entidades

### ExameME - Status de Validação

```java
public enum StatusValidacao {
    PENDENTE,      // Realizados, aguardando validação
    VALIDADO,      // ✅ Aprovado pela central
    REJEITADO,     // ❌ Recusado, precisa refazer
    REALIZADO      // Realizado mas em validação
}
```

### ProtocoloME - Status Automático

- **NOTIFICADO**: Sem exames realizados
- **EM PROCESSO**: Alguns exames realizados mas ainda não validados
- **MORTE_CEREBRAL_CONFIRMADA**: Exames validados, ME confirmada
- **ENTREVISTA_FAMILIAR**: Todos os exames validados ✅
- **FAMILIA_RECUSOU**: Família recusou doação
- **DOACAO_AUTORIZADA**: Autorização confirmada

---

## 🔐 Permissões Necessárias

### Perfil MÉDICO/ENFERMEIRO
- ✅ Registrar testes clínicos (realizar)
- ❌ Validar exames (apenas leitura do status)

### Perfil CENTRAL_TRANSPLANTES
- ✅ Visualizar exames realizados
- ✅ **Validar exames** (novo)
- ✅ Rejeitar exames (novo)
- ✅ Registrar observações de validação (novo)

### Perfil ADMIN
- ✅ Tudo (incluindo validação se necessário)

---

## 📝 Exemplo de Uso via API

### Médico realiza teste clínico 1

```bash
POST /api/protocolo/123/registrar-teste-clinico-1
```

### Central valida o teste clínico 1

```bash
POST /api/protocolo/123/validar/teste-clinico-1?validadoPor=Central_A&observacoes=Exame OK
```

Resposta:
```json
{
  "id": 123,
  "testeClinico1Realizado": true,
  "testeClinico1Validado": true,
  "dataTesteClinico1": "2026-05-04T10:00:00",
  "dataValidacaoTesteClinico1": "2026-05-04T11:30:00",
  "validadosPor": "Central_A",
  "status": "EM_PROCESSO"
}
```

---

## ⚠️ Impacto nas Funcionalidades

### Painel da Central (Dashboard)

Agora mostrará:
- ✅ Exames realizados (status REALIZADOS)
- ✅ Exames validados (status VALIDADOS)
- ✅ Exames pendentes (status PENDENTE)
- ✅ Exames rejeitados (status REJEITADO)

### Protocolo Médico

Agora exibe:
- ✅ Status de realização de cada teste
- ✅ Status de validação de cada teste
- ✅ Data de validação
- ✅ Quem validou

### Entrevista Familiar

Liberada somente quando:
- ✅ 2 testes clínicos VALIDADOS
- ✅ Teste de apneia VALIDADO
- ✅ 1 exame complementar VALIDADO
- ✅ ME confirmada

---

## 🔄 Migração de Dados Existentes

Para protocolos já em andamento:

```sql
-- Marcar exames já realizados como validados (suposição de retroatividade)
UPDATE protocolo_me
SET testeClinico1Validado = testeClinico1Realizado,
    testeClinico2Validado = testeClinico2Realizado,
    testesComplementaresValidados = testesComplementaresRealizados,
    apneiaValidada = testeClinico1Realizado AND testeClinico2Realizado
WHERE dataAtualizacao < '2026-05-04'
  AND testeClinico1Realizado = true;
```

---

## 📞 Suporte

Dúvidas sobre a nova regra de validação?

- **Médicos**: Aguarde validação da central para liberar entrevista
- **Central**: Use os novos endpoints de validação para aprovar/rejeitar exames
- **Admin**: Consulte os logs em `dataValidacao` e `validadosPor`

---

*Documento gerado em 04/05/2026*
