**Fluxo: `LoginPage` — Dependências e mapeamento completo**

Resumo: este documento descreve o fluxo completo da página `LoginPage` no frontend e todos os arquivos/serviços/backend que ela invoca direta ou indiretamente. Use-o como referência para testes end-to-end, auditoria de contratos e revisão do fluxo entre front e back.

**Página (Frontend)**
- **Componente:** [frontend/src/componentes/LoginPage.js](frontend/src/componentes/LoginPage.js#L1-L200) — formulário de login, validação de email/senha e chamada para `autenticarService.login`.
- **Estilos:** [frontend/src/styles/LoginPage.css](frontend/src/styles/LoginPage.css)

**Javascript/Utils do Frontend usados pela página**
- **Serviço de autenticação:** [frontend/src/services/autenticarService.js](frontend/src/services/autenticarService.js#L1-L240)
  - Método principal chamado: `login(email, senha)` → faz `api.post("/api/usuarios/login", { email, senha })` e salva sessão no `localStorage`.
  - Outros métodos relevantes (usados por outras páginas): `listarUsuarios`, `registrarAdmin`, `atualizarUsuario`, `redefinirSenha`, `obterUsuarioAtual`, `obterToken`, `isAutenticado`, `isAdmin`, etc.
- **Cliente HTTP com interceptors:** [frontend/src/services/clienteHttpService.js](frontend/src/services/clienteHttpService.js#L1-L200)
  - Adiciona `Authorization: Bearer <token>` aos requests quando existe token.
  - Intercepta respostas 401 e redireciona para `/login` quando necessário.
- **Instância axios base:** [frontend/src/api/clienteHttp.js](frontend/src/api/clienteHttp.js#L1-L50)
  - `baseURL` está vazio no client; o `package.json` do frontend define `proxy: "http://localhost:2500"` para desenvolvimento.
- **Gerenciador de erros (util):** [frontend/src/utils/apiError.js](frontend/src/utils/apiError.js#L1-L40)
  - `getApiErrorMessage(error, fallbackMessage)` usado para extrair mensagens amigáveis do erro HTTP.

**Outras páginas/frontend que usam `autenticarService` (contexto)**
- [frontend/src/componentes/UsuariosAdminPage.js](frontend/src/componentes/UsuariosAdminPage.js#L1-L420) — usa `autenticarService.listarUsuarios`, `registrarAdmin`, `atualizarUsuario`, `redefinirSenha`.

**Backend — endpoints acionados pela página**
- `POST /api/usuarios/login` → controlador:
  - [backend/src/main/java/back/backend/controller/UsuarioController.java](backend/src/main/java/back/backend/controller/UsuarioController.java#L1-L120)
    - Método: `login(@Valid @RequestBody LoginRequestDTO request)` → chama `usuarioService.autenticar(...)`, gera token com `JwtUtil` e retorna `AuthResponseDTO`.

**Backend — Service / Regras de negócio**
- [backend/src/main/java/back/backend/service/UsuarioService.java](backend/src/main/java/back/backend/service/UsuarioService.java#L1-L320)
  - Método crítico: `autenticar(String email, String senha)`
    - Normaliza email, busca `Usuario` via `UsuarioRepository`, valida `ativo` e compara senha via `PasswordEncoder`.
    - Retorna `UsuarioDTO` (via `UsuarioMapper`).

**Backend — DTOs e Mappers envolvidos**
- [backend/src/main/java/back/backend/dto/LoginRequestDTO.java](backend/src/main/java/back/backend/dto/LoginRequestDTO.java#L1-L80)
  - Estrutura enviada pelo frontend: `{ email, senha }` com validações `@NotBlank` e `@Email`.
- [backend/src/main/java/back/backend/dto/AuthResponseDTO.java](backend/src/main/java/back/backend/dto/AuthResponseDTO.java#L1-L120)
  - Estrutura retornada: `{ token, tokenExpiraEm, usuario }`.
- [backend/src/main/java/back/backend/dto/UsuarioDTO.java](backend/src/main/java/back/backend/dto/UsuarioDTO.java#L1-L200)
  - Representação pública do usuário retornada após login.
- [backend/src/main/java/back/backend/mapper/UsuarioMapper.java](backend/src/main/java/back/backend/mapper/UsuarioMapper.java#L1-L80)
  - MapStruct mapper que converte `Usuario` -> `UsuarioDTO`.
- [backend/src/main/java/back/backend/mapper/UsuarioRequestMapper.java](backend/src/main/java/back/backend/mapper/UsuarioRequestMapper.java#L1-L140)
  - MapStruct mapper que converte `UsuarioRequestDTO` -> `Usuario` (usado em criação/atualização de usuários).

**Backend — Segurança / Token**
- [backend/src/main/java/back/backend/security/JwtUtil.java](backend/src/main/java/back/backend/security/JwtUtil.java#L1-L240)
  - Gera token JWT (`gerarToken`) e fornece utilitários de extração/validação (`extractExpiration`, `isTokenValid`, `extractRole`).
- [backend/src/main/java/back/backend/security/SecurityConfig.java](backend/src/main/java/back/backend/security/SecurityConfig.java#L1-L140)
  - Configura permissões: `POST /api/usuarios/login` é público; outras rotas de `/api/usuarios/**` exigem roles apropriadas.

**Repositório / persistência**
- [backend/src/main/java/back/backend/repository/UsuarioRepository.java](backend/src/main/java/back/backend/repository/UsuarioRepository.java#L1-L200) — métodos como `findByEmail`, `countByRole`, `existsById` etc. usados por `UsuarioService`.

**Testes relacionados (pontos de verificação)**
- Frontend: [frontend/src/services/autenticarService.test.js](frontend/src/services/autenticarService.test.js#L1-L120) — testes unitários do serviço de autenticação.
- Backend: [backend/src/test/java/back/backend/controller/UsuarioControllerIntegrationTest.java](backend/src/test/java/back/backend/controller/UsuarioControllerIntegrationTest.java#L1-L160) — testes de integração do controlador de usuários.

**Diagrama de chamada (linear)**
1. Usuário preenche `LoginPage` e submete.
2. `LoginPage` chama `autenticarService.login(email, senha)`.
3. `autenticarService.login` chama `api.post('/api/usuarios/login', payload)` usando cliente axios configurado em `clienteHttpService`.
4. Backend recebe `POST /api/usuarios/login` → `UsuarioController.login(LoginRequestDTO)`.
5. `UsuarioController` chama `UsuarioService.autenticar(email, senha)`.
6. `UsuarioService` busca `Usuario` via `UsuarioRepository.findByEmail`, valida senha, retorna `UsuarioDTO` via `UsuarioMapper`.
7. `UsuarioController` gera token via `JwtUtil.gerarToken(...)` e retorna `AuthResponseDTO(token, expiraEm, usuarioDTO)`.
8. Frontend salva `token` e `usuario` no `localStorage` e redireciona/atualiza UI.

**Pontos importantes / observações**
- O `baseURL` do cliente HTTP está vazio — durante desenvolvimento o `package.json` do frontend usa `proxy: "http://localhost:2500"` (roteia requests para backend). Em produção, ajustar `baseURL` adequadamente.
- `clienteHttpService` redireciona para `/login` em respostas 401 não provenientes de `/api/usuarios/login`.
- `autenticarService` centraliza a persistência da sessão no `localStorage` (token + usuario).

**Sugestões de testes end-to-end (smoke / fluxo mínimo)**
1. Subir backend (porta 2500) e frontend (porta 3000) com `mvn spring-boot:run` e `npm start` (ou rodar build e servir). Certifique-se das variáveis `jwt.secret` e DB de testes configuradas.
2. Teste de sucesso: criar usuário via `POST /api/usuarios` (ou usar `registrarAdmin` bootstrap), acessar a UI `/login`, submeter credenciais válidas e verificar `localStorage` para `token` e `usuario` e redirecionamento.
3. Teste de erro: credenciais inválidas → verificar mensagem de erro apresentada (frontend usa `getApiErrorMessage`).
4. Teste de sessão expirada: forçar 401 em uma rota protegida e validar redirecionamento ao `/login`.

**Próximos passos (opções)**
- Gerar testes e2e (Cypress) cobrindo: criação de usuário (API), login via UI e validação de token no `localStorage`.
- Ou executar `mvn test` no backend e compartilhar resultados.

Se quiser, eu:
- Gero um teste e2e (Cypress) cobrindo: criação de usuário (API), login via UI e validação de token no `localStorage`.
- Ou executo `mvn test` no backend e compartilho os resultados.

---
Documento gerado em 2026-05-06.
