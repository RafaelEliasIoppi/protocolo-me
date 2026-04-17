# Sistema de Gerenciamento de Hospitais

## 📋 Descrição

Sistema completo para cadastro e gerenciamento de hospitais, com funcionalidade específica para a equipe médica alterar status dos hospitais em tempo real.

## 🚀 Funcionalidades

### Backend (Spring Boot)
- **Cadastro de Hospitais**: Criar novos hospitais com validação de CNPJ
- **Listagem**: Visualizar todos os hospitais
- **Filtros**: Filtrar por status, cidade, estado ou CNPJ
- **Edição**: Atualizar dados do hospital
- **Gerenciamento de Status**: Alterar status do hospital (ATIVO, INATIVO, MANUTENÇÃO, SUSPENSÃO)
- **Deleção**: Remover hospitais do sistema

### Frontend (React)
- **HospitalForm**: Componente para cadastro e edição de hospitais
- **HospitalStatus**: Interface para equipe médica alterar status

## 🔌 Endpoints da API

### Criar Hospital
```
POST /api/hospitais
Content-Type: application/json

{
  "nome": "Hospital Central",
  "cnpj": "12345678000190",
  "endereco": "Rua das Flores, 123",
  "cidade": "São Paulo",
  "estado": "SP",
  "telefone": "(11) 98765-4321",
  "email": "hospital@email.com",
  "responsavelMedico": "Dr. João Silva"
}
```

### Listar Todos os Hospitais
```
GET /api/hospitais
```

### Buscar por ID
```
GET /api/hospitais/{id}
```

### Buscar por CNPJ
```
GET /api/hospitais/cnpj/{cnpj}
```

### Listar por Status
```
GET /api/hospitais/status/{status}
Valores: ATIVO, INATIVO, MANUTENCAO, SUSPENSAO
```

### Listar por Cidade
```
GET /api/hospitais/cidade/{cidade}
```

### Listar por Estado
```
GET /api/hospitais/estado/{estado}
```

### Atualizar Hospital
```
PUT /api/hospitais/{id}
Content-Type: application/json
```

### Alterar Status (Equipe Médica)
```
PATCH /api/hospitais/{id}/status
Params: status=ATIVO|INATIVO|MANUTENCAO|SUSPENSAO
```

### Deletar Hospital
```
DELETE /api/hospitais/{id}
```

## 📁 Estrutura de Arquivos Backend

```
backend/src/main/java/back/backend/
├── model/
│   └── Hospital.java
├── repository/
│   └── HospitalRepository.java
├── service/
│   └── HospitalService.java
└── controller/
    └── HospitalController.java
```

## 📁 Estrutura de Arquivos Frontend

```
frontend/src/
├── componentes/
│   ├── HospitalForm.js
│   └── HospitalStatus.js
└── styles/
    ├── HospitalForm.css
    └── HospitalStatus.css
```

## 🎨 Estados dos Hospitais

| Status | Cor | Descrição |
|--------|-----|-----------|
| ATIVO | Verde | Hospital funcionando normalmente |
| INATIVO | Cinza | Hospital não está operacional |
| MANUTENÇÃO | Amarelo | Hospital em manutenção |
| SUSPENSÃO | Vermelho | Hospital suspenso |

## 🔐 Validações

### No Cadastro:
- CNPJ: Deve ter 14 dígitos (validado)
- Nome: Campo obrigatório
- Endereço: Campo obrigatório
- Cidade: Campo obrigatório
- Estado: Campo obrigatório
- CNPJ Único: Não pode registrar dois hospitais com mesmo CNPJ

## 💾 Banco de Dados

A tabela `hospital` é criada automaticamente pelo Hibernate com os campos:
- `id` (PK)
- `nome`
- `cnpj` (UNIQUE)
- `endereco`
- `cidade`
- `estado`
- `telefone`
- `email`
- `status` (ENUM)
- `data_criacao`
- `data_atualizacao`
- `responsavel_medico`

## 🎯 Como Usar

### Cadastrar um Hospital

1. Acesse a página de cadastro (HospitalForm)
2. Preencha os dados obrigatórios
3. Clique em "Cadastrar Hospital"
4. Sucesso!

### Alterar Status (Equipe Médica)

1. Acesse a página "Gerenciar Status dos Hospitais"
2. (Opcional) Filtre por status desejado
3. Localize o hospital na lista
4. Clique no novo status desejado
5. Status é atualizado em tempo real

## 🔄 Integração com Frontend

Os componentes utilizam Axios para comunicar com a API Backend:

```javascript
// Exemplo de requisição
axios.post('/api/hospitais', dadosHospital)
axios.patch(`/api/hospitais/${id}/status`, {}, { params: { status: 'ATIVO' } })
```

## 📝 Observações Importantes

1. O proxy do Frontend está configurado para `http://localhost:2500`
2. O Backend roda na porta 2500
3. O Frontend roda na porta 3000
4. Todos os campos de data são gerenciados automaticamente (criar e atualizar)

## 🚨 Tratamento de Erros

- CNPJ duplicado: Retorna erro 400
- Hospital não encontrado: Retorna erro 404
- Status inválido: Retorna erro 400

## ✅ Checklist de Implementação

- [x] Model Hospital criado
- [x] Repository criado
- [x] Service criado
- [x] Controller com todos endpoints
- [x] Componente HospitalForm (cadastro/edição)
- [x] Componente HospitalStatus (alteração de status)
- [x] Estilos CSS completos
- [x] Validações de entrada
- [x] Tratamento de erros
- [x] Documentação

---

**Desenvolvido para: Sistema de Transportadora de Pacientes**
