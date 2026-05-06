# 🛠️ PLANO DE AÇÃO: Correção do Fluxo Exame-Protocolo

**Status:** ⏳ EM EXECUÇÃO
**Criado:** 04/05/2026
**Objetivo:** Fixar 6 problemas críticos que impedem validação de exames

---

## 📊 ORDEM DE EXECUÇÃO

### Fase 1: Backend - Sincronização de Validação (CRÍTICO)

#### P4 URGENTE: `validarExame()` deve atualizar protocolo
- **Arquivo:** ProtocoloMEService.java (linhas 375-395)
- **Status:** ⏳ PRONTO PARA CORRIGIR
- **Tempo Estimado:** 5 min
- **Razão:** Sem isso, frontend nunca vê exame como validado

#### P1 URGENTE: `atualizarIndicadoresProtocolo()` deve verificar VALIDADOS
- **Arquivo:** ExameMEService.java (linhas 206-250)
- **Status:** ⏳ PRONTO PARA CORRIGIR
- **Tempo Estimado:** 10 min
- **Razão:** Sem isso, marca REALIZADO mas não VALIDADO

#### P2 URGENTE: Apneia deve ser campo específico
- **Arquivo:** ExameMEService.java (mesma função)
- **Status:** ⏳ PRONTO PARA CORRIGIR
- **Tempo Estimado:** 5 min
- **Razão:** Sem isso, apneia substitui TC1/TC2

#### P3: Métodos de validação devem recalcular status
- **Arquivo:** ProtocoloMEService.java (linhas 340-410)
- **Status:** ⏳ PRONTO PARA CORRIGIR
- **Tempo Estimado:** 10 min
- **Razão:** Sem isso, status não muda automaticamente

### Fase 2: Frontend - Exibição Correta

#### P5: Trocar "/3" por "/4"
- **Arquivo:** CentralPacientesPainel.js (linha 260)
- **Status:** ⏳ PRONTO PARA CORRIGIR
- **Tempo Estimado:** 2 min

#### P6: Usar novo formato de resumo
- **Arquivo:** CentralPacientesPainel.js (linhas 264-266)
- **Status:** ⏳ PRONTO PARA CORRIGIR
- **Tempo Estimado:** 5 min

---

## 🎯 EXECUÇÃO IMEDIATA

### Próximo Passo: P4 - Corrigir `validarExame()`

**Atualmente:**
```java
public ProtocoloMEDTO validarExame(Long id, Long exameId, boolean validado,
                                   String validadoPor, String observacoes) {
    ProtocoloME protocolo = buscarOuFalhar(id);
    ExameME exame = exameRepository.findById(exameId)...

    exame.setStatusValidacao(validado ? StatusValidacao.VALIDADO : StatusValidacao.REJEITADO);
    exame.setValidadoPor(validadoPor);
    exame.setDataValidacao(LocalDateTime.now());

    exameRepository.save(exame);
    return toDTO(protocolo);  // ❌ Retorna protocolo SEM atualizar!
}
```

**Deverá ser:**
```java
public ProtocoloMEDTO validarExame(Long id, Long exameId, boolean validado,
                                   String validadoPor, String observacoes) {
    ProtocoloME protocolo = buscarOuFalhar(id);
    ExameME exame = exameRepository.findById(exameId)
        .orElseThrow(() -> new RuntimeException("Exame não encontrado"));

    exame.setStatusValidacao(validado ? StatusValidacao.VALIDADO : StatusValidacao.REJEITADO);
    exame.setValidadoPor(validadoPor);
    exame.setDataValidacao(LocalDateTime.now());
    exame.setObservacoesValidacao(observacoes);  // ✅ NOVO

    exameRepository.save(exame);

    // ✅ NOVO: Atualizar indicadores do protocolo com base em VALIDADOS
    atualizarIndicadoresProtocolo(id);

    // ✅ NOVO: Recalcular status
    protocolo = buscarOuFalhar(id);
    protocolo.setStatus(protocolo.calcularStatusAutomatico());

    return toDTO(salvar(protocolo));
}
```

---

*Próximas correções serão executadas sequencialmente após P4 estar implementada*
