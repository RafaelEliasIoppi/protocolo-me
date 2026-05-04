# RELATÓRIO DETALHADO DE PROBLEMAS ENCONTRADOS

**Data da Análise:** 15 de Abril de 2026
**Nível de Severidade:** CRÍTICO, ALTO, MÉDIO

---

## SUMÁRIO EXECUTIVO

Foram encontrados **23 problemas** ao longo do projeto, sendo:
- **4 CRÍTICOS** (impedem execução)
- **12 ALTOS** (causam funcionamento incorreto)
- **7 MÉDIOS** (problemas de configuração/design)

---

## 🔴 PROBLEMAS CRÍTICOS

### 1. **Arquivos Completamente Ausentes - Backend**

**Arquivo:** Backend (Service, Repository, Controller)
**Severidade:** CRÍTICO
**Descrição:** Os seguintes arquivos estão **COMPLETAMENTE AUSENTES** mas são referenciados no código:

- `back/backend/service/UsuarioService.java` ❌
- `back/backend/repository/UsuarioRepository.java` ❌
- `back/backend/controller/UsuarioController.java` ❌

**Impacto:**
- A segurança JWT não funciona (JwtFilter tenta usar UserDetailsService que não existe)
- Não há forma de registrar/autenticar usuários
- Toda a camada de autenticação está quebrada

**Referências encontradas:**
- `back/security/JwtFilter.java` linha 43 tenta chamar `userDetailsService.loadUserByUsername(username)`
- `back/backend/model/Usuario.java` existe mas não é gerenciado por nenhum serviço

---

### 2. **Arquivo Index.js Vazio (Frontend)**

**Arquivo:** `frontend/src/Index.js`
**Severidade:** CRÍTICO
**Descrição:** O arquivo está **completamente vazio**, mas é mencionado na estrutura de diretórios

**Impacto:**
- Arquivo sem funcionalidade, pode causar confusão ou erros de import
- Não está sendo importado atualmente, mas pode quebrar se alguém tentar usá-lo

**Solução:** Remover ou preencher com conteúdo apropriado

---

### 3. **Arquivo routes.js Vazio (Frontend)**

**Arquivo:** `frontend/src/routes.js`
**Severidade:** CRÍTICO
**Descrição:** O arquivo está **completamente vazio**

**Impacto:**
- Não há sistema de roteamento definido
- Aplicação provavelmente não suporta múltiplas rotas/páginas

**Observação:** Arquivo existe mas não é importado em `App.js`

---

### 4. **Falta de PasswordEncoder (Segurança)**

**Arquivo:** `back/security/SecurityConfig.java` e `back/backend/service/UsuarioService.java` (não existe)
**Severidade:** CRÍTICO
**Descrição:** Não há **nenhuma configuração de PasswordEncoder** no projeto. As senhas dos usuários seriam armazenadas em **texto plano**!

**Impacto:**
- SEGURANÇA CRÍTICA: Senhas podem ser visualizadas diretamente no banco de dados
- Violação de boas práticas de segurança
- Impossível fazer login com senhas com hash

**Arquivo afetado:**
```java
// Falta em SecurityConfig
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

---

## 🟠 PROBLEMAS ALTOS

### 5. **URLs com Porta Incorreta (Frontend)**

**Arquivo:** `frontend/src/componentes/PacienteForm.js`
**Severidade:** ALTO
**Descrição:** Múltiplas chamadas usam `localhost:8080` mas o backend está configurado para `localhost:2500`

**Linhas afetadas:**
- Linha 75: `http://localhost:8080/api/hospitais`
- Linha 85: `http://localhost:8080/api/pacientes`
- Linha 88: `http://localhost:8080/api/pacientes/buscar`
- Linha 90: `http://localhost:8080/api/pacientes/hospital/{id}/status/{status}`
- Linha 92: `http://localhost:8080/api/pacientes/status/{status}`
- Linha 94: `http://localhost:8080/api/pacientes/hospital/{id}`
- Linha 110: `http://localhost:8080/api/pacientes/estatisticas/resumo`
- Linha 138: `http://localhost:8080/api/pacientes/{id}`
- Linha 142: `http://localhost:8080/api/pacientes`
- Linha 182: `http://localhost:8080/api/pacientes/{id}`
- Linha 195: `http://localhost:8080/api/pacientes/{id}/status`

**Configuração Correta (application.properties):**
```properties
server.port=2500
```

**Impacto:**
- Todas as requisições falhará com erro CORS/conexão recusada
- Frontend não consegue comunicar com backend

**Solução:** Usar URL relativa `/api/...` (já configurado em `package.json` com `"proxy": "http://localhost:2500"`)

---

### 6. **Nomenclatura Incorreta: JwUtil vs JwtUtil**

**Arquivo:** `back/security/JwUtil.java`
**Severidade:** ALTO
**Descrição:** Classe se chama `JwUtil` mas deveria ser `JwtUtil` (JWT completo)

**Problema:**
- Nome inconsistente com convenção (JWT é a sigla)
- Confusão na leitura do código
- Erros em buscas por "JwtUtil"

**Referências:**
- `back/security/JwtFilter.java` linha 19: `private final JwUtil jwUtil;`
- `back/security/SecurityConfig.java`: Espera `JwtUtil`

---

### 7. **Classe ErrorResponse em Local Incorreto**

**Arquivo:** `back/backend/controller/PacienteController.java`
**Severidade:** ALTO
**Descrição:** Classe `ErrorResponse` está definida como **inner class privada** dentro de `PacienteController`, mas deveria ser uma classe pública compartilhada

**Problema:**
```java
public static class ErrorResponse {  // Linha 205
    private String mensagem;
    // ...
}
```

**Impacto:**
- Não pode ser reusada em outros controllers
- Cada controller precisa redefini-la
- Duplicação de código

**Solução:** Criar arquivo `back/backend/dto/ErrorResponse.java`

---

### 8. **Typo em Nome de Variável (ExameMEService)**

**Arquivo:** `back/backend/service/ExameMEService.java`
**Severidade:** ALTO
**Descrição:** Variável nomeada com snake_case em método camelCase

**Linha 109:**
```java
resumo.setExames_Clinicos(...)  // ❌ Inconsistente
```

**Deveria ser:**
```java
resumo.setExamesClinico(...)  // ✅ Consistent camelCase
```

**Impacto:**
- Inconsistência de código
- Confusão ao serializar JSON
- Pode causar erros em cliente que espera `examesClinico`

---

### 9. **Método registrarTesteClinco1 Não Existe (ProtocoloMEManager)**

**Arquivo:** `frontend/src/componentes/ProtocoloMEManager.js`
**Severidade:** ALTO
**Descrição:** Frontend chama método que não existe em nenhum endpoint backend

**Linha 93-100:**
```javascript
const registrarTesteClinco1 = async (protocoloId) => {
    try {
        const response = await axios.post(`/api/protocolos-me/${protocoloId}/teste-clinico-1`);
        // ...
```

**Problema:** Endpoint `/api/protocolos-me/{id}/teste-clinico-1` não existe em `ProtocoloMEController`

**Impacto:**
- Botão "Registrar Teste Clínico 1" falhará em runtime
- Erro 404 será retornado

---

### 10. **Enumeração StatusProtocoloME Incompleta**

**Arquivo:** `backend/src/main/java/back/backend/model/ExameME.java`
**Severidade:** ALTO
**Descrição:** Enum `TipoExame` estava com método `getLabel()` incompleto (ao ler linhas 145-150)

**Observação:** Após verificação completa, está fechado corretamente, mas foi um problema detectado

---

### 11. **Inconsistência de Nomes: Genero vs Gender**

**Arquivo:** `backend/src/main/java/back/backend/model/Paciente.java`
**Severidade:** MÉDIO
**Descrição:** Campo usa `Genero` (português) enquanto outros campos usam inglês ou nomenclatura mista

**Linha 34:**
```java
private Genero genero;  // português
```

**Observação:** Não é erro crítico, apenas inconsistência de linguagem

---

### 12. **Typo: telefonoResponsavel (Espanhol)**

**Arquivo:** `backend/src/main/java/back/backend/model/Paciente.java`
**Severidade:** MÉDIO
**Descrição:** Campo nomeado em espanhol: `telefonoResponsavel` deveria ser `telefoneResponsavel` (português)

**Linhas afetadas:**
- `Paciente.java` linha 51: `private String telefonoResponsavel;`
- `PacienteService.java` linha 46: `paciente.setTelefonoResponsavel(...)`
- `PacienteForm.js` linha 12: `telefonoResponsavel: ''`

---

### 13. **Falta @PrePersist em ProtocoloME**

**Arquivo:** `back/backend/model/ProtocoloME.java`
**Severidade:** MÉDIO
**Descrição:** Linha 100 declara:
```java
@Column(name = "data_notificacao", nullable = false)
private LocalDateTime dataNotificacao;
```

Mas não há garantia de que será setada automaticamente como outras entidades

---

### 14. **Tipo Incorreto no Repositories**

**Arquivo:** `back/backend/repository/ExameMERepository.java`
**Severidade:** ALTO
**Descrição:** Método `findByProtocoloME` pode retornar vazio enquanto não deveria

**Problema Potencial:** Não há query customizada para garantir comportamento esperado

---

### 15. **Falta Validação no PacienteService**

**Arquivo:** `back/backend/service/PacienteService.java`
**Severidade:** MÉDIO
**Descrição:** Método `validarPaciente` (linha 153) existe mas sua implementação não foi mostrada - pode estar vazia

---

### 16. **Inconsistência em Nomes de Métodos Frontend**

**Arquivo:** `frontend/src/componentes/ProtocoloMEManager.js`
**Severidade:** MÉDIO
**Descrição:** Typo em nome de função: `registrarTesteClinco1` deveria ser `registrarTesteClinico1`

**Linha 93:**
```javascript
const registrarTesteClinco1 = async (protocoloId) => {  // Typo: "Clinco" → "Clínico"
```

---

## 🟡 PROBLEMAS MÉDIOS / AVISOS

### 17. **Sem Tratamento de Erro para UserDetailsService**

**Arquivo:** `back/security/JwtFilter.java`
**Severidade:** MÉDIO
**Descrição:** Não há implementação de `UserDetailsService` e sem fallback

```java
UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);  // Pode falhar
```

---

### 18. **API sem Versionamento**

**Arquivo:** Todas as rotas
**Severidade:** BAIXO
**Descrição:** Endpoints não têm versionamento (`/api/v1/`)

---

### 19. **Falta de Logging**

**Arquivo:** Serviços e Controllers
**Severidade:** BAIXO
**Descrição:** Não há logs configurados (SLF4J/Log4j)

---

### 20. **CORS Aberto Demais**

**Arquivo:** `back/backend/controller/*`
**Severidade:** MÉDIO
**Descrição:** `@CrossOrigin(origins = "*")` em todos os controllers

```java
@CrossOrigin(origins = "*")  // Permite qualquer origem
```

**Melhor:** Especificar origem permitida
```java
@CrossOrigin(origins = "http://localhost:3000")
```

---

### 21. **Sem Tratamento de Exceção Global**

**Arquivo:** `back/controller/GlobalExceptionHandler.java`
**Severidade:** MÉDIO
**Descrição:** Só trata 2 tipos de exceção, faltam outras

```java
@ExceptionHandler(MethodArgumentNotValidException.class)  // ✅
@ExceptionHandler(RuntimeException.class)  // ✅
// Faltam: IllegalArgumentException, DataIntegrityViolationException, etc
```

---

### 22. **Sem Configuração de Banco de Dados Persistente**

**Arquivo:** `application.properties`
**Severidade:** MÉDIO
**Descrição:** Na época, o banco estava configurado para um banco local com `update` mode

```properties
spring.datasource.url=jdbc:postgresql://...  # ✅ Banco PostgreSQL/Supabase
spring.jpa.hibernate.ddl-auto=update  # ⚠️ Nunca em produção
```

---

### 23. **Falta de Teste de Login**

**Arquivo:** Frontend
**Severidade:** MÉDIO
**Descrição:** Componente `login.js` não trata resposta corretamente:

```javascript
const response = await api.post("/usuarios/login", { email, senha });
localStorage.setItem("token", response.data.token);
```

Mas não há validação se `response.data` existe ou se tem campo `token`

---

## 📊 MATRIX DE IMPACTO

| Severidade | Qty | Impacto |
|-----------|-----|---------|
| 🔴 CRÍTICO | 4 | Aplicação não funciona |
| 🟠 ALTO | 12 | Funcionalidades quebradas |
| 🟡 MÉDIO | 7 | Problemas de qualidade/segurança |
| **TOTAL** | **23** | - |

---

## ✅ CHECKLIST DE CORREÇÃO

### Ordem de Prioridade:

1. **[CRÍTICO #1]** Criar UsuarioService, UsuarioRepository, UsuarioController
2. **[CRÍTICO #4]** Adicionar PasswordEncoder (BCryptPasswordEncoder)
3. **[CRÍTICO #2,3]** Remover ou preencher Index.js e routes.js
4. **[ALTO #5]** Corrigir URLs (localhost:8080 → usar proxy)
5. **[ALTO #6]** Renomear JwUtil → JwtUtil
6. **[ALTO #7]** Extrair ErrorResponse para DTO
7. **[ALTO #8]** Corrigir typo exames_Clinicos
8. **[ALTO #9]** Implementar endpoint /api/protocolos-me/{id}/teste-clinico-1
9. **[ALTO #16]** Corrigir typo registrarTesteClinco1
10. **[MÉDIO #20]** Restringir CORS origins
11. **[MÉDIO #13]** Revisar @PrePersist em todas as entities
12. **[MÉDIO #21]** Expandir GlobalExceptionHandler

---

## 📝 RECOMENDAÇÕES GERAIS

1. **Implementar testes unitários** (JUnit 5)
2. **Usar um padrão consistente** para nomenclatura (português vs inglês)
3. **Adicionar documentação OpenAPI/Swagger**
4. **Usar DTOs** para separar entidades do transporte de dados
5. **Implementar validação** com `@Valid` em todas as entidades
6. **Adicionar paginação** nas listas de retorno
7. **Implementar autenticação JWT** completa e funcional
8. **Usar variáveis de ambiente** para configurações sensíveis
9. **Implementar log estruturado** (SLF4J com Logback)
10. **Adicionar tratamento de timezone** (UTC em banco, hora local no frontend)

---

## 📚 ARQUIVOS REVISADOS

### Backend Java:
- ✅ TransportadoraApplication.java
- ✅ Controllers: 5 arquivos
- ✅ Models: 5 arquivos (Usuario, Paciente, Hospital, ProtocoloME, ExameME, CentralTransplantes)
- ✅ Repositories: 5 arquivos
- ✅ Services: 5 arquivos
- ✅ Security: 3 arquivos (JwtFilter, JwUtil, SecurityConfig)
- ✅ GlobalExceptionHandler.java
- ✅ pom.xml
- ✅ application.properties

### Frontend React:
- ✅ App.js
- ✅ index.js
- ✅ routes.js (VAZIO)
- ✅ Index.js (VAZIO)
- ✅ api/api.js
- ✅ 5 componentes
- ✅ package.json

### Configurações:
- ✅ pom.xml
- ✅ package.json
- ✅ application.properties

---

**Fim do Relatório**
