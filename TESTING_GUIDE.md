# Guia de Testes - Protocolo ME

## Visão Geral

Este documento descreve a estratégia de testes para o projeto Protocolo ME, incluindo testes de unidade, integração e end-to-end.

## Estrutura de Testes

### Backend (Java/Spring Boot)

#### Testes de Serviço
- **Local**: `backend/src/test/java/back/backend/service/`
- **Arquivos**:
  - `PacienteServiceTest.java` - Testes de lógica de pacientes
  - `HospitalServiceTest.java` - Testes de lógica de hospitais
  - `ExameMEServiceTest.java` - Testes de exames
  - `ProtocoloMEServiceTest.java` - Testes de protocolos
  - `CentralTransplantesServiceTest.java` - Testes de central de transplantes
  - `UsuarioServiceTest.java` - Testes de autenticação e usuários

#### Testes de Controller
- **Local**: `backend/src/test/java/back/backend/controller/`
- **Arquivos**:
  - `PacienteControllerTest.java` - Testes de endpoints de pacientes
  - `HospitalControllerTest.java` - Testes de endpoints de hospitais
  - `ExameMEControllerTest.java` - Testes de endpoints de exames
  - `ProtocoloMEControllerTest.java` - Testes de endpoints de protocolos
  - `CentralTransplantesControllerTest.java` - Testes de endpoints de central

### Frontend (React/JavaScript)

#### Testes de Serviço
- **Local**: `frontend/src/services/`
- **Arquivos**:
  - `exameService.test.js` - Testes do serviço de exames
  - `protocoloService.test.js` - Testes do serviço de protocolos
  - `centralTransplantesService.test.js` - Testes do serviço de central

#### Testes de Componentes
- **Local**: `frontend/src/componentes/`
- **Arquivos**:
  - `login.test.js` - Testes do componente de login
  - `HospitalStatus.test.js` - Testes do status de hospitais
  - `HospitalForm.test.js` - Testes do formulário de hospital
  - `PacienteForm.test.js` - Testes do formulário de paciente
  - `ExameMEManager.test.js` - Testes do gerenciador de exames
  - `ProtocoloMEManager.test.js` - Testes do gerenciador de protocolos
  - `CentralTransplantesForm.test.js` - Testes do formulário de central

#### Testes de Integração
- **Local**: `frontend/src/`
- **Arquivo**:
  - `App.integration.test.js` - Testes de integração da aplicação completa

## Configuração de Testes

### Backend - Maven

```bash
cd backend

# Executar todos os testes
mvn test

# Executar testes de um módulo específico
mvn test -Dtest=PacienteServiceTest

# Executar com coverage
mvn test jacoco:report
```

### Frontend - Jest

```bash
cd frontend

# Instalar dependências
npm install

# Executar todos os testes
npm test

# Modo watch
npm test -- --watch

# Com coverage
npm test -- --coverage

# Testes específicos
npm test -- login.test.js
```

## Configuração do Jest

Arquivo de configuração: `frontend/jest.config.js`

**Características**:
- Ambiente de teste: jsdom
- Coverage mínimo: 70%
- Timeout: 10 segundos
- Suporte a CSS e imagens

## Boas Práticas

### Para Testes Backend

1. **Use MockitoAnnotations** para mocking
2. **SpringBootTest** para testes de integração
3. **MockMvc** para testes de controller
4. **Setup adequado** com @BeforeEach
5. **Assertions claras** com JUnit 5

### Para Testes Frontend

1. **Mock de APIs** com jest.mock()
2. **React Testing Library** para componentes
3. **fireEvent** para interações
4. **waitFor** para operações assíncronas
5. **Dados de teste realistas**

## Exemplos de Testes

### Backend - Teste de Serviço
```java
@Test
@DisplayName("Deve criar novo paciente")
public void testCriarPaciente() {
    Paciente pacienteCriado = pacienteService.criar(paciente);
    
    assertNotNull(pacienteCriado.getId());
    assertEquals("João Silva", pacienteCriado.getNome());
}
```

### Backend - Teste de Controller
```java
@Test
@DisplayName("Deve listar todos os pacientes")
public void testListarPacientes() throws Exception {
    pacienteRepository.save(paciente);

    mockMvc.perform(get("/api/pacientes"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
}
```

### Frontend - Teste de Serviço
```javascript
it('deve listar todos os pacientes', async () => {
    const pacientes = {
        data: [{ id: 1, nome: 'João' }]
    };
    apiClient.get.mockResolvedValue(pacientes);
    
    const result = await pacienteService.listar();
    
    expect(result).toEqual(pacientes.data);
});
```

### Frontend - Teste de Componente
```javascript
it('deve renderizar o formulário de login', () => {
    render(<login onLoginSuccess={jest.fn()} />);
    
    expect(screen.getByText(/login/i)).toBeInTheDocument();
});
```

## Coverage Esperado

- **Backend**: Mínimo 70% em todas as classes de serviço e controller
- **Frontend**: Mínimo 70% em componentes e serviços

## CI/CD Integration

Os testes devem ser executados automaticamente:
- No commit (pre-commit hooks)
- No push (GitHub Actions)
- Antes do merge (Pull Request checks)

## Troubleshooting

### Backend
- **Erro de conexão BD**: Verificar aplicação.properties
- **Falha em mocks**: Verificar inicialização com MockitoAnnotations
- **Timeout**: Aumentar timeout em SpringBootTest

### Frontend
- **Erro de módulo**: Verificar jest.config.js
- **Falha em async**: Usar waitFor do React Testing Library
- **Mock não funciona**: Verificar se jest.mock() é chamado antes do import

## Recursos

- [JUnit 5 Documentation](https://junit.org/junit5/)
- [Spring Boot Testing](https://spring.io/guides/gs/testing-web/)
- [Jest Documentation](https://jestjs.io/)
- [React Testing Library](https://testing-library.com/react)
