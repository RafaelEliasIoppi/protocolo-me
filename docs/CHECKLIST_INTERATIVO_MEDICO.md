# Checklist Interativo do Medico

## Como usar
Marque cada etapa enquanto voce navega no sistema. A ideia e associar clique, request e efeito na tela.

---

## 1) Entrada no sistema

- [ ] Abrir a tela de login.
- [ ] Fazer login com um usuario MEDICO ou ENFERMEIRO.
- [ ] Confirmar que o token foi salvo no navegador.
- [ ] Verificar que a pagina principal abriu sem 401.

### Validacao
- [ ] O menu mostra o link Meu Protocolo ME.
- [ ] O usuario aparece com a role correta.

---

## 2) Abrir o fluxo do medico

- [ ] Clicar em Meu Protocolo ME.
- [ ] Conferir se a lista de pacientes em protocolo carregou.
- [ ] Verificar se nao existem cards repetidos para o mesmo paciente.
- [ ] Confirmar que cada card mostra nome, CPF, hospital e status.

### Validacao
- [ ] O painel mostra o proximo passo do paciente.
- [ ] A lista veio do endpoint /api/protocolos-me.

---

## 3) Abrir exames

- [ ] Clicar em Acessar Protocolo.
- [ ] Conferir se a aba de exames abriu.
- [ ] Verificar se o resumo de exames apareceu.
- [ ] Confirmar que o formulario de novo exame mostra tipos disponiveis.

### Validacao
- [ ] O endpoint /api/exames-me/protocolo/{id} foi chamado.
- [ ] O resumo de exames carregou sem erro.

---

## 4) Criar um exame

- [ ] Selecionar um tipo de exame ainda nao usado.
- [ ] Preencher descricao ou resultado quando necessario.
- [ ] Salvar o exame.
- [ ] Confirmar que ele apareceu na lista.

### Validacao
- [ ] O backend nao aceitou duplicidade do mesmo tipo.
- [ ] O contador de exames foi atualizado.
- [ ] O card do paciente continuou consistente depois do refresh.

---

## 5) Registrar resultado

- [ ] Abrir um exame existente.
- [ ] Registrar resultado positivo, negativo ou texto.
- [ ] Salvar o resultado.
- [ ] Conferir se o item mudou de estado.

### Validacao
- [ ] O status do protocolo foi recalculado.
- [ ] O paciente exibido no painel refletiu a mudanca.
- [ ] Nao apareceu 403 ao atualizar.

---

## 6) Conferir o status automatico

- [ ] Verificar se o protocolo saiu de NOTIFICADO.
- [ ] Verificar se o fluxo passou para EM_PROCESSO ou Morte Cerebral Confirmada.
- [ ] Conferir se o espelho do paciente tambem mudou.

### Validacao
- [ ] O backend ajustou o status sem acao manual extra.
- [ ] O frontend sincronizou sem recarregar a tela inteira.

---

## 7) Abrir entrevista familiar

- [ ] Clicar em Realizar Entrevista.
- [ ] Confirmar que a entrevista so aparece no momento certo.
- [ ] Marcar que a familia foi notificada.
- [ ] Definir se a doacao foi autorizada ou recusada.
- [ ] Salvar o resultado final.

### Validacao
- [ ] O status da entrevista foi atualizado no protocolo.
- [ ] O paciente tambem recebeu o espelho do resultado.
- [ ] A tela passou a mostrar autorizada ou recusada.

---

## 8) Abrir o painel da Central

- [ ] Abrir o painel da Central.
- [ ] Conferir se os pacientes apareceram em modo somente leitura.
- [ ] Selecionar um paciente.
- [ ] Carregar o relatorio final.

### Validacao
- [ ] O relatorio trouxe protocolos, exames e entrevista.
- [ ] O status final apareceu de forma clara.
- [ ] O total de exames bate com o que foi visto no medico.

---

## 9) Exportar o relatorio

- [ ] Exportar o relatorio como CSV.
- [ ] Abrir a versao imprimivel.
- [ ] Confirmar que o nome do arquivo foi gerado corretamente.

### Validacao
- [ ] O arquivo baixado tem o nome do paciente.
- [ ] A impressao mostra conclusao final e resumo dos protocolos.

---

## 10) Pergunta final para checar entendimento

Se algo falhar, responda sempre:

- [ ] O problema esta na tela?
- [ ] O problema esta na request?
- [ ] O problema esta no controller?
- [ ] O problema esta no service?
- [ ] O problema esta na regra de negocio?
- [ ] O problema esta na seguranca?

Se voce conseguir responder isso, voce ja sabe onde olhar primeiro.

---

## Caminho visual resumido

```text
LOGIN -> LISTA DE PACIENTES -> EXAMES -> STATUS AUTOMATICO -> ENTREVISTA -> CENTRAL -> RELATORIO
```
