# Integração Completa: Protocolos ME + Exames

## 📋 Como Usar Juntos

### 1. Dashboard Completo de Protocolo

```javascript
import React, { useState } from 'react';
import ProtocoloMEManager from './componentes/ProtocoloMEManager';
import ExameMEManager from './componentes/ExameMEManager';

function ProtocoloCompleto({ protocoloId }) {
  const [protocolo, setProtocolo] = useState(null);

  return (
    <div className="protocolo-completo">
      <div className="container-protocolo">
        <ProtocoloMEManager />
      </div>
      
      <div className="container-exames">
        <ExameMEManager protocoloId={protocoloId} />
      </div>
    </div>
  );
}

export default ProtocoloCompleto;
```

## 📊 Fluxo de Trabalho Recomendado

### 1º - Notificação de Possível Doador
```
1. Criar Protocolo de ME
   - Número do protocolo
   - Hospital origem
   - Paciente ID
   - Status: NOTIFICADO
```

### 2º - Realizar Testes Clínicos 1
```
Exames Clínicos:
- Resposta ao Estímulo Doloroso ✓
- Reflexo Pupilar ✓
- Reflexo Corneal ✓
- Reflexo Vestibulo-Ocular ✓
- Reflexo Nauseoso ✓
- Reflexo de Tosse ✓

Status do Protocolo: EM_PROCESSO
```

### 3º - Realizar Testes Clínicos 2 (6-24 horas depois)
```
Repetir testes clínicos 1

+ 
Teste de Apneia ✓

Status do Protocolo: EM_PROCESSO
```

### 4º - Exame Complementar (Imagem ou EEG)
```
Exames Complementares:
- Angiografia Cerebral OU
- EEG (traço plano)

Status do Protocolo: MORTE_CEREBRAL_CONFIRMADA
```

### 5º - Exames Laboratoriais de Suporte
```
Exames Laboratoriais:
- Gasometria Arterial ✓
- Hemograma ✓
- Eletrólitos ✓
- Função Hepática ✓
- Função Renal ✓
- Coagulação ✓
- Sorologias (HIV, HBV, HCV, RPR) ✓
```

### 6º - Notificação da Família
```
Registrar no protocolo:
- Familia Notificada ✓

Status do Protocolo: FAMILIA_INFORMADA
```

### 7º - Preservação de Órgãos
```
Registrar:
- Órgãos Disponíveis
- Preservação de Órgãos ✓

Status do Protocolo: ORGAOS_PRESERVADOS
```

### 8º - Final
```
Status: APTO_TRANSPLANTE ou CONTRAINDICADO
```

## 🎯 Endpoints Práticos

### Scenario Completo

```bash
# 1. Criar Protocolo
curl -X POST http://localhost:2500/api/protocolos-me \
  -H "Content-Type: application/json" \
  -d '{
    "numeroProtocolo": "ME-2024-001",
    "hospitalOrigem": "Hospital A",
    "centralTransplantesId": 1
  }'
# Response: { "id": 1, ... }

# 2. Adicionar Exame Clínico
curl -X POST http://localhost:2500/api/exames-me \
  -H "Content-Type: application/json" \
  -d '{
    "protocoloME": { "id": 1 },
    "tipoExame": "REFLEXO_PUPILAR",
    "categoria": "CLINICO",
    "descricao": "Teste pupilar à luz",
    "responsavel": "Dr. Silva"
  }'

# 3. Registrar Resultado do Exame
curl -X POST "http://localhost:2500/api/exames-me/1/resultado?resultado=Pupilas+fixas&resultado_positivo=false&responsavel=Dr.+Silva"

# 4. Obter Resumo de Exames
curl http://localhost:2500/api/exames-me/protocolo/1/resumo
# Response: {
#   "totalExames": 35,
#   "examesRealizados": 5,
#   "exames_Clinicos": 9,
#   "examesComplementares": 8,
#   "examesLaboratoriais": 8
# }

# 5. Listar Apenas Exames Clínicos
curl http://localhost:2500/api/exames-me/protocolo/1/clinicos

# 6. Alterar Status do Protocolo
curl -X PATCH "http://localhost:2500/api/protocolos-me/1/status?status=MORTE_CEREBRAL_CONFIRMADA"
```

## 💻 Interface de Usuário

### Layout Recomendado

```
┌─────────────────────────────────────────────────────────────┐
│                    PROTOCOLO DE ME                          │
├─────────────────────────────────────────────────────────────┤
│ Número: ME-2024-001 | Hospital: Hospital A | Status: EM_PROC│
│ Paciente ID: 123    | Data: 2024-01-15 10:30              │
├─────────────────────────────────────────────────────────────┤
│ [Confirmar Morte] [Notificar Família] [Preservar Órgãos]    │
├─────────────────────────────────────────────────────────────┤
│                    GERENCIADOR DE EXAMES                    │
├─────────────────────────────────────────────────────────────┤
│  Total: 35 | Realizados: 5 | Clínicos: 5 | Comp: 0 | Lab: 0│
├─────────────────────────────────────────────────────────────┤
│ [+ Adicionar Exame]              [Filtrar ▼] [🔄 Atualizar]│
├─────────────────────────────────────────────────────────────┤
│                     Lista de Exames                         │
│                                                              │
│ ✓ Reflexo Pupilar (Clínico)        Realizado              │
│   Resultado: Pupilas fixas e dilatadas                      │
│   [📝 Editar] [🗑 Deletar]                                  │
│                                                              │
│ ⏱ Reflexo Corneal (Clínico)        Pendente               │
│   [📝 Registrar] [🗑 Deletar]                              │
│                                                              │
│ ... mais exames ...                                         │
│                                                              │
└─────────────────────────────────────────────────────────────┘
```

## 🔄 Fluxo de Dados

```
[ProtocoloMEManager]
    ↓
    Cria/Atualiza ProtocoloME
    ↓
[ExameMEManager]
    ↓
    Adiciona ExamesME relacionado ao ProtocoloME
    ↓
    Registra Resultados
    ↓
[Visualização de Resumo]
    ↓
    Atualiza Status do Protocolo
```

## 📱 Responsividades

Ambos os componentes são responsivos:
- Desktop: Grid de 2+ colunas
- Tablet: Grid de 1-2 colunas
- Mobile: 1 coluna

## 🔐 Permissões Recomendadas

Para usar os exames:
```
Role: CENTRAL_TRANSPLANTES
Permissões:
- gerenciar_protocolos_me
- registrar_doadores
- atualizar_protocolo_me
- registrar_exames_me
- visualizar_exames_me
```

## 📊 Dados de Exemplo

### Protocolo Completo com Exames

```json
{
  "id": 1,
  "numeroProtocolo": "ME-2024-001",
  "hospitalOrigem": "Hospital A",
  "status": "MORTE_CEREBRAL_CONFIRMADA",
  "exames": [
    {
      "id": 1,
      "tipoExame": "REFLEXO_PUPILAR",
      "categoria": "CLINICO",
      "resultado": "Pupilas fixas e dilatadas",
      "resultado_positivo": false,
      "dataRealizacao": "2024-01-15T10:30:00"
    },
    {
      "id": 2,
      "tipoExame": "TESTE_APNEIA",
      "categoria": "CLINICO",
      "resultado": "Sem esforço respiratório",
      "resultado_positivo": false,
      "dataRealizacao": "2024-01-15T11:00:00"
    },
    {
      "id": 3,
      "tipoExame": "ELETROENCEFALOGRAMA",
      "categoria": "COMPLEMENTAR",
      "resultado": "Traço plano",
      "resultado_positivo": true,
      "dataRealizacao": "2024-01-15T12:00:00"
    },
    {
      "id": 4,
      "tipoExame": "GASOMETRIA_ARTERIAL",
      "categoria": "LABORATORIAL",
      "resultado": "pH 7.35, PaO2 450, PaCO2 45",
      "dataRealizacao": "2024-01-15T10:45:00"
    }
  ]
}
```

## 🎓 Tutorial Passo a Passo

### 1. Paciente Notificado

1. Abrir ProtocoloMEManager
2. Clicar em "Novo Protocolo de ME"
3. Preencher Número do Protocolo
4. Preencher Hospital Origem
5. Selecionar Central de Transplantes
6. Clicar em "Criar Protocolo"

### 2. Adicionar Primeiro Exame Clínico

1. Na seção "Adicionar Novo Exame"
2. Selecionar "Reflexo Pupilar" em Exames Clínicos
3. Preencher responsável (opcional)
4. Clicar em "+ Adicionar Exame"

### 3. Registrar Resultado

1. Localizar exame na lista
2. Clicar em "📝 Registrar Resultado"
3. Preencher resultado ("Pupilas fixas...")
4. Selecionar "Negativo" pour resultado_positivo
5. Clicar em "Salvar Resultado"

### 4. Confirmar Morte Encefálica

(Após 2 testes clínicos e 1 exame complementar)
1. Voltar para ProtocoloMEManager
2. Clicar em "✓ Confirmar Morte Cerebral"
3. Protocolo muda para "MORTE_CEREBRAL_CONFIRMADA"

### 5. Completar Exames Laboratoriais

1. Adicionar múltiplos exames de uma vez ou incrementalmente
2. Registrar resultados conforme laboratório retorna

## 🚀 Próximos Passos

1. ✅ Sistema de Exames implementado
2. ⏳ Sistema de Notificações (email/SMS)
3. ⏳ Dashboard de Estatísticas
4. ⏳ Relatórios de ME
5. ⏳ Integração com LIS (Laboratory Information System)

---

**Última atualização**: 2024
**Versão**: 1.0
