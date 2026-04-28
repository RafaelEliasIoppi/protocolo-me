# Índice de Documentos - Protocolo ME

## ⭐ COMECE AQUI

Não sabe por onde começar? Leia: [GUIA_CODIGO_BACK_FRONT.md](GUIA_CODIGO_BACK_FRONT.md)
- Manual único com o fluxo completo do paciente
- Exemplos práticos de payload e endpoints
- Caminho front → service → controller → mapper → service → banco

---

## Documentos principais (manter)

- [COMO_RODAR.md](COMO_RODAR.md): execução local, portas, CORS e troubleshooting.
- [TESTING_GUIDE.md](TESTING_GUIDE.md): testes automatizados e smoke tests manuais.
- [README_START.md](README_START.md): atalho rápido de inicialização.
- [GUIA_CODIGO_BACK_FRONT.md](GUIA_CODIGO_BACK_FRONT.md): mapa por camadas backend/frontend.
- [GUIA_CLASSE_METODO_V2.md](GUIA_CLASSE_METODO_V2.md): detalhamento classe/método.
- [MAPA_VISUAL_DO_SISTEMA.md](MAPA_VISUAL_DO_SISTEMA.md): visão por diagramas.
- [FLUXO_GUIADO_MEDICO.md](FLUXO_GUIADO_MEDICO.md): fluxo operacional do médico.

### 📚 Documento oficial de Salvamento de Paciente

- [GUIA_CODIGO_BACK_FRONT.md](GUIA_CODIGO_BACK_FRONT.md): documento oficial e único para o fluxo de paciente.

### 🧹 Auditoria e Refatoração de Código

- [AUDITORIA_NOMES_FRONTEND.md](AUDITORIA_NOMES_FRONTEND.md): auditoria de nomes genéricos encontrados no frontend.
- [RELATORIO_FINAL_REFATORACAO_NOMES.md](RELATORIO_FINAL_REFATORACAO_NOMES.md): **LEIA** - Resumo completo da refatoração de 26 funções (140+ referências atualizadas).

### Documentos gerais

- [CHECKLIST_INTERATIVO_MEDICO.md](CHECKLIST_INTERATIVO_MEDICO.md): validação guiada do fluxo médico.
- [RELATORIO_AUDITORIA_TECNICA_BACKEND.md](RELATORIO_AUDITORIA_TECNICA_BACKEND.md): auditoria técnica atual do backend.
- [CHECKLIST_DEBUG_RAPIDO.md](CHECKLIST_DEBUG_RAPIDO.md): diagnóstico rápido de problemas comuns.

## Documentos removidos nesta limpeza

Os arquivos abaixo foram removidos por redundância e conteúdo desatualizado em relação ao estado atual do código:

- `COMECE_AQUI.md`
- `CONCLUSAO_IMPLEMENTACAO.md`
- `RESUMO_ANEXACAO_DOCUMENTOS.md`
- `SUMARIO_MUDANCAS.md`
- `TESTE_END_TO_END.md`
- `FLUXO_MEDICO_PASSO_A_PASSO.md`
- `MAPA_NAVEGACAO.md`
- `RESUMO_RAPIDO_SALVAMENTO_PACIENTE.md`
- `FLUXO_SALVAMENTO_PACIENTE.md`
- `TESTES_PRATICOS_SALVAMENTO_PACIENTE.md`
- `DIAGNOSTICO_ERRO_500_CPF.md`

## Regra de manutenção daqui para frente

- Evitar criar novos documentos de "resumo da sessão".
- Atualizar sempre os documentos principais acima.
- Manter documentos históricos apenas em `arquivo_legacy/` quando necessário.
