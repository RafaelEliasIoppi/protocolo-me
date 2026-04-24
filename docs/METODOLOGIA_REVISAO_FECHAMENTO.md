# Metodologia de Revisao com Fechamento

Objetivo: corrigir por blocos, sem abrir novas frentes antes de fechar a atual.

## Regra principal
- Trabalhar por ciclos curtos com escopo fixo.
- Cada ciclo so termina com: codigo ajustado + validacao tecnica + regressao de contrato.
- Nao iniciar novo ciclo enquanto houver pendencia no ciclo atual.

## Ciclo padrao (sempre igual)
1. Congelar escopo
- Escolher um bloco unico: Controller, Service, DTO, Integracao Front-Back, Seguranca, Testes.
- Listar arquivos alvo do ciclo.

2. Levantamento estruturado
- Buscar apenas 3 classes de problema:
  - Contrato (endpoint, payload, status HTTP).
  - Regra de negocio em camada errada.
  - Validacao/tratamento de erro.
- Classificar cada achado em Alta, Media, Baixa.

3. Correcao orientada por contrato
- Corrigir primeiro Alta, depois Media, depois Baixa.
- Aplicar mudancas minimas necessarias.
- Manter compatibilidade com o frontend existente quando possivel.

4. Gate de validacao obrigatorio
- Backend: compile limpo.
- Frontend: sem erros estaticos nos arquivos alterados.
- Contrato: conferir endpoint, metodo HTTP, payload e resposta.

5. Encerramento do ciclo
- Registrar o que foi corrigido.
- Registrar risco residual.
- Definir proximo ciclo.

## Ordem fixa dos ciclos neste projeto
1. Contratos API (frontend x backend)
2. Controllers finos + DTO + @Valid
3. Services (regra de negocio e parse/conversao)
4. Exception handling e seguranca
5. Testes de regressao dos pontos criticos

## Checklist de saida (Definition of Done)
- [ ] Nao ha endpoint usado no frontend sem mapping no backend.
- [ ] Nao ha payload divergente entre frontend e backend.
- [ ] Controllers sem regra de negocio relevante.
- [ ] Entradas com validacao efetiva (@Valid + constraints no DTO).
- [ ] ResponseEntity com tipo e status adequados.
- [ ] Erros de negocio tratados por handler global.
- [ ] Compile backend ok.
- [ ] Sem erros estaticos nos arquivos alterados do frontend.

## Politica anti-dispersao
- Se surgir novo erro fora do escopo do ciclo, registrar em backlog e nao interromper o ciclo atual.
- So abrir novo escopo apos checklist de saida concluido.
