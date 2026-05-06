# Relatório do Processo de Cadastro de Protocolo ME

## Objetivo

Registrar o teste ponta a ponta do fluxo de cadastro de um novo Protocolo de Morte Encefálica (ME) usando um usuário médico já cadastrado no sistema.

## Ambiente utilizado

- Backend: `http://localhost:2500`
- Frontend: `http://localhost:3000`
- Usuário testado: `medico@gmail.com`
- Senha utilizada no teste: `Admin123!`

## Passo a passo executado

### 1. Autenticação do médico

Foi realizado login no endpoint de autenticação da API:

```bash
POST /api/usuarios/login
{
  "email": "medico@gmail.com",
  "senha": "Admin123!"
}
```

Resultado:
- login aceito;
- token JWT retornado com role `MEDICO`;
- usuário identificado com sucesso no backend.

### 2. Consulta dos pacientes elegíveis

Após a autenticação, foi consultada a lista de pacientes internados sem protocolo ativo:

```bash
GET /api/pacientes/status/INTERNADO/sem-protocolo-ativo
```

Resultado observado:
- 4 pacientes elegíveis retornados;
- exemplos retornados:
  - Maria Silva Teste
  - Ana Costa Teste
  - João da Silva
  - Teste Final

### 3. Cadastro do novo Protocolo ME

Foi criado um novo protocolo ME para o paciente de id `6`:

```bash
POST /api/protocolos-me
{
  "pacienteId": 6,
  "diagnosticoBasico": "Suspeita de morte encefálica - teste",
  "numeroProtocolo": ""
}
```

Resultado:
- protocolo criado com sucesso;
- status inicial: `NOTIFICADO`;
- protocolo retornado com id `4`;
- paciente vinculado: `Maria Silva Teste`.

### 4. Validação do resultado

Foi conferido se o paciente saiu da lista de disponíveis e passou a constar em protocolo:

- paciente `6` não aparece mais entre os disponíveis;
- paciente `6` aparece na listagem de pacientes em protocolo ME.

## Resultado final

O fluxo de cadastro de protocolo ME funcionou corretamente com o usuário médico cadastrado.

Resumo do teste:
- login realizado com sucesso;
- lista de pacientes elegíveis exibida corretamente;
- protocolo criado com sucesso;
- paciente atualizado corretamente após o cadastro.

## Observação técnica

Durante a validação do frontend, foi confirmado que a aplicação está disponível na porta `3000`, enquanto o backend permanece em `2500`.
