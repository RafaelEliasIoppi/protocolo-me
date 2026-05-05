# RELATÓRIO DE VARREDURA AGRESSIVA - Protocolo ME Frontend

**Data:** 2026-05-05
**Ferramenta:** depcheck + ESLint (import/no-unused-modules) + análise CSS manual

---

## 1. **DEPENDÊNCIAS NPM NÃO USADAS** ⚠️ (CRÍTICO)

**Ferramenta:** `depcheck`

Foram encontradas **2 dependências npm não usadas** no `package.json`:

### Candidatos à remoção imediata:

| Pacote | Status | Recomendação |
|--------|--------|--------------|
| `@supabase/ssr` | Não referenciado no código | **REMOVER** |
| `@supabase/supabase-js` | Não referenciado no código | **REMOVER** |

**Benefício:** Reduz tamanho da bundle em ~100+ KB.

**Comando de remoção:**
```bash
npm uninstall @supabase/ssr @supabase/supabase-js
```

---

## 2. **EXPORTS/IMPORTS JAVASCRIPT** ✅ (OK)

**Ferramenta:** ESLint com `import/no-unused-modules`

**Resultado:** Nenhum export não utilizado detectado.

- Todos os exports JavaScript nos serviços e componentes estão sendo importados em algum lugar.
- Nenhuma ação necessária.

---

## 3. **CLASSES CSS POSSIVELMENTE NÃO USADAS** ⚠️ (INVESTIGAR)

**Ferramenta:** Análise grep de `.css` vs `className` em JSX

**Resumo:**
- **Classes CSS definidas:** 505
- **Classes em uso em JSX:** 399
- **Classes possivelmente não usadas:** 189

### TOP 30 Classes CSS sem referência detectada:

```
aba
accent
aguardando
alerta
alerta-erro
alerta-sucesso
ativa
ativo
badge
badge-aguardando
badge-danger
badge-exame
badge-exame-negativo
badge-exame-pendente
badge-exame-positivo
badge-nao-realizado
badge-protocolo
badge-secondary
badge-success
badge-validado
badge-warning
btn-acao
btn-confirmacao
btn-danger
btn-info
btn-small
btn-status
btn-status-amarelo
btn-status-cinza
btn-status-verde
```

### ⚠️ AVISOS:

1. **Falsos Positivos:** Muitas classes CSS podem estar:
   - Usadas dinamicamente (Template strings, variáveis condicionais)
   - Definidas para uso futuro
   - Reutilizadas em múltiplos componentes com patterns similares

2. **Recomendação:** 
   - ✅ Revisar manualmente 5-10 classes principais antes de remover
   - ✅ Usar `purgecss` ou similar em produção para real-world usage
   - ❌ **NÃO remover em lote sem validação visual**

3. **Exemplo de Falso Positivo:**
   - Classes com nomes genéricos (`aba`, `ativa`) podem estar em uso condicional
   - Bootstrap e libs CSS customizadas podem referenciá-las dinamicamente

---

## 4. **RECOMENDAÇÕES POR PRIORIDADE**

### 🔴 Altíssima Prioridade (Ação Imediata):
1. **Remover Supabase packages** — Não usados, economiza espaço
   - Comando: `npm uninstall @supabase/ssr @supabase/supabase-js`

### 🟡 Média Prioridade (Investigar):
2. **CSS não usado** — Revisar manualmente antes de remover
   - Selecionar 5-10 classes críticas para validação visual
   - Considerar usar `purgecss` em build para production

### 🟢 Baixa Prioridade (OK):
3. **Exports JS** — Tudo está sendo utilizado, nenhuma ação necessária

---

## 5. **PRÓXIMOS PASSOS**

1. [ ] Remover `@supabase/ssr` e `@supabase/supabase-js` do package.json
2. [ ] Rodar testes e verificar se aplicação continua funcionando
3. [ ] (Opcional) Validar manualmente 10 classes CSS mais específicas
4. [ ] Fazer commit: `fix: remover dependências supabase não usadas`
5. [ ] Push e atualizar PR

---

