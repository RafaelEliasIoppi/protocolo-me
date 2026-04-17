# 📋 Teste End-to-End - Sistema ME com Anexos

## ✅ Verificação Rápida de Status

Toda a implementação foi concluída com sucesso:
- ✅ CSS para EntrevistaFamiliarManager
- ✅ Integração de GerenciadorAnexos em ExameMEManager
- ✅ Modal de protocolo em CentralDashboardPage
- ✅ Estilos do modal
- ✅ Backend compila sem erros

## 🚀 Como Testar End-to-End

### 1. Iniciar Backend

```bash
cd /workspaces/protocolo-me/backend
./mvnw clean spring-boot:run
```

Aguarde até ver: `Transportadora Application started on port 2500`

### 2. Iniciar Frontend (novo terminal)

```bash
cd /workspaces/protocolo-me/frontend
npm start
```

Aguarde até ver: `webpack compiled successfully`

Acesse: http://localhost:3000

### 3. Login

- **Email**: qualquer email válido
- **Senha**: qualquer senha
- **Role**: Selecione `MEDICO` ou `MEDICO/CENTRAL_TRANSPLANTES`

### 4. Fluxo de Teste Completo

#### Passo A: Criar Protocolo ME
1. Vá para **Pacientes**
2. Crie um novo paciente (ou use existente)
3. Preencha dados: Nome, CPF, Hospital, Cidade
4. Clique em **Criar Protocolo ME** (ou similar)
5. ✅ Protocolo criado com status `NOTIFICADO`

#### Passo B: Adicionar Exames + Anexos
1. Vá para **Protocolo** do paciente ou use **Gerenciador de Exames**
2. Adicione exame: **"Teste Clínico 1"** (selecione tipo: Resposta ao Estímulo Doloroso)
3. 🟡 Status ainda é `NOTIFICADO` (faltam mais exames)
4. Note o card de resultado
5. Clique em **Anexar documento**:
   - Selecione PDF/imagem (arquivo pequeno)
   - Escreva descrição: "Teste Clínico 1"
   - Clique **Enviar**
6. ✅ Documento anexado - deve aparecer na lista abaixo
7. Registre resultado e desafie clicando **Download** para verificar o arquivo

#### Passo C: Completar Todos os Exames
1. Adicione **Teste Clínico 2** (tipo: Reflexo Pupilar)
2. Registre resultado
3. Adicione exame **Complementar** (ex: Angiografia Cerebral Digital)
4. Registre resultado
5. 🟢 Status deve agora ser **MORTE_CEREBRAL_CONFIRMADA**
6. Dashboard central atualiza em tempo real (5s max)

#### Passo D: Abrir Modal de Detalhes
1. Vá para a **Dashboard Central de Monitoramento ME**
2. Procure pelo paciente na tabela
3. **Clique na linha** do paciente
4. ✅ Modal abre com os detalhes do protocolo
5. Visualiza: Status card, testes realizados, seção de entrevista

#### Passo E: Realizar Entrevista Familiar
1. No modal que abriu:
   1. Clique **"Marcar para Entrevista"** (botão verde)
   2. ✅ Status muda para `ENTREVISTA_FAMILIAR`
   3. Formulário de entrevista aparece

2. Preencha o formulário:
   - ✅ Marque: "Família foi notificada"
   - ✅ Marque: "Família autorizou a doação" (ou deixe desmarcado para recusa)
   - 📝 Adicione observações (opcional)

3. Role para baixo e **Anexe documento da entrevista**:
   - Selecione arquivo (PDF/imagem)
   - Descrição: "Termo de consentimento"
   - Clique **Enviar**
   - ✅ Documento aparece na lista

4. Clique **"Salvar Resultado"**
   - ✅ Status muda para `DOACAO_AUTORIZADA` (ou `FAMILIA_RECUSOU`)
   - ✅ Card de resultado aparece com a decisão

#### Passo F: Validar na Dashboard Central
1. Feche o modal
2. Volte para o topo da dashboard
3. Verifique **Resumo Executivo**:
   - Contador de "Autorizados" deve aumentar
   - Contador de "Entrevista" muda para "Autorizados"
4. Tabela atualiza - paciente agora com status `DOACAO_AUTORIZADA`

## 🧪 Testes Específicos de Anexos

### T1: Upload de Arquivo Inválido
**Esperado**: Erro de validação
```
1. Tente anexar arquivo .exe ou .bat
2. Deve mostrar: "Tipo de arquivo não permitido"
3. Frontend valida ANTES de enviar
```

### T2: Upload de Arquivo Grande
**Esperado**: Erro de tamanho
```
1. Tente anexar arquivo > 20MB
2. Deve mostrar: "Arquivo ultrapassa limite de 20MB"
3. Botão enviar fica desabilitado
```

### T3: Download de Arquivo
**Esperado**: Arquivo baixa com nome correto
```
1. Anexe um PDF com nome: "resultado_exame.pdf"
2. Clique Download na lista de anexos
3. Arquivo deve baixar com nome original
```

### T4: Deletar Anexo
**Esperado**: Documento removido da lista
```
1. Clique botão 🗑️ (delete) na lista
2. Pedir confirmação (opcional)
3. Documento desaparece da lista
4. Arquivo removido do servidor
```

## 📊 Validações de Status

| Ação | Trigger | Novo Status |
|------|---------|------------|
| Criar protocolo | - | `NOTIFICADO` |
| Adicionar Teste Clínico 1 | Criar exame | Permanece `NOTIFICADO` |
| Adicionar Teste Clínico 2 | Criar exame | Permanece `NOTIFICADO` |
| Adicionar Exame Complementar | Criar exame | `EM_PROCESSO` (se tem >= 1 clinico e >= 1 complementar) |
| Todos exames completos | Registrar resultado | `MORTE_CEREBRAL_CONFIRMADA` |
| Marcar para entrevista | POST /marcar-entrevista | `ENTREVISTA_FAMILIAR` |
| Registrar autorização | POST /resultado-entrevista (true) | `DOACAO_AUTORIZADA` |
| Registrar recusa | POST /resultado-entrevista (false) | `FAMILIA_RECUSOU` |

## 🔍 Critérios de Sucesso

✅ **Todos os testes devem passar para considerar a implementação completa:**

- [ ] Backend compila sem erros
- [ ] Frontend inicia sem console errors
- [ ] Protocolo ME criado com status correto
- [ ] Exames podem ser adicionados
- [ ] Documentos podem ser anexados a exames
- [ ] Status auto-atualiza quando exames completados
- [ ] Dashboard central mostra pacientes em tempo real
- [ ] Modal abre ao clicar na linha do paciente
- [ ] Entrevista familiar pode ser marcada
- [ ] Resultado pode ser registrado
- [ ] Documentos podem ser anexados à entrevista
- [ ] Status final (DOACAO_AUTORIZADA/FAMILIA_RECUSOU) exibido
- [ ] Download de documentos funciona
- [ ] Exclusão de documentos funciona
- [ ] Validação de arquivo funciona (tipo, tamanho)

## 🐛 Troubleshooting

### Backend não compila
```
Solução: ./mvnw clean compile -X (ver logs detalhados)
```

### Frontend não inicia
```
Solução 1: rm -rf node_modules package-lock.json && npm install
Solução 2: npm start -- --reset-cache
```

### Modal não abre ao clicar
```
Solução: Verificar console (F12) para erros de JavaScript
Comum: protocoloMEId undefined - verificar se protocolo.id existe em JSON
```

### Arquivo não anexa
```
Solução 1: Verificar se arquivo tem < 20MB
Solução 2: Verificar tipo de arquivo (ver lista de extensões aceitas)
Solução 3: Verificar console do backend para erros de IO
```

### Dashboard não atualiza
```
Solução: Esperar 5 segundos (intervalo de refresh)
Ou clicar botão "🔄 Atualizar" manualmente
```

## 📱 Responsividade

Testar resize de janela em:
- ✅ Desktop (> 1024px)
- ✅ Tablet (768px - 1024px)
- ✅ Mobile (< 768px)

Todos os componentes devem se adaptar automaticamente.

## 🚀 Próximas Melhorias (Futuro)

1. Permitir upload de múltiplos arquivos simultâneos
2. Preview de PDFs antes de download
3. Zip de todos os documentos para download em massa
4. Sistema de comentários em anexos
5. Histórico de versões de documento
6. Permissões por role (quem pode deletar, etc)
7. Integração com scanner (TWAIN/escanear direto)

---

**Data**: 2024-12-19
**Sistema**: Protocolo ME com Anexação de Documentos
**Status**: ✅ Pronto para Testes
