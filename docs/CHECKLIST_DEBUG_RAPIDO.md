# Checklist Rápido de Debug (Backend + Frontend + UX)

> Leia primeiro: [GUIA_MESTRA_DE_INICIO.md](GUIA_MESTRA_DE_INICIO.md). Use este checklist apenas quando precisar diagnosticar um erro.

Este guia serve para erros de fluxo como:
- ação bloqueada por regra de negócio no backend
- mensagem genérica no frontend
- problema visual de contraste em tema claro/escuro

---

## 1) Reproduzir e registrar o erro

Objetivo: evitar debug no escuro.

Passos:
1. Reproduza o fluxo exatamente como o usuário.
2. Copie a mensagem exata mostrada na tela.
3. Anote endpoint acionado e payload enviado.

Checklist:
- [ ] Mensagem exata registrada
- [ ] Endpoint identificado
- [ ] Perfil do usuário registrado (MEDICO, ENFERMEIRO, etc.)

---

## 2) Encontrar origem da mensagem no backend

Objetivo: descobrir a regra que bloqueou a operação.

Passos:
1. Buscar a mensagem no backend.
2. Localizar método de serviço que lança a exceção.
3. Confirmar qual pré-condição está faltando.

Exemplo deste projeto:
- ausência de central cadastrada bloqueia criação de protocolo

Checklist:
- [ ] Classe/método mapeados
- [ ] Condição de bloqueio identificada
- [ ] Regra de negócio validada com o time

---

## 3) Confirmar se é bug ou pré-condição válida

Objetivo: decidir a correção correta.

Critério:
- Se a regra é correta, melhorar orientação (não remover validação).
- Se a regra está errada, corrigir a regra.

Checklist:
- [ ] Regra mantida ou alterada com justificativa
- [ ] Mensagem de erro orientativa definida

---

## 4) Corrigir em duas camadas

### Backend

Objetivo: manter integridade da regra.

Boas práticas:
1. Centralizar validação em método único.
2. Lançar mensagem clara com ação recomendada.
3. Evitar duplicar lógica em múltiplos métodos.

Checklist:
- [ ] Validação centralizada
- [ ] Exceção com mensagem acionável
- [ ] Sem regressão no fluxo principal

### Frontend

Objetivo: prevenir erro antes do submit.

Boas práticas:
1. Buscar pré-condições ao carregar a tela.
2. Exibir alerta claro quando pré-condição falhar.
3. Desabilitar botão de ação inválida.
4. Mostrar caminho de correção (link para tela de cadastro).

Checklist:
- [ ] Pré-checagem implementada
- [ ] Alerta visível ao usuário
- [ ] Botão bloqueado corretamente
- [ ] Link/atalho para resolver a causa

---

## 5) Corrigir contraste no modo escuro

Objetivo: garantir legibilidade real.

Pontos críticos para revisar:
1. títulos principais (h1/h2/h3)
2. números de cards
3. textos de apoio (p, note)
4. badges e links
5. texto em estados de aviso/erro/sucesso

Checklist:
- [ ] Texto principal com contraste alto
- [ ] Texto secundário ainda legível
- [ ] Cores consistentes entre painéis/cards

---

## 6) Validar após a correção

Objetivo: fechar o ciclo com segurança.

Validação funcional:
1. Sem pré-condição: ação bloqueada com orientação.
2. Com pré-condição: fluxo completo funciona.
3. Mensagem final de sucesso/erro coerente.

Validação visual:
1. tema escuro: leitura confortável
2. tema claro: sem regressão
3. desktop e mobile

Checklist:
- [ ] Fluxo negativo validado
- [ ] Fluxo positivo validado
- [ ] Tema escuro validado
- [ ] Tema claro validado

---

## 7) Template curto para PR

Use este bloco no PR:

- Causa raiz: [descreva]
- Regra de negócio: [mantida/alterada]
- Correção backend: [resumo]
- Correção frontend: [resumo]
- Ajuste visual (tema): [resumo]
- Como validar:
  1. [passo]
  2. [passo]
  3. [passo]

---

## Aplicação no caso atual (protocolo ME)

Resumo:
1. O backend exige central cadastrada para criar protocolo.
2. O frontend passou a pré-validar se existem centrais.
3. O dashboard recebeu ajuste de contraste no modo escuro.

Comportamento esperado:
- Sem central: usuário recebe orientação e não consegue iniciar protocolo.
- Com central: protocolo é iniciado normalmente.
- Modo escuro: textos legíveis nos cards e painéis.
