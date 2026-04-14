# Sistema de Pacientes - Documentação Completa

## 📋 Visão Geral

O Sistema de Pacientes é o módulo central de gerenciamento de informações dos pacientes internados que podem entrar em protocolo de Morte Encefálica (ME).

**Status**: ✅ **COMPLETO E FUNCIONAL**

---

## 🏗️ Arquitetura

### Backend (Java/Spring Boot)

#### **Paciente.java** (Model/Entity)
Local: `/backend/src/main/java/back/backend/model/Paciente.java`

```java
@Entity
@Table(name = "paciente")
public class Paciente {
    - id (PK)
    - nome (String, NOT NULL, UNIQUE)
    - cpf (String, NOT NULL, UNIQUE)
    - dataNascimento (LocalDate, NOT NULL)
    - genero (Enum: MASCULINO, FEMININO, OUTRO)
    - hospital (ManyToOne → Hospital)
    - leito (String)
    - dataInternacao (LocalDate)
    - diagnosticoPrincipal (TEXT)
    - historicoMedico (TEXT)
    - nomeResponsavel (String)
    - telefonoResponsavel (String)
    - emailResponsavel (String)
    - status (Enum: PRE_INTERNACAO, INTERNADO, EM_PROTOCOLO_ME, APTO_TRANSPLANTE, NAO_APTO, RECUSADO, EXODO)
    - protocolosME (OneToMany → ProtocoloME, cascade)
    - dataCriacao (LocalDateTime, auto)
    - dataAtualizacao (LocalDateTime, auto)
}
```

#### **PacienteRepository.java**
Local: `/backend/src/main/java/back/backend/repository/PacienteRepository.java`

**Métodos de Query**:
- `findByCpf(String)` - Encontrar paciente por CPF
- `findByHospital(Hospital)` - Listar pacientes de um hospital
- `findByStatus(StatusPaciente)` - Listar por status
- `findByHospitalAndStatus(Hospital, StatusPaciente)` - Filtro duplo
- `findPacientesByHospitalAndDataNascimento(Hospital, LocalDate, LocalDate)` - Range de datas
- `findByNomeContainingIgnoreCase(String)` - Busca fuzzy por nome
- `countByStatus(StatusPaciente)` - Contar por status
- `countByHospital(Hospital)` - Contar por hospital

#### **PacienteService.java**
Local: `/backend/src/main/java/back/backend/service/PacienteService.java`

**Métodos Principais**:

```
CRUD Básico:
├─ criarPaciente(Paciente)
├─ obterPacientePorId(Long)
├─ obterPacientePorCpf(String)
├─ atualizarPaciente(Long, Paciente)
├─ atualizarStatus(Long, StatusPaciente)
└─ deletarPaciente(Long)

Listagem e Busca:
├─ listarTodos()
├─ listarPorHospital(Long)
├─ listarPorStatus(StatusPaciente)
├─ listarPorHospitalEStatus(Long, StatusPaciente)
├─ procurarPorNome(String)
├─ procurarPorNomeEHospital(Long, String)

Análise e Estatísticas:
└─ obterEstatisticas() → PacienteStatisticas
   - totalPacientes
   - pacientesInternados
   - pacientesEmProtocoloME
   - pacientesAptosTransplante
   - pacientesNaoAptos
```

#### **PacienteController.java**
Local: `/backend/src/main/java/back/backend/controller/PacienteController.java`

### Frontend (React)

#### **PacienteForm.js** (Component)
Local: `/frontend/src/componentes/PacienteForm.js`

**Features**:
- 📊 Estatísticas em cards (5 métricas)
- 📝 Formulário completo para criar/editar pacientes
- 🔍 Filtros avançados (nome, status, hospital)
- 📋 Grid de pacientes com cards informativos
- 🏁 Status badges com cores personalizadas
- ✏️ Edição inline de registros
- 🗑️ Deleção com confirmação
- ⚡ Real-time updates

**Props Aceitas**:
```javascript
<PacienteForm 
  paciente={pacienteItem}  // Opcional: para editar
  onSave={handleSave}       // Callback após salvar
  onCancel={handleCancel}   // Callback ao cancelar
/>
```

#### **PacienteForm.css**
Local: `/frontend/src/styles/PacienteForm.css`

**Responsivo**: Sim (mobile-first design)
**Cores**: Tema azul/verde com cores específicas por status

---

## 🔌 API REST Endpoints

### Base URL: `http://localhost:8080/api/pacientes`

#### **1. Criar Paciente**
```
POST /api/pacientes
Content-Type: application/json

{
  "nome": "João Silva",
  "cpf": "123.456.789-00",
  "dataNascimento": "1980-05-15",
  "genero": "MASCULINO",
  "hospital": { "id": 1 },
  "leito": "UTI 205",
  "dataInternacao": "2024-01-10",
  "diagnosticoPrincipal": "Traumatismo craniano grave",
  "historicoMedico": "Sem comorbidades",
  "nomeResponsavel": "Maria Silva",
  "telefonoResponsavel": "(11) 98765-4321",
  "emailResponsavel": "maria@email.com",
  "status": "INTERNADO"
}

Response: 201 CREATED
{
  "id": 1,
  "nome": "João Silva",
  "cpf": "123.456.789-00",
  "dataCriacao": "2024-01-10T10:30:00",
  ...
}
```

#### **2. Listar Todos**
```
GET /api/pacientes

Response: 200 OK
[
  { ... paciente 1 ... },
  { ... paciente 2 ... },
  ...
]
```

#### **3. Obter por ID**
```
GET /api/pacientes/{id}

Response: 200 OK
{ ... paciente detalhado ... }
```

#### **4. Obter por CPF**
```
GET /api/pacientes/cpf/{cpf}

Response: 200 OK
{ ... paciente ... }
```

#### **5. Listar por Hospital**
```
GET /api/pacientes/hospital/{hospitalId}

Response: 200 OK
[ ...lista de pacientes do hospital... ]
```

#### **6. Listar por Status**
```
GET /api/pacientes/status/{status}

Valores: PRE_INTERNACAO, INTERNADO, EM_PROTOCOLO_ME, APTO_TRANSPLANTE, NAO_APTO, RECUSADO, EXODO

Response: 200 OK
[ ...lista de pacientes com esse status... ]
```

#### **7. Listar por Hospital e Status**
```
GET /api/pacientes/hospital/{hospitalId}/status/{status}

Response: 200 OK
[ ...lista filtrada... ]
```

#### **8. Buscar por Nome**
```
GET /api/pacientes/buscar?nome={nome}

Response: 200 OK
[ ...pacientes cujo nome contém o texto... ]
```

#### **9. Buscar por Nome em Hospital**
```
GET /api/pacientes/hospital/{hospitalId}/buscar?nome={nome}

Response: 200 OK
[ ...pacientes encontrados naquele hospital... ]
```

#### **10. Atualizar Paciente**
```
PUT /api/pacientes/{id}
Content-Type: application/json

{
  "nome": "João Silva Atualizado",
  "dataNascimento": "1980-05-15",
  "genero": "MASCULINO",
  "leito": "UTI 206",
  "diagnosticoPrincipal": "TC mostra lesão bilateral",
  ...
}

Response: 200 OK
{ ... paciente atualizado ... }
```

#### **11. Atualizar Status**
```
PATCH /api/pacientes/{id}/status
Content-Type: application/json

{
  "status": "EM_PROTOCOLO_ME"
}

Response: 200 OK
{ ... paciente com novo status ... }
```

#### **12. Deletar Paciente**
```
DELETE /api/pacientes/{id}

Response: 204 NO CONTENT
```

#### **13. Obter Estatísticas**
```
GET /api/pacientes/estatisticas/resumo

Response: 200 OK
{
  "totalPacientes": 45,
  "pacientesInternados": 28,
  "pacientesEmProtocoloME": 8,
  "pacientesAptosTransplante": 5,
  "pacientesNaoAptos": 4
}
```

---

## 🎯 Fluxo de Uso

### 1. **Criar Novo Paciente**
```
Sequência:
1. Acessar: PacienteForm component
2. Preencher: nome, CPF, data nasc, gênero, hospital
3. Adicionar: leito, data internação
4. Descrever: diagnóstico principal, histórico
5. Responsável: nome, telefone, email
6. Clicar: "Criar Paciente"
7. Sistema: Salva no banco, atualiza lista
```

### 2. **Consultar Paciente**
```
Opções:
A) Listar todos → G ET /api/pacientes
B) Por CPF → GET /api/pacientes/cpf/{cpf}
C) Por Hospital → GET /api/pacientes/hospital/{hospitalId}
D) Por Status → GET /api/pacientes/status/{status}
E) Buscar Nome → GET /api/pacientes/buscar?nome={nome}
```

### 3. **Atualizar Status para Protocolo ME**
```
1. Encontrar paciente na lista
2. Clicar no select de status
3. Selecionar: "EM_PROTOCOLO_ME"
4. Sistema: Atualiza status
5. Resultado: Cor do card muda para laranja
6. Próximo Passo: Criar protocolo_me para esse paciente
```

### 4. **Editar Informações**
```
1. Clicar: "Editar" no card paciente
2. Sistema: Carrega dados no formulário
3. Modificar: Qualquer campo (exceto CPF)
4. Salvar: Clica "Atualizar Paciente"
```

### 5. **Analisar Estatísticas**
```
Cards visíveis na tela:
├─ Total de Pacientes
├─ Internados
├─ Em Protocolo ME
├─ Aptos Transplante
└─ Não Aptos
```

---

## 📊 Statuses de Paciente

| Status | Cor | Significado | Próximo |
|--------|-----|-------------|---------|
| **PRE_INTERNACAO** | - | Aguardando internação | INTERNADO |
| **INTERNADO** | 🔴 Vermelho | Internado no hospital | EM_PROTOCOLO_ME ou ALTA |
| **EM_PROTOCOLO_ME** | 🟠 Laranja | Em protocolo de ME | APTO_TRANSPLANTE ou NAO_APTO |
| **APTO_TRANSPLANTE** | 🟢 Verde | Desencadeado fluxo de transplante | EXODO |
| **NAO_APTO** | ⚫ Cinza | Não atende critérios | EXODO |
| **RECUSADO** | 🔴 Vermelho escuro | Família recusou doação | EXODO |
| **EXODO** | ⚫ Azul escuro | Falecido | - |

---

## 🔐 Validações

### No Backend (PacienteService)

```java
✓ Nome: Obrigatório, não vazio
✓ CPF: Obrigatório, único
✓ Data Nascimento: Obrigatória, não pode ser futura
✓ Gênero: Obrigatório
✓ Hospital: Obrigatório, deve existir
✓ Status: Padrão = INTERNADO
```

### No Frontend

```javascript
✓ Campos obrigatórios marcados com *
✓ Input date previne datas futuras
✓ Select garante apenas valores válidos
✓ Validação em tempo real do formulário
✓ Confirmação antes de deletar
```

---

## 💾 Banco de Dados

### Tabela: `paciente`

```sql
CREATE TABLE paciente (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  nome VARCHAR(255) NOT NULL,
  cpf VARCHAR(20) NOT NULL UNIQUE,
  data_nascimento DATE NOT NULL,
  genero VARCHAR(20) NOT NULL,
  hospital_id BIGINT NOT NULL,
  leito VARCHAR(100),
  data_internacao DATE,
  diagnostico_principal TEXT,
  historico_medico TEXT,
  nome_responsavel VARCHAR(255),
  telefono_responsavel VARCHAR(20),
  email_responsavel VARCHAR(255),
  status VARCHAR(50) NOT NULL DEFAULT 'INTERNADO',
  data_criacao TIMESTAMP NOT NULL,
  data_atualizacao TIMESTAMP NOT NULL,
  FOREIGN KEY (hospital_id) REFERENCES hospital(id)
);
```

### Índices
```sql
CREATE INDEX idx_paciente_cpf ON paciente(cpf);
CREATE INDEX idx_paciente_hospital ON paciente(hospital_id);
CREATE INDEX idx_paciente_status ON paciente(status);
CREATE INDEX idx_paciente_nome ON paciente(nome);
```

---

## 🛠️ Como Integrar ao Dashboard

```javascript
import PacienteForm from './componentes/PacienteForm';

function Dashboard() {
  return (
    <div>
      <PacienteForm />
    </div>
  );
}
```

---

## 📝 Exemplos de Uso

### Exemplo 1: Criar Paciente via cURL

```bash
curl -X POST http://localhost:8080/api/pacientes \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Ana Silva",
    "cpf": "123.456.789-00",
    "dataNascimento": "1975-03-20",
    "genero": "FEMININO",
    "hospital": { "id": 1 },
    "leito": "CTI 310",
    "dataInternacao": "2024-01-12",
    "diagnosticoPrincipal": "AVC hemorrágico",
    "historicoMedico": "Hipertensão, diabetes",
    "nomeResponsavel": "Carlos Silva",
    "telefonoResponsavel": "(11) 99999-8888",
    "emailResponsavel": "carlos@email.com",
    "status": "INTERNADO"
  }'
```

### Exemplo 2: Buscar Paciente por Nome

```bash
curl http://localhost:8080/api/pacientes/buscar?nome=Ana
```

### Exemplo 3: Atualizar Status para Protocolo ME

```bash
curl -X PATCH http://localhost:8080/api/pacientes/1/status \
  -H "Content-Type: application/json" \
  -d '{ "status": "EM_PROTOCOLO_ME" }'
```

### Exemplo 4: Listar Pacientes do Hospital 1 em Protocolo ME

```bash
curl http://localhost:8080/api/pacientes/hospital/1/status/EM_PROTOCOLO_ME
```

---

## 🐛 Troubleshooting

| Erro | Causa | Solução |
|------|-------|---------|
| 400 Bad Request | CPF duplicado | Verificar se CPF já existe |
| 404 Not Found | Paciente não existe | Verificar ID ou CPF |
| 400 Bad Request | Data nascimento futura | Inserir data válida |
| 400 Bad Request | Hospital não existe | Criar hospital antes |
| 400 Bad Request | Status inválido | Usar enum correto |

---

## 🎓 Referências Rápidas

**Criar um ProtocoloME para este Paciente**:
1. Paciente deve ter status = "INTERNADO"
2. Alterar para status = "EM_PROTOCOLO_ME"
3. Criar ProtocoloME referenciando paciente.id
4. Sistema auto-populará 35 exames

**Próximos Passos**:
- ✅ Paciente Entity (COMPLETO)
- ⏳ [Auto-preenchimento de exames ao criar protocolo]
- ⏳ [Autenticação e Login]
- ⏳ [Notificações automáticas]

---

**Desenvolvido para**: Sistema de Transportadora de Pacientes  
**Versão**: 1.0  
**Data**: 2024  
**Status**: ✅ Produção
