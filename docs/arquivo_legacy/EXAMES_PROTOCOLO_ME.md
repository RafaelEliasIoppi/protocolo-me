# Sistema de Exames para Protocolos de Morte Encefálica

## 📋 Descrição

Modulo completo para gerenciamento de exames necessários nos protocolos de Morte Encefálica (ME), incluindo exames clínicos, complementares e laboratoriais.

## 🏥 Tipos de Exames Implementados

### 1️⃣ EXAMES CLÍNICOS (Testes Neurológicos)

Avaliação clínica através de testes neurológicos específicos:

#### Resposta ao Estímulo Doloroso
- **Categoria**: Clínico
- **Descrição**: Avalia resposta a estímulos dolorosos centrais
- **Objetivo**: Verificar ausência de resposta ao dor
- **Resultado Esperado**: Ausência de resposta (Negativo)

#### Reflexo Pupilar
- **Categoria**: Clínico
- **Descrição**: Teste de reatividade da pupila à luz
- **Objetivo**: Avaliar função de tronco cerebral
- **Resultado Esperado**: Pupilas fixas e dilatadas

#### Reflexo Corneal
- **Categoria**: Clínico
- **Descrição**: Toque suave na córnea
- **Objetivo**: Avaliar integridade do tronco cerebral
- **Resultado Esperado**: Ausência de reflexo

#### Reflexo Vestibulo-Ocular (Calórico)
- **Categoria**: Clínico
- **Descrição**: Teste calórico com água morna em conduto auditivo
- **Objetivo**: Avaliar função de tronco cerebral
- **Resultado Esperado**: Ausência de desvio ocular

#### Reflexo Nauseoso/Faríngeo
- **Categoria**: Clínico
- **Descrição**: Estimulação da parede faríngea
- **Objetivo**: Avaliar reflexos bulbares
- **Resultado Esperado**: Ausência de reflexo

#### Reflexo de Tosse (Traqueal)
- **Categoria**: Clínico
- **Descrição**: Estímulo do tubo traqueal
- **Objetivo**: Avaliar reflexos de tronco cerebral
- **Resultado Esperado**: Ausência de tosse

#### Teste de Apneia
- **Categoria**: Clínico
- **Descrição**: Desconexão do ventilador mecânico
- **Objetivo**: Verificar ausência de esforço respiratório espontâneo
- **Duração**: Mínimo 10 minutos
- **Critério**: Sem esforço ventilatório, PaCO2 > 60 mmHg

#### Postura Decerebrada
- **Categoria**: Clínico
- **Descrição**: Posicionamento rigid do corpo
- **Objetivo**: Avaliar reflexos primitivos
- **Característica**: Extensão dos membros

#### Postura Descerebrada
- **Categoria**: Clínico
- **Descrição**: Posicionamento alternado
- **Objetivo**: Avaliar reflexos arcaicos
- **Característica**: Flexão/extensão alternada

---

### 2️⃣ EXAMES COMPLEMENTARES (Imagem e Eletrofisiologia)

#### Angiografia Cerebral Digital
- **Categoria**: Complementar
- **Tipo**: Imagem
- **Descrição**: Angiografia por subtração digital
- **Objetivo**: Verificar ausência de fluxo sanguíneo cerebral
- **Indicação**: Gold standard para diagnóstico de ME
- **Tempo**: Viável a partir de 6 horas

#### Ressonância Magnética
- **Categoria**: Complementar
- **Tipo**: Imagem
- **Descrição**: Ressonância magnética de crânio
- **Objetivo**: Avaliar lesões cerebrais irreversíveis
- **Vantagem**: Detalhe de lesão cerebral

#### Tomografia de Crânio
- **Categoria**: Complementar
- **Tipo**: Imagem
- **Descrição**: Tomografia computadorizada
- **Objetivo**: Verificar lesão cerebral
- **Rapidez**: Realização rápida

#### Tomografia Angio (CTA)
- **Categoria**: Complementar
- **Tipo**: Imagem
- **Descrição**: Tomografia com contraste
- **Objetivo**: Avaliar perfusão e fluxo cerebral
- **Tempo**: Alternativa quando angiografia não disponível

#### Ultrassom Doppler Transcraniano
- **Categoria**: Complementar
- **Tipo**: Imagem
- **Descrição**: Doppler de artérias cerebrais
- **Objetivo**: Avaliar paradoxo sistólico-diastólico
- **Padrão ME**: Picos sistólicos seguidos de refluxo diastólico

#### Eletroencefalograma (EEG)
- **Categoria**: Complementar
- **Tipo**: Eletrofisiologia
- **Descrição**: Registro da atividade elétrica cerebral
- **Objetivo**: Verificar traço plano (isoelétrico)
- **Duração Mínima**: 30 minutos
- **Critério ME**: Atividade elétrica < 2 µV

#### Mapeamento Cerebral
- **Categoria**: Complementar
- **Tipo**: Eletrofisiologia
- **Descrição**: Mapeamento topográfico da atividade elétrica
- **Objetivo**: Confirmação adicional de inatividade cerebral

#### Ressonância Magnética Funcional
- **Categoria**: Complementar
- **Tipo**: Eletrofisiologia
- **Descrição**: fMRI para avaliar ativação cerebral
- **Objetivo**: Verificar ausência de ativação cerebral

---

### 3️⃣ EXAMES LABORATORIAIS

#### Gasometria Arterial
- **Categoria**: Laboratorial
- **Tipo**: Sangue
- **Objetivo**: Avaliar oxigenação e ventilação
- **Parâmetros**: pH, PaCO2, PaO2, HCO3, BE

#### Hemograma Completo
- **Categoria**: Laboratorial
- **Tipo**: Sangue
- **Objetivo**: Avaliar contagem de células
- **Parâmetros**: Hemoglobina, Hematócrito, Plaquetas, Leucócitos

#### Eletrólitos
- **Categoria**: Laboratorial
- **Tipo**: Sangue
- **Objetivo**: Avaliar equilíbrio eletrolítico
- **Parâmetros**: Sódio, Potássio, Cloro

#### Glicemia
- **Categoria**: Laboratorial
- **Tipo**: Sangue
- **Objetivo**: Avaliar metabolismo de glicose
- **Parâmetro**: Glicose sérica

#### Cálcio Iônico
- **Categoria**: Laboratorial
- **Tipo**: Sangue
- **Objetivo**: Avaliar metabolismo de cálcio
- **Parâmetro**: Ca++ iônico

#### Função Hepática
- **Categoria**: Laboratorial
- **Tipo**: Sangue
- **Objetivo**: Avaliar função hepática
- **Parâmetros**: AST, ALT, Bilirrubina Total/Direta

#### Função Renal
- **Categoria**: Laboratorial
- **Tipo**: Sangue
- **Objetivo**: Avaliar função renal
- **Parâmetros**: Creatinina, Uréia, Clearance

#### Testes de Coagulação
- **Categoria**: Laboratorial
- **Tipo**: Sangue
- **Objetivo**: Avaliar sistema de coagulação
- **Parâmetros**: PT (INR), APTT, Tempo de Trombina

#### Proteínas Totais
- **Categoria**: Laboratorial
- **Tipo**: Sangue
- **Objetivo**: Avaliar proteína sérica
- **Parâmetros**: Proteína Total, Albumina, Globulina

#### Sorologia HIV
- **Categoria**: Laboratorial
- **Tipo**: Infeccioso
- **Objetivo**: Descartar infecção por HIV
- **Obrigatoriedade**: Sim (para doação)

#### Sorologia Hepatite B
- **Categoria**: Laboratorial
- **Tipo**: Infeccioso
- **Objetivo**: Descartar infecção por HBV
- **Obrigatoriedade**: Sim (para doação)

#### Sorologia Hepatite C
- **Categoria**: Laboratorial
- **Tipo**: Infeccioso
- **Objetivo**: Descartar infecção por HCV
- **Obrigatoriedade**: Sim (para doação)

#### Sorologia Sífilis (RPR/VDRL)
- **Categoria**: Laboratorial
- **Tipo**: Infeccioso
- **Objetivo**: Descartar sífilis
- **Obrigatoriedade**: Sim (para doação)

#### Hemocultura
- **Categoria**: Laboratorial
- **Tipo**: Infeccioso
- **Objetivo**: Descartar bacteremia/fungemia
- **Coleta**: Antes de antibióticos se possível

#### Tipagem Sanguínea
- **Categoria**: Laboratorial
- **Tipo**: Infeccioso
- **Objetivo**: Definir tipo sanguíneo para compatibilidade
- **Obrigatoriedade**: Sim (para transplante)

#### Sorologias Diversas
- **Categoria**: Laboratorial
- **Tipo**: Infeccioso
- **Objetivo**: Pesquisa de outras infecções
- **Exemplos**: HTLV, Toxoplasmose, CMV

#### Teste de Função Tireoidiana
- **Categoria**: Laboratorial
- **Tipo**: Metabólico
- **Objetivo**: Avaliar função tireoidiana
- **Parâmetros**: TSH, T3, T4

#### Lactato Sérico
- **Categoria**: Laboratorial
- **Tipo**: Metabólico
- **Objetivo**: Avaliar metabolismo celular
- **Indicação**: Avaliar perfusão e hipóxia tecidual

---

## 📊 Relação com ProtocoloME

```
ProtocoloME (1)
    ↓
    ├─ OneToMany ─→ ExameME (N)
    │
    └─ Relacionamento: protocoloME
```

Cada protocolo de ME pode ter múltiplos exames associados.

## 🔌 Endpoints da API

### Criar Exame
```
POST /api/exames-me
Content-Type: application/json

{
  "protocoloME": { "id": 1 },
  "tipoExame": "REFLEXO_PUPILAR",
  "categoria": "CLINICO",
  "descricao": "Teste de reflexo pupilar à luz",
  "resultado": "Pupilas fixas e dilatadas",
  "resultado_positivo": false,
  "responsavel": "Dr. Silva",
  "observacoes": "Sem anormalidades"
}
```

### Listar Exames de um Protocolo
```
GET /api/exames-me/protocolo/{protocoloId}
```

### Listar Exames Clínicos
```
GET /api/exames-me/protocolo/{protocoloId}/clinicos
```

### Listar Exames Complementares
```
GET /api/exames-me/protocolo/{protocoloId}/complementares
```

### Listar Exames Laboratoriais
```
GET /api/exames-me/protocolo/{protocoloId}/laboratoriais
```

### Registrar Resultado
```
POST /api/exames-me/{exameId}/resultado
Params:
  - resultado (string)
  - resultado_positivo (boolean)
  - responsavel (string)
```

### Obter Resumo
```
GET /api/exames-me/protocolo/{protocoloId}/resumo

Response:
{
  "totalExames": 25,
  "examesRealizados": 18,
  "exames_Clinicos": 9,
  "examesComplementares": 8,
  "examesLaboratoriais": 8
}
```

## 🎛️ Integração Frontend

### Importar Componente
```javascript
import ExameMEManager from './componentes/ExameMEManager';

// Usar como:
<ExameMEManager protocoloId={protocoloId} />
```

### Exemplo de Uso Completo
```javascript
import ProtocoloMEManager from './componentes/ProtocoloMEManager';
import ExameMEManager from './componentes/ExameMEManager';

function ProtocoloPage({ protocoloId }) {
  return (
    <div>
      <ProtocoloMEManager />
      <ExameMEManager protocoloId={protocoloId} />
    </div>
  );
}
```

## 📋 Checklist de Exames para ME (Brasil)

### Mínimo Obrigatório:
- [x] 2 Testes Clínicos 1
- [x] 2 Testes Clínicos 2
- [x] Teste de Apneia
- [x] Gasometria Arterial
- [x] Exame complementar (EEG ou Angiografia)

### Recomendados:
- [x] Hemograma
- [x] Eletrólitos
- [x] Função Hepática
- [x] Função Renal
- [x] Coagulação
- [x] Sorologias
- [x] Tipagem Sanguínea
- [x] Lactato

## 🔐 Validações

- Tipo de exame obrigatório
- Resultado pode ser registrado após criação
- Automaticamente cria data de realização ao registrar resultado
- Responsável é opcional mas recomendado

## 📊 Categorias de Exames

| Categoria | Quantidade | Descrição |
|-----------|-----------|-----------|
| Clínicos | 9 | Testes neurológicos |
| Complementares | 8 | Imagem e eletrofisiologia |
| Laboratoriais | 18 | Sangue, infecciosos, metabólicos |
| **TOTAL** | **35** | Tipos de exames disponíveis |

## 🔗 Relacionamentos

```
ProtocoloME
    ├─ exames [] → ExameME[]
    ├─ status
    ├─ dataNotificacao
    └─ ...

ExameME
    ├─ protocoloME → ProtocoloME
    ├─ categoria
    ├─ tipoExame
    ├─ resultado
    ├─ dataRealizacao
    └─ resultadoPositivo
```

## 💾 Armazenamento de Dados

Tabela: `exame_me`
- `id` (PK)
- `protocolo_me_id` (FK)
- `categoria` (ENUM)
- `tipo_exame` (ENUM)
- `descricao` (TEXT)
- `resultado` (TEXT)
- `resultado_positivo` (BOOLEAN)
- `data_realizacao` (TIMESTAMP)
- `responsavel` (VARCHAR)
- `observacoes` (TEXT)
- `data_criacao` (TIMESTAMP)
- `data_atualizacao` (TIMESTAMP)

## 📚 Referências

- **Código de Ética Médica**: Resolução CFM 1.480/1997
- **Protocolos de ME**: ABNT NBR 15.605:2007
- **RDC ANVISA**: Requisitos para morte encefálica

## 🎓 Novas Adições

Última atualização deste documento:
- Versão: 1.0
- Data: 2024
- Sistema: Central de Transplantes

---

**Desenvolvido para: Sistema de Transportadora de Pacientes**
