# Sistema de Central de Transplantes - Protocolos de Morte Encefálica (ME)

## 📋 Descrição

Sistema completo para gerenciamento de protocolos de Morte Encefálica (ME) pela Central de Transplantes, incluindo controle de transferências de órgãos, notificações, testes clínicos e rastreamento de doadores.

## 🚀 Funcionalidades Principais

### Central de Transplantes

#### Backend
- **Cadastro de Centrais**: Criar e gerenciar centros de transplante
- **Vinculação de Hospitais**: Associar hospitais parceiros à central
- **Gerenciamento de Status**: ATIVO, INATIVO, PLANTÃO, MANUTENÇÃO
- **Dados de Contato**: Telefone, email, informações de plantão
- **Coordenação**: Gerenciar coordenador responsável
- **Capacidade**: Registrar capacidade de processamento e especialidades

#### Frontend
- **Cadastro/Edição**: Formulário completo de registros
- **Vinculação de Hospitais**: Interface para gerenciar parcerias
- **Alteração de Status**: Gerenciar operacionalidade da central

### Protocolos de Morte Encefálica

#### Backend
- **Criar Protocolos**: Registrar novos casos de ME
- **Testes Clínicos**: Registrar teste clínico 1, teste clínico 2, testes complementares
- **Confirmação de ME**: Confirmar morte cerebral
- **Notificações**: Registrar notificação da família e autorização para doação
- **Preservação**: Gerenciar preservação de órgãos
- **Rastreamento**: Acompanhar todo o fluxo do protocolo
- **Filtros Avançados**: Por status, período, hospital origem, central

#### Frontend
- **Gerenciador**: Interface completa para gerenciar protocolos
- **Checklist**: Lista de verificação de procedimentos
- **Alteração de Status**: Estados progressivos do protocolo
- **Registros de Procedimentos**: Confirmar teste clínico, notificação, etc.

## 📊 Modelos de Dados

### CentralTransplantes
```
id: Long
nome: String (UNIQUE)
cnpj: String (UNIQUE)
endereco: String
cidade: String
estado: String
telefone: String
telefonePlantao: String
email: String
emailPlantao: String
coordenador: String
telefoneCoordenador: String
statusOperacional: StatusCentral (ENUM)
capacidadeProcessamento: Integer
especialidadesOrgaos: String
hospitaisParceados: List<Hospital>
protocolosME: List<ProtocoloME>
usuarios: List<Usuario>
dataCriacao: LocalDateTime
dataAtualizacao: LocalDateTime
```

### ProtocoloME
```
id: Long
numeroProtocolo: String (UNIQUE)
centralTransplantes: CentralTransplantes
paciente: Paciente
hospitalOrigem: String
medicoResponsavel: String
enfermeiro: String
status: StatusProtocoloME (ENUM)
diagnosticoBasico: String
causaMorte: String
observacoes: String (2000 chars)
testeClinico1Realizado: Boolean
dataTesteClinico1: LocalDateTime
testeClinico2Realizado: Boolean
dataTesteClinico2: LocalDateTime
testesComplementaresRealizados: Boolean
testesComplementares: String
dataTesteComplementar: LocalDateTime
familiaNotificada: Boolean
dataNotificacaoFamilia: LocalDateTime
autopsiaAutorizada: Boolean
orgaosDisponiveis: String
preservacaoOrgaos: Boolean
dataPreservacao: LocalDateTime
dataNotificacao: LocalDateTime
dataConfirmacaoME: LocalDateTime
dataSaidaHospital: LocalDateTime
dataCriacao: LocalDateTime
dataAtualizacao: LocalDateTime
```

## 🔌 Endpoints da API

### Central de Transplantes

#### Criar Central
```
POST /api/centrais-transplantes
Content-Type: application/json

{
  "nome": "Central de Transplantes São Paulo",
  "cnpj": "12345678000190",
  "endereco": "Avenida Paulista, 1000",
  "cidade": "São Paulo",
  "estado": "SP",
  "telefone": "(11) 3000-0000",
  "telefonePlantao": "(11) 98765-4321",
  "email": "central@hospital.com",
  "emailPlantao": "plantao@hospital.com",
  "coordenador": "Dr. Carlos Silva",
  "telefoneCoordenador": "(11) 99999-9999",
  "capacidadeProcessamento": 50,
  "especialidadesOrgaos": "Coração, Pulmão, Fígado, Rim"
}
```

#### Listar Centrais
```
GET /api/centrais-transplantes
```

#### Buscar por ID
```
GET /api/centrais-transplantes/{id}
```

#### Buscar por CNPJ
```
GET /api/centrais-transplantes/cnpj/{cnpj}
```

#### Alterar Status
```
PATCH /api/centrais-transplantes/{id}/status
Params: status=ATIVO|INATIVO|PLANTAO|MANUTENCAO
```

#### Vincular Hospital
```
POST /api/centrais-transplantes/{centralId}/hospitais/{hospitalId}
```

### Protocolos de ME

#### Criar Protocolo
```
POST /api/protocolos-me
Content-Type: application/json

{
  "numeroProtocolo": "ME-2024-001",
  "centralTransplantesId": 1,
  "pacienteId": 1,
  "hospitalOrigem": "Hospital A",
  "medicoResponsavel": "Dr. João",
  "diagnosticoBasico": "Traumatismo craniano",
  "causaMorte": "Morte cerebral",
  "orgaosDisponiveis": "Coração, Pulmão, Fígado"
}
```

#### Listar Protocolos de uma Central
```
GET /api/protocolos-me/central/{centralId}
```

#### Listar por Status
```
GET /api/protocolos-me/status/{status}
Status: NOTIFICADO|EM_PROCESSO|MORTE_CEREBRAL_CONFIRMADA|FAMILIA_INFORMADA|ORGAOS_PRESERVADOS|APTO_TRANSPLANTE|CONTRAINDICADO|FINALIZADO
```

#### Registrar Teste Clínico 1
```
POST /api/protocolos-me/{id}/teste-clinico-1
```

#### Registrar Teste Clínico 2
```
POST /api/protocolos-me/{id}/teste-clinico-2
```

#### Confirmar Morte Cerebral
```
POST /api/protocolos-me/{id}/confirmar-morte-cerebral
```

#### Notificar Família
```
POST /api/protocolos-me/{id}/notificar-familia
```

#### Preservar Órgãos
```
POST /api/protocolos-me/{id}/preservacao-orgaos
```

#### Alterar Status
```
PATCH /api/protocolos-me/{id}/status
Params: status=NOTIFICADO|EM_PROCESSO|...
```

## 🎨 Estados dos Protocolos de ME

| Status | Cor | Descrição |
|--------|-----|-----------|
| NOTIFICADO | Azul | Central notificada de possível doador |
| EM_PROCESSO | Amarelo | Testes clínicos em andamento |
| MORTE_CEREBRAL_CONFIRMADA | Laranja | ME confirmada clinicamente |
| FAMILIA_INFORMADA | Verde | Família informada e consentimento obtido |
| ORGAOS_PRESERVADOS | Roxo | Órgãos em preservação para transporte |
| APTO_TRANSPLANTE | Verde Escuro | Doador apto para transplante |
| CONTRAINDICADO | Vermelho | Contraindicação para doação |
| FINALIZADO | Cinza | Protocolo finalizado |

## 🔐 Sistema de Roles e Permissões

```
ADMIN
  - gerenciar_usuarios
  - gerenciar_hospitais
  - gerenciar_centrais

COORDENADOR_TRANSPLANTES
  - gerenciar_protocolos
  - visualizar_disponibilidade
  - atualizar_status

MEDICO
  - visualizar_protocolos
  - atualizar_protocolo
  - registrar_observacoes

ENFERMEIRO
  - visualizar_protocolos
  - registrar_observacoes

CENTRAL_TRANSPLANTES
  - gerenciar_protocolos_me
  - registrar_doadores
  - atualizar_protocolo_me
```

## 📁 Estrutura de Arquivos Backend

```
backend/src/main/java/back/backend/
├── model/
│   ├── Role.java
│   ├── Usuario.java
│   ├── CentralTransplantes.java
│   ├── ProtocoloME.java
│   ├── Hospital.java
│   └── Paciente.java
├── repository/
│   ├── CentralTransplantesRepository.java
│   ├── ProtocoloMERepository.java
│   ├── HospitalRepository.java
│   ├── UsuarioRepository.java
│   └── PacienteRepository.java
├── service/
│   ├── CentralTransplantesService.java
│   ├── ProtocoloMEService.java
│   ├── HospitalService.java
│   ├── UsuarioService.java
│   └── PacienteService.java
└── controller/
    ├── CentralTransplantesController.java
    ├── ProtocoloMEController.java
    ├── HospitalController.java
    ├── UsuarioController.java
    └── PacienteController.java
```

## 📁 Estrutura de Arquivos Frontend

```
frontend/src/
├── componentes/
│   ├── CentralTransplantesForm.js
│   ├── ProtocoloMEManager.js
│   ├── HospitalForm.js
│   └── PacienteForm.js
└── styles/
    ├── CentralTransplantesForm.css
    ├── ProtocoloMEManager.css
    ├── HospitalForm.css
    └── PacienteForm.css
```

## 🎯 Fluxo de um Protocolo de ME

```
1. NOTIFICADO
   └─→ Hospital notifica central de possível doador

2. EM_PROCESSO
   └─→ Registrar Teste Clínico 1
   └─→ Registrar Teste Clínico 2
   └─→ Registrar Testes Complementares

3. MORTE_CEREBRAL_CONFIRMADA
   └─→ Confirmar morte cerebral com 2 testes clínicos

4. FAMILIA_INFORMADA
   └─→ Notificar família
   └─→ Obter consentimento

5. ORGAOS_PRESERVADOS
   └─→ Registrar preservação de órgãos
   └─→ Preparar para transporte

6. APTO_TRANSPLANTE ou CONTRAINDICADO
   └─→ Final do protocolo

7. FINALIZADO
   └─→ Protocolo encerrado
```

## 💾 Banco de Dados

Tabelas criadas automaticamente:
- `central_transplantes`
- `protocolo_me`
- `central_hospitais` (Many-to-Many)
- `usuario`
- `hospital`
- `paciente`

## ⚙️ Integração com Frontend

Os componentes utilizam Axios para comunicar com a API:

```javascript
// Criar central
axios.post('/api/centrais-transplantes', dados)

// Criar protocolo
axios.post('/api/protocolos-me', dados)

// Registrar test clínico
axios.post(`/api/protocolos-me/${id}/teste-clinico-1`)

// Alterar status
axios.patch(`/api/protocolos-me/${id}/status`, {}, { params: { status: 'MORTE_CEREBRAL_CONFIRMADA' } })
```

## 🚨 Validações

### Central de Transplantes
- CNPJ único obrigatório
- Nome, endereço, cidade obrigatórios
- Coordenador responsável obrigatório

### Protocolo de ME
- Número de protocolo único
- Hospital origem obrigatório
- Status segue fluxo progressivo

## 🔍 Filtros e Buscas

```javascript
// Listar por status
GET /api/protocolos-me/status/MORTE_CEREBRAL_CONFIRMADA

// Listar por período
GET /api/protocolos-me/periodo?dataInicio=2024-01-01T00:00:00&dataFim=2024-01-31T23:59:59

// Listar por hospital origem
GET /api/protocolos-me/hospital/Hospital%20A

// Listar por central e status
GET /api/protocolos-me/central/1/status/EM_PROCESSO
```

## 📝 Observações Importantes

1. **Sigilo Médico**: O sistema respeita confidencialidade de dados de pacientes
2. **Rastreabilidade**: Todos os eventos são registrados com data/hora
3. **Auditoria**: Mudanças de status são auditadas
4. **Comunicação**: Sistema integrado com notificações para hospitais
5. **Protocolos Legais**: Segue regulamentações de ME no Brasil

## ✅ Checklist de Implementação

- [x] Modelo CentralTransplantes criado
- [x] Modelo ProtocoloME criado
- [x] Modelo Role com permissões
- [x] Modelo Usuario criado
- [x] Repository para Central criado
- [x] Repository para Protocolo criado
- [x] Service para Central criado
- [x] Service para Protocolo criado
- [x] Controller para Central criado
- [x] Controller para Protocolo criado
- [x] Componente CentralTransplantesForm criado
- [x] Componente ProtocoloMEManager criado
- [x] Estilos CSS completos
- [x] Validações de entrada
- [x] Tratamento de erros
- [x] Documentação completa

## 🔗 Relacionamento com Outras Entidades

```
CentralTransplantes
├─→ Hospital (Many-to-Many)
├─→ ProtocoloME (One-to-Many)
└─→ Usuario (One-to-Many)

ProtocoloME
├─→ CentralTransplantes (Many-to-One)
├─→ Paciente (Many-to-One)
└─→ Usuario (através de atualizações)

Usuario
├─→ Hospital (Many-to-One)
└─→ CentralTransplantes (Many-to-One)
```

---

**Desenvolvido para: Sistema de Transportadora de Pacientes**
**Versão: 1.0**
**Data: 2024**
