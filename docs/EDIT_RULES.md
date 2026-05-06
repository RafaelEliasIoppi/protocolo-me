# Política de Edição de Documentação

Princípios obrigatórios ao alterar, mover ou remover documentos Markdown neste repositório:

1. Pensar no fluxo inteiro (end-to-end): toda alteração deve considerar simultaneamente frontend e backend.
   - Antes de remover ou alterar documentação de API, verifique controladores, DTOs e serviços no backend.
   - Antes de mudar instruções de uso, verifique componentes, serviços e clientes HTTP no frontend.

2. Não deletar sem arquivar: nunca apagar diretamente um arquivo `.md` sem primeiro arquivá-lo em `docs/_archived/`.
   - Arquive com commit que explique motivo e a relação com backend/frontend verificada.

3. Verificação obrigatória:
   - Confirmar que não existem links internos ou referências de código que dependam do documento a ser removido.
   - Rodar testes automatizados relevantes (backend `mvn test`, frontend `npm run build`) e executar um smoke test manual do fluxo afetado.

4. Regra para arquivos de relatório antigos:
   - Relatórios históricos e material legado devem ser movidos para `docs/arquivo_legacy/` ou `docs/_archived/` e marcados com data e breve razão.

5. Aprovação:
   - Alterações que impactem contratos (endpoints, payloads, DTOs) exigem revisão por pelo menos um desenvolvedor backend e um frontend.

6. Registro:
   - Atualize `INDICE_DOCUMENTOS.md` quando arquivos forem arquivados ou excluídos.

Aplicando esta política reduzimos risco de gaps entre front e back e preservamos histórico para auditoria.

Gerado em 2026-05-06.
