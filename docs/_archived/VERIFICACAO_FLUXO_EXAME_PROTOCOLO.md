# 🔍 VERIFICAÇÃO COMPLETA DO FLUXO: Exame-Protocolo ME

**Data:** 04/05/2026
**Status:** 🔴 CRÍTICO - Múltiplas Inconsistências Encontradas

---

## 📊 PROBLEMAS CRÍTICOS IDENTIFICADOS

### 🔴 PROBLEMA #1: `atualizarIndicadoresProtocolo()` não considera VALIDAÇÃO

**Localização:** `backend/src/main/java/back/backend/service/ExameMEService.java:206-250`

**O que faz:**
```java
@Transactional
protected void atualizarIndicadoresProtocolo(Long protocoloId) {
    ProtocoloME protocolo = protocoloRepository.findById(protocoloId)...

    long clinicosConcluidos = exames.stream()
        .filter(e -> e.getCategoria() == ExameME.CategoriaExame.CLINICO)
        .filter(e -> e.getResultado() != null)  // ❌ Verifica resultado, não validação!
        .count();

    // Define TC1 se >= 1 clínico com resultado
    protocolo.setTesteClinico1Realizado(clinicosConcluidos >= 1);

    // Define TC2 se >= 2 clínicos com resultado
    protocolo.setTesteClinico2Realizado(clinicosConcluidos >= 2);

    // ❌ PROBLEMA: NÃO ATUALIZA CAMPOS DE VALIDAÇÃO!
    // protocolo.setTesteClinico1Validado() - NÃO EXISTE!
    // protocolo.setTesteClinico2Validado() - NÃO EXISTE!
    // protocolo.setApneiaValidada() - NÃO EXISTE!
    // protocolo.setTestesComplementaresValidados() - NÃO EXISTE!
}
```

**Impacto:**
- ✅ Médico cria exames → `testeClinico1Realizado = true`
- ❌ MAS `testeClinico1Validado` continua false
- ❌ Entrevista não é liberada (requer VALIDADO, não REALIZADO)

**Quando é chamada:**
- ✅ Quando exame é criado
- ✅ Quando exame é atualizado
- ✅ Quando exame é deletado
- ❌ NUNCA após Central validar exame!

---

### 🔴 PROBLEMA #2: Apneia não é discriminada do resto dos clínicos

**Localização:** `backend/src/main/java/back/backend/service/ExameMEService.java:213-230`

**Problema:**
```java
long clinicosConcluidos = exames.stream()
    .filter(e -> e.getCategoria() == ExameME.CategoriaExame.CLINICO)
    .filter(e -> e.getResultado() != null)
    .count();

protocolo.setTesteClinico1Realizado(clinicosConcluidos >= 1);
protocolo.setTesteClinico2Realizado(clinicosConcluidos >= 2);
```

**Exemplo:**
- Se 3 testes de Apneia + resultado → `testeClinico2Realizado = true` ✅
- Se 1 Apneia + 0 outros clínicos → `testeClinico2Realizado = true` ❌ ERRADO!

**Nova regra diz:**
- Apneia é ESPECÍFICA (não substitui TC1 ou TC2)
- Requisito: TC1 (qualquer clínico) + TC2 (qualquer clínico) + Apneia + Complementar

**Necessário:**
```java
// Identificar especificamente Apneia
boolean temApneia = exames.stream()
    .anyMatch(e -> e.getTipoExame() == ExameME.TipoExame.APNEIA_TEST
                && e.getResultado() != null);

protocolo.setApneiaValidada(temApneia); // ✅ Depois revisar com validação
```

---

### 🔴 PROBLEMA #3: Métodos de validação não atualizam protocolo

**Localização:** `backend/src/main/java/back/backend/service/ProtocoloMEService.java:405-410`

**Código atual:**
```java
public ProtocoloMEDTO validarTesteClinico1(Long id, String validadoPor, String observacoes) {
    return executar(id, p -> {
        p.setTesteClinico1Validado(true);
        p.setDataValidacaoTesteClinico1(LocalDateTime.now());
        p.setValidadosPor(validadoPor);
        // ❌ Não chama sincronizarStatusPaciente()
        // ❌ Não chama calcularStatusAutomatico()
    });
}
```

**Impacto:**
- ✅ Valida teste clínico 1
- ❌ Status do protocolo não muda
- ❌ Entrevista não é automaticamente liberada
- ❌ Paciente continua como "EM_PROCESSO"

**Necessário:**
```java
public ProtocoloMEDTO validarTesteClinico1(Long id, String validadoPor, String observacoes) {
    return executar(id, p -> {
        p.setTesteClinico1Validado(true);
        p.setDataValidacaoTesteClinico1(LocalDateTime.now());
        p.setValidadosPor(validadoPor);

        // ✅ Atualizar status automaticamente
        p.setStatus(p.calcularStatusAutomatico());

        // ✅ Sincronizar status do paciente
        sincronizarStatusPaciente(p);
    });
}
```

---

### 🔴 PROBLEMA #4: `validarExame()` não retorna protocolo atualizado

**Localização:** `backend/src/main/java/back/backend/service/ProtocoloMEService.java:375-395`

**Código atual:**
```java
public ProtocoloMEDTO validarExame(Long id, Long exameId, boolean validado,
                                   String validadoPor, String observacoes) {
    ProtocoloME protocolo = buscarOuFalhar(id);
    ExameME exame = exameRepository.findById(exameId)...

    exame.setStatusValidacao(validado ? VALIDADO : REJEITADO);
    exame.setValidadoPor(validadoPor);
    exame.setDataValidacao(LocalDateTime.now());

    exameRepository.save(exame);  // ✅ Salva exame
    return toDTO(protocolo);       // ❌ Retorna protocolo SEM atualizar!
}
```

**Impacto:**
- ❌ Exame é validado mas protocolo não reflete
- ❌ Frontend não vê mudança de status
- ❌ Entrevista não é liberada

**Necessário:**
```java
exameRepository.save(exame);
atualizarIndicadoresProtocolo(id);  // ✅ Atualizar protocolo
return toDTO(protocolo);
```

---

### 🔴 PROBLEMA #5: Frontend exibe "/3" em vez de "/4"

**Localização:** `frontend/src/componentes/CentralPacientesPainel.js:260`

**Código atual:**
```jsx
<strong>{examesConcluidos}/3</strong>
```

**Problema:**
- Mostra "2/3 exames concluídos"
- Mas deveria ser "2/4" agora (incluindo apneia)

**Impacto:**
- UI enganosa
- Usuário pensa que precisa de 3 exames, quando na verdade precisa de 4

**Necessário:**
```jsx
<strong>{examesConcluidos}/4</strong>  // ✅ Apneia incluída
```

---

### 🔴 PROBLEMA #6: Frontend ainda usa antigo `resumoStatusExames`

**Localização:** `frontend/src/componentes/CentralPacientesPainel.js:264-266`

**Código atual:**
```jsx
<span className="badge-exame badge-exame-positivo">+ {resumoStatusExames.positivos}</span>
<span className="badge-exame badge-exame-negativo">- {resumoStatusExames.negativos}</span>
<span className="badge-exame badge-exame-pendente">⏳ {resumoStatusExames.pendentes}</span>
```

**Impacto:**
- Mostra "Positivo/Negativo" (resultado clínico)
- NÃO mostra status de validação
- Central não consegue saber quais estão "AGUARDANDO VALIDAÇÃO"

**Necessário:**
```jsx
<span className="badge-validado">✅ {resumoStatusExames.validados}</span>
<span className="badge-aguardando">⏳ {resumoStatusExames.aguardandoValidacao}</span>
<span className="badge-nao-realizado">❌ {resumoStatusExames.naoRealizados}</span>
```

---

## 🔗 FLUXO QUEBRADO ATUAL

```
1. Médico cria 4 exames (Apneia + 3 clínicos + complementar)
   ↓
2. ExameMEService.criarExame() chama atualizarIndicadoresProtocolo()
   ↓
3. ❌ PROBLEMA: Atualiza testeClinico1Realizado = true
   ❌ MAS testeClinico1Validado = false (nunca foi setado!)
   ↓
4. ProtocoloME.calcularStatusAutomatico() verifica VALIDADOS
   ↓
5. ❌ FALHA: Precisa de 4 VALIDADOS, tem 0
   ↓
6. Status = EM_PROCESSO
   ↓
7. Entrevista bloqueada ✅ (correto)
   ↓
8. Central vê UI mostrando "2/3 exames" ❌ (errado, deveria ser "0/4 VALIDADOS")
   ↓
9. Central clica "Validar Apneia"
   ↓
10. POST /protocolo/1/validar/apneia
    ↓
11. ProtocoloMEService.validarApneia() → apneiaValidada = true
    ❌ MAS não chama calcularStatusAutomatico()
    ❌ MAS não chama sincronizarStatusPaciente()
    ↓
12. ❌ FALHA: Protocolo retorna SEM atualizar status
    ↓
13. Frontend não atualiza nada (pensa que nada mudou)
    ↓
14. Após validar todos 4:
    ↓
15. Entrevista ainda bloqueada ❌ (porque status não foi recalculado)
```

---

## ✅ FLUXO ESPERADO (após correções)

```
1. Médico cria 4 exames
   ↓
2. atualizarIndicadoresProtocolo() marca como REALIZADO
   ✅ MAS também verifica VALIDAO (nenhum ainda)
   ✓ ProtocoloME.apneiaValidada = null (aguardando)
   ↓
3. Status = EM_PROCESSO (exames realizados mas não validados)
   ↓
4. Central vê "0/4 VALIDADOS" ✅
   ↓
5. Central clica "Validar Apneia"
   ↓
6. ProtocoloMEService.validarApneia()
   ✅ apneiaValidada = true
   ✅ calcularStatusAutomatico() = EM_PROCESSO (faltam outros)
   ✅ sincronizarStatusPaciente() mantém EM_PROTOCOLO_ME
   ↓
7. Frontend atualiza → "1/4 VALIDADOS" ✅
   ↓
8. Central valida outros 3
   ↓
9. Quando 4º = VALIDADO:
   ✅ calcularStatusAutomatico() = ENTREVISTA_FAMILIAR
   ✅ sincronizarStatusPaciente() → APTO (ou EM_PROTOCOLO_ME)
   ✓ Entrevista é LIBERADA ✅
```

---

## 🛠️ SOLUÇÕES NECESSÁRIAS

### Fase A: Backend - Sincronização Crítica (IMEDIATO)

**A1.** Adicionar validação à `atualizarIndicadoresProtocolo()`
```java
// Além de verificar se realizado, também verifica se validado
long clinicosValidados = exames.stream()
    .filter(e -> e.getCategoria() == CLINICO)
    .filter(e -> e.getStatusValidacao() == VALIDADO)
    .count();

protocolo.setTesteClinico1Validado(clinicosValidados >= 1);
protocolo.setTesteClinico2Validado(clinicosValidados >= 2);
```

**A2.** Adicionar apneia como campo específico
```java
boolean apneiaValidada = exames.stream()
    .anyMatch(e -> e.getTipoExame() == APNEIA_TEST
              && e.getStatusValidacao() == VALIDADO);
protocolo.setApneiaValidada(apneiaValidada);
```

**A3.** Chamar `atualizarIndicadoresProtocolo()` após validação de exame
```java
public ProtocoloMEDTO validarExame(...) {
    ExameME exame = exameRepository.findById(exameId)...
    exame.setStatusValidacao(validado ? VALIDADO : REJEITADO);
    exameRepository.save(exame);

    // ✅ NOVO: Atualizar protocolo após validar exame
    atualizarIndicadoresProtocolo(id);

    ProtocoloME protocolo = buscarOuFalhar(id);
    protocolo.setStatus(protocolo.calcularStatusAutomatico());
    return toDTO(salvar(protocolo));
}
```

**A4.** Garantir que métodos de validação recalculam status
```java
public ProtocoloMEDTO validarTesteClinico1(...) {
    return executar(id, p -> {
        p.setTesteClinico1Validado(true);
        p.setDataValidacaoTesteClinico1(LocalDateTime.now());

        // ✅ NOVO: Atualizar status e paciente
        p.setStatus(p.calcularStatusAutomatico());
        sincronizarStatusPaciente(p);
    });
}
```

### Fase B: Frontend - Exibição Correta

**B1.** Atualizar contagem de exames
```jsx
<strong>{examesConcluidos}/4</strong>  // ✅ Incluir apneia
```

**B2.** Usar novo formato de resumo de validação
```jsx
const resumo = obterResumoStatusExames(protocolo);
// Mostra: { validados: 2, aguardandoValidacao: 1, naoRealizados: 1, total: 4 }

<span>✅ {resumo.validados}</span>  // Quanto foi validado
<span>⏳ {resumo.aguardandoValidacao}</span>  // Aguardando validação
<span>❌ {resumo.naoRealizados}</span>  // Não realizado ainda
```

---

## 📋 CHECKLIST DE VERIFICAÇÃO (PÓS-CORREÇÃO)

- [ ] Médico cria 4 exames → `testeClinico1Realizado = true`, `testeClinico1Validado = false`
- [ ] Central Dashboard mostra "0/4 VALIDADOS"
- [ ] Central clica "Validar Apneia" → apneiaValidada muda para true
- [ ] Frontend atualiza para "1/4 VALIDADOS"
- [ ] Status do protocolo recalcula automaticamente
- [ ] Após 4 exames VALIDADOS → Entrevista LIBERADA
- [ ] Apneia é contado como exame obrigatório (não substitui TC1/TC2)
- [ ] mensagens de pendência especificam "AGUARDANDO VALIDAÇÃO"

---

## 🎯 ORDEM DE CORREÇÃO RECOMENDADA

1. **URGENTE (hoje):** Corrigir `validarExame()` para chamar `atualizarIndicadoresProtocolo()`
2. **URGENTE:** Adicionar `apneiaValidada` check na `atualizarIndicadoresProtocolo()`
3. **HOJE:** Adicionar `calcularStatusAutomatico()` aos métodos de validação
4. **HOJE:** Atualizar frontend para mostrar "/4" exames
5. **HOJE:** Atualizar resumo de status para mostrar validação

---

*Documento de verificação gerado em 04/05/2026*
