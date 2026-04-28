# Regra Geral de Ouro — Alterações no Sistema

Este documento registra a regra essencial que deve ser seguida antes de qualquer alteração no repositório/projeto.

Princípio central
-----------------

- O que funciona agora SÓ PODE SER ALTERADO com autorização explícita de responsáveis técnicos e do produto.
- Qualquer alteração que impacte comportamento em produção deve passar por aprovação formal antes de implementação.

Processo obrigatório antes de qualquer alteração
------------------------------------------------

1. Registrar a solicitação: abra um *issue* descrevendo o problema, justificativa da mudança e riscos.
2. Avaliação técnica: o Tech Lead (ou revisor técnico designado) analisa o impacto e define mitigação de riscos.
3. Aprovação de negócio: Product Owner confirma a necessidade e autoriza a mudança em produção.
4. Plano de testes: o autor propõe testes automatizados e passos de verificação manual; QA valida.
5. Pull Request (PR): crie um PR apontando o issue, descrevendo claramente o escopo e incluindo a tag `requires-authorization`.
6. Aprovações no PR: mínimo de uma aprovação técnica (Tech Lead) e uma aprovação de produto/PO.
7. Deploy controlado: após aprovação, o deploy deve seguir o processo de release definido (com rollback plan).

Exceções (hotfixes)
-------------------

- Em caso de correção crítica (hotfix), documente a ação imediatamente no issue/PR e notifique Tech Lead e PO.
- Hotfixes devem ser reduzidos ao mínimo necessário e serem seguidos por um post-mortem e PR revert/merge para a branch principal.

Como registrar a autorização
----------------------------

- A autorização válida deve constar no PR (comentário de aprovação) e no issue associado. Mensagens de chat ou e-mail sem registro no repositório não substituem a autorização.

Locais sensíveis (recomendação)
-------------------------------

- Não alterar arquivos de configuração de build, scripts de deploy, mapeamentos de segurança ou lógica crítica do backend sem autorização explícita.
- Exemplos de pastas: `backend/src/main/java`, `frontend/build` (artefatos), `scripts/`, `Makefile`.

Responsáveis sugeridos
----------------------

- Product Owner
- Tech Lead / Maintainer
- QA Lead

Observações finais
------------------

- Esta é a "Regra Geral de Ouro" do projeto: deve ser consultada e obedecida antes de qualquer mudança que altere comportamento em produção.
- Se desejar, posso adicionar um checklist de PR automático e um template de issue/PR para reforçar o processo.

Documento criado para registro e referência. Modificações neste arquivo também devem seguir a Regra Geral de Ouro.
