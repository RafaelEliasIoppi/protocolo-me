# Instruções de Integração - Central de Transplantes

## 🚀 Como Usar os Novos Componentes

### 1. Importar Componentes no App.js ou Routes.js

```javascript
// Componentes importados
import CentralTransplantesForm from './componentes/CentralTransplantesForm';
import ProtocoloMEManager from './componentes/ProtocoloMEManager';
```

### 2. Adicionar Rotas (em routes.js)

```javascript
import CentralTransplantesForm from './componentes/CentralTransplantesForm';
import ProtocoloMEManager from './componentes/ProtocoloMEManager';

const routes = [
  // ... rotas existentes

  // Novas rotas para Central de Transplantes
  { path: '/centrais-transplantes', component: CentralTransplantesForm },
  { path: '/protocolos-me', component: ProtocoloMEManager },

  // Se usar roteador
  // <Route path="/centrais-transplantes" element={<CentralTransplantesForm />} />
  // <Route path="/protocolos-me" element={<ProtocoloMEManager />} />
];
```

### 3. Adicionar Links no Menu de Navegação

```javascript
<nav>
  {/* Links existentes */}
  
  {/* Novos links */}
  <li><a href="/centrais-transplantes">Central de Transplantes</a></li>
  <li><a href="/protocolos-me">Protocolos de ME</a></li>
</nav>
```

## 📊 Estrutura de Dados esperada

### Para Paciente.java

Certifique-se de que existe uma entidade Paciente:

```java
@Entity
@Table(name = "paciente")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Paciente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nome;
    
    @Column(nullable = false)
    private String dataAdmissao;
    
    @Column(nullable = false)
    private String hospital;
    
    // ... outros campos
}
```

### Para Hospital.java

Se ainda não criou, use o modelo já desenvolvido em HOSPITAIS_README.md

## 🔧 Configurações Necessárias

### 1. Dependências (Maven - pom.xml)

Certifique-se de ter:
```xml
<!-- Lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>

<!-- JPA -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- Validation -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<!-- Web -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

### 2. application.properties

As configurações já estão no projeto:
```
spring.jpa.hibernate.ddl-auto=update
spring.datasource.url=jdbc:h2:file:./data/banco;AUTO_SERVER=TRUE;LOCK_TIMEOUT=10000
```

### 3. CORS no Backend

Os controllers já têm `@CrossOrigin(origins = "*")`, mas você pode restringir:
```java
@CrossOrigin(origins = "http://localhost:3000")
```

## 🧪 Testes da API

### Com cURL

```bash
# Criar Central de Transplantes
curl -X POST http://localhost:2500/api/centrais-transplantes \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Central SP",
    "cnpj": "12345678000190",
    "endereco": "Av. Paulista, 1000",
    "cidade": "São Paulo",
    "estado": "SP",
    "telefone": "(11) 3000-0000",
    "email": "central@hospital.com",
    "coordenador": "Dr. Silva"
  }'

# Listar Centrais
curl http://localhost:2500/api/centrais-transplantes

# Criar Protocolo de ME
curl -X POST http://localhost:2500/api/protocolos-me \
  -H "Content-Type: application/json" \
  -d '{
    "numeroProtocolo": "ME-2024-001",
    "hospitalOrigem": "Hospital A",
    "medicoResponsavel": "Dr. João",
    "diagnosticoBasico": "Traumatismo",
    "orgaosDisponiveis": "Coração, Pulmão"
  }'
```

### Com Postman

1. Crie uma collection "Central de Transplantes"
2. Configure base URL: `http://localhost:2500/api`
3. Adicione requisições para cada endpoint

## 📱 Fluxo de Uso

### Como Coordenador

1. **Criar Central** → CentralTransplantesForm
   - Preencher dados da central
   - Adicionar hospitais parceiros
   
2. **Gerenciar Protocolos** → ProtocoloMEManager
   - Criar novo protocolo quando notificado
   - Registrar testes clínicos
   - Confirmar morte cerebral
   - Notificar família
   - Preservar órgãos
   - Alterar status progressivamente

### Como Médico

1. **Visualizar Protocolos**
   - Filtrar por status
   - Ver detalhes do protocolo
   - Registrar observações

2. **Atualizar Informações**
   - Registrar testes clínicos
   - Confirmar morte cerebral

## ⚠️ Validações Importantes

- **CNPJ**: Deve ser único e com 14 dígitos
- **Número do Protocolo**: Deve ser único
- **Hospital Origem**: Campo obrigatório
- **Coordenador**: Campo obrigatório
- **Status**: Segue ordem progressiva (não pode pular etapas)

## 🐛 Troubleshooting

### Erro 404 - Central/Protocolo não encontrado
- Verifique o ID na URL
- Confirme que o recurso foi criado

### Erro 400 - Requisição inválida
- Verifique validade do CNPJ
- Confirme unicidade de números de protocolo
- Verifique formato JSON

### Erro 500 - Erro interno do servidor
- Verifique logs do backend
- Confirme conexão com banco de dados
- Verifique se as entidades foram criadas

## 📞 Support

Se algo não funcionar:
1. Verifique se o backend está rodando (`make backend`)
2. Verifique se o frontend está rodando (`make frontend`)
3. Confira os logs do terminal
4. Valide a estrutura das requisições

## 🔗 Links Úteis

- [Documentação Completa](./CENTRAL_TRANSPLANTES_README.md)
- [Documentação de Hospitais](./HOSPITAIS_README.md)
- [Como Rodar](./README_START.md)
