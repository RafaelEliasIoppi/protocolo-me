# GUIA PRÁTICO DE CORREÇÃO - PASSO A PASSO

## ⚡ QUICK FIX (1 hora)

### 1. Corrigir URLs Frontend (5 minutos)
**Arquivo:** `frontend/src/componentes/PacienteForm.js`

**ANTES:**
```javascript
await axios.get('http://localhost:8080/api/hospitais');
await axios.post('http://localhost:8080/api/pacientes', dados);
```

**DEPOIS:** (Use URLs relativas)
```javascript
await axios.get('/api/hospitais');
await axios.post('/api/pacientes', dados);
```

**Linhas a substituir:** 75, 85, 88, 90, 92, 94, 110, 138, 142, 182, 195

**Comando:**
```bash
cd frontend/src/componentes
sed -i 's|http://localhost:8080||g' PacienteForm.js
```

---

### 2. Renomear JwUtil → JwtUtil (3 minutos)

**Arquivo 1:** `back/security/JwUtil.java`
```bash
cd backend/src/main/java/back/security
mv JwUtil.java JwtUtil.java
# Editar conteúdo: renomear classe de JwUtil para JwtUtil
```

**Arquivo 2:** `back/security/SecurityConfig.java` e `back/security/JwtFilter.java`
```bash
# Todas as importações e referências:
# Substituir: import back.security.JwUtil;
# Por:        import back.security.JwtUtil;

# Substituir: private JwUtil jwUtil;
# Por:        private JwtUtil jwUtil;
```

---

### 3. Limpar Arquivos Vazios (2 minutos)

**Opção A - Remover:**
```bash
rm frontend/src/Index.js
rm frontend/src/routes.js
```

**Opção B - Preencher (se necessário):**
```javascript
// frontend/src/routes.js
export const routes = [
  { path: '/', component: 'Dashboard' },
  { path: '/login', component: 'Login' },
];
```

---

## 🔧 CORREÇÕES INTERMEDIÁRIAS (4-6 horas)

### 4. Criar UsuarioService.java

**Arquivo:** `backend/src/main/java/back/backend/service/UsuarioService.java`

```java
package back.backend.service;

import back.backend.model.Usuario;
import back.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));
        
        return User.builder()
            .username(usuario.getEmail())
            .password(usuario.getSenha())
            .roles(usuario.getRole().name())
            .build();
    }

    public Usuario registrar(Usuario usuario) {
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new RuntimeException("Email já cadastrado");
        }
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuario.setDataCriacao(LocalDateTime.now());
        usuario.setDataAtualizacao(LocalDateTime.now());
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }
}
```

---

### 5. Criar UsuarioRepository.java

**Arquivo:** `backend/src/main/java/back/backend/repository/UsuarioRepository.java`

```java
package back.backend.repository;

import back.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
}
```

---

### 6. Criar UsuarioController.java

**Arquivo:** `backend/src/main/java/back/backend/controller/UsuarioController.java`

```java
package back.backend.controller;

import back.backend.model.Usuario;
import back.backend.service.UsuarioService;
import back.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:3000")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<Usuario> registrar(@RequestBody Usuario usuario) {
        try {
            Usuario novoUsuario = usuarioService.registrar(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> credenciais) {
        try {
            Usuario usuario = usuarioService.findByEmail(credenciais.get("email"))
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            if (!passwordEncoder.matches(credenciais.get("senha"), usuario.getSenha())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Gerar token JWT (precisamos do UserDetails)
            String token = jwtUtil.generateToken(
                org.springframework.security.core.userdetails.User.builder()
                    .username(usuario.getEmail())
                    .password(usuario.getSenha())
                    .roles(usuario.getRole().name())
                    .build()
            );

            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
```

---

### 7. Adicionar PasswordEncoder em SecurityConfig

**Arquivo:** `back/security/SecurityConfig.java` - Adicionar Bean:

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

**Import necessário:**
```java
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
```

---

### 8. Corrigir typo em ExameMEService

**Arquivo:** `back/backend/service/ExameMEService.java` - Linha 109:

**ANTES:**
```java
resumo.setExames_Clinicos(...)
```

**DEPOIS:**
```java
resumo.setExamesClinico(...)
```

**Também em ExameResumo classe:**
```java
// ANTES
private int exames_Clinicos;
public int getExames_Clinicos() { return exames_Clinicos; }
public void setExames_Clinicos(int exames_Clinicos) { this.exames_Clinicos = exames_Clinicos; }

// DEPOIS
private int examesClinico;
public int getExamesClinico() { return examesClinico; }
public void setExamesClinico(int examesClinico) { this.examesClinico = examesClinico; }
```

---

### 9. Corrigir typo no Frontend

**Arquivo:** `frontend/src/componentes/ProtocoloMEManager.js` - Linha 93:

**ANTES:**
```javascript
const registrarTesteClinco1 = async (protocoloId) => {
```

**DEPOIS:**
```javascript
const registrarTesteClinico1 = async (protocoloId) => {
```

**Também atualizar chamada na linha 280:**
```javascript
// ANTES
onClick={() => registrarTesteClinco1(protocolo.id)}

// DEPOIS
onClick={() => registrarTesteClinico1(protocolo.id)}
```

---

## 🏗️ CORREÇÕES AVANÇADAS (2-3 dias)

### 10. Criar DTO ErrorResponse

**Arquivo:** `backend/src/main/java/back/backend/dto/ErrorResponse.java`

```java
package back.backend.dto;

public class ErrorResponse {
    private String mensagem;
    private int codigo;
    private long timestamp;

    public ErrorResponse(String mensagem) {
        this.mensagem = mensagem;
        this.timestamp = System.currentTimeMillis();
    }

    public ErrorResponse(String mensagem, int codigo) {
        this.mensagem = mensagem;
        this.codigo = codigo;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters
    public String getMensagem() { return mensagem; }
    public int getCodigo() { return codigo; }
    public long getTimestamp() { return timestamp; }
}
```

**Depois remover inner class de todos os controllers e importar este DTO**

---

### 11. Expandir GlobalExceptionHandler

**Arquivo:** `back/controller/GlobalExceptionHandler.java`

```java
@ExceptionHandler(IllegalArgumentException.class)
public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
    return new ResponseEntity<>(
        new ErrorResponse("Argumento inválido: " + ex.getMessage()),
        HttpStatus.BAD_REQUEST
    );
}

@ExceptionHandler(DataIntegrityViolationException.class)
public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
    return new ResponseEntity<>(
        new ErrorResponse("Violação de integridade de dados"),
        HttpStatus.CONFLICT
    );
}
```

---

### 12. Implementar Endpoints Faltantes

**Arquivo:** `ProtocoloMEController.java` - Adicionar:

```java
@PostMapping("/{id}/teste-clinico-1")
public ResponseEntity<?> registrarTesteClinico1(@PathVariable Long id) {
    try {
        ProtocoloME protocolo = protocoloService.buscarPorId(id)
            .orElseThrow(() -> new RuntimeException("Protocolo não encontrado"));
        
        protocolo.setTesteClinico1Realizado(true);
        protocolo.setDataTesteClinico1(LocalDateTime.now());
        
        ProtocoloME atualizado = protocoloService.atualizarProtocolo(id, protocolo);
        return ResponseEntity.ok(atualizado);
    } catch (Exception e) {
        return ResponseEntity.badRequest().build();
    }
}
```

---

## ✅ VERIFICAÇÃO FINAL

Após todas as correções, execute:

```bash
# Backend
cd backend
mvn clean install
mvn spring-boot:run

# Frontend (em outro terminal)
cd frontend
npm install
npm start
```

**Verificar:**
- [ ] Backend inicia sem erros na porta 2500
- [ ] Frontend inicia na porta 3000
- [ ] Login funciona
- [ ] Pacientes podem ser criados
- [ ] Todas as listas carregam
- [ ] Sem erros de CORS

---

## 📝 CHECKLIST DE IMPLEMENTAÇÃO

- [ ] URLs frontend corrigidas
- [ ] JwUtil renomeado para JwtUtil
- [ ] UsuarioService criado
- [ ] UsuarioRepository criado
- [ ] UsuarioController criado
- [ ] PasswordEncoder configurado
- [ ] typo exames_Clinicos corrigido
- [ ] typo registrarTesteClinco1 corrigido
- [ ] ErrorResponse como DTO
- [ ] GlobalExceptionHandler expandido
- [ ] Endpoints faltantes implementados
- [ ] Testes manuais passando
- [ ] Sem erros no console

