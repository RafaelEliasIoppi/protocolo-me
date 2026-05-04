# Verificação dos `useEffect` no Frontend

**Data:** 04/05/2026
**Escopo:** revisão dos efeitos em componentes do frontend do projeto `protocolo-me`

## Resultado resumido

- Os efeitos de inicialização, atualização de UI e sincronização estão, em geral, cumprindo seu papel.
- Foi corrigido um efeito inconsistente em `MedicoProtocoloME.js` que sincronizava o protocolo selecionado com dependência desatualizada.
- Foi adicionada proteção contra atualização após unmount nos loaders assíncronos de formulários e listas mais sensíveis.
- O frontend compilou com sucesso após os ajustes.

## Efeitos verificados

### `frontend/src/componentes/MedicoProtocoloME.js`

- Carregamento inicial de centrais, pacientes em protocolo e pacientes disponíveis: funciona como bootstrap da tela.
- Autoabertura do modal quando existe apenas um paciente em protocolo: faz sentido para o fluxo atual.
- Sincronização do `protocoloSelecionado` quando a lista de pacientes muda: foi ajustada para depender do ID do protocolo selecionado, evitando estado morto e leitura de dependência obsoleta.
- Os loaders assíncronos passaram a checar sentinela de montagem antes de aplicar `setState`.

### `frontend/src/componentes/CentralDashboardPage.js`

- Carregamento periódico do painel com `setInterval`: cumpre o papel de manter o dashboard atualizado.
- Persistência de configurações em `localStorage`: correto e previsível.
- Listener de `fullscreenchange`: correto, com cleanup.
- Rotação automática do telão: correta e com cleanup do timer.
- Reset da página ao mudar filtros em modo telão: adequado.

### `frontend/src/componentes/CentralPacientesPainel.js`

- Montagem de seções do relatório final a partir do texto editável: correto.
- O efeito reage a mudanças em `relatorioFinalPaciente` e `relatorioTextoPorProtocolo`, que são as entradas certas.

### `frontend/src/componentes/Dashboard.js`

- Carregamento inicial e atualização periódica com limpeza de intervalo: correto.
- Usa flag de atividade para evitar atualizações após unmount.

### `frontend/src/componentes/ExameMEManager.js`

- Limpeza de mensagens de erro e sucesso com timeout: correta.
- Recarrega exames quando `protocoloId` muda: cumpre a função.
- O carregamento de exames agora ignora respostas tardias após unmount.

### `frontend/src/componentes/PacientesPage.js`

- Carrega estatísticas ao montar: cumpre o papel esperado.
- Sem efeito colateral funcional observado.
- O carregamento inicial agora ignora respostas tardias após unmount.

### `frontend/src/componentes/PacienteForm.js`

- Carregamento inicial de hospitais, pacientes e estatísticas: cumpre a função de preparar o formulário.
- Sincronização do formulário quando a prop `paciente` muda: correta.
- O formulário volta para o estado padrão quando a prop `paciente` fica vazia.
- Os loaders assíncronos agora ignoram respostas tardias após unmount.

### `frontend/src/componentes/ProtocoloMEManager.js`

- Carregamento inicial da lista de protocolos: cumpre o papel esperado.
- O carregamento inicial agora ignora respostas tardias após unmount.

### `frontend/src/App.js`

- Sincronização do tema no `document.documentElement`: correto.

## Ajuste realizado

- Em `MedicoProtocoloME.js`, a sincronização do protocolo selecionado foi ajustada para depender do identificador do protocolo atual, removendo estado morto e evitando dependência desatualizada.
- Os componentes com fetch inicial sensível foram protegidos com sentinela de montagem para evitar `setState` após desmontagem.

## Observações de risco baixo

- Ainda existem efeitos que poderiam ser migrados para `AbortController` em uma segunda refatoração, mas o risco funcional principal de atualização após unmount foi mitigado.

## Validação executada

- `npm run build` em `frontend` concluído com sucesso após a segunda passada de correções.
