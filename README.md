# 🗣️ ForumHub API

API REST desenvolvida com **Spring Boot** para gerenciamento de tópicos de um fórum, com autenticação via **JWT** e persistência em **PostgreSQL**. Projeto desenvolvido como desafio da Alura.

---

## 📋 Índice

- [Sobre o Projeto](#sobre-o-projeto)
- [Tecnologias](#tecnologias)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Diagrama do Banco de Dados](#diagrama-do-banco-de-dados)
- [Pré-requisitos](#pré-requisitos)
- [Configuração e Instalação](#configuração-e-instalação)
- [Endpoints da API](#endpoints-da-api)
- [Testando no Postman](#testando-no-postman)
- [Regras de Negócio](#regras-de-negócio)
- [Migrations](#migrations)
- [Segurança JWT](#segurança-jwt)

---

## 📌 Sobre o Projeto

O **ForumHub** é uma API REST que replica o funcionamento de um fórum no nível do back-end. Os usuários podem criar tópicos com dúvidas sobre cursos, visualizar, atualizar e excluir esses tópicos. Toda a API é protegida por autenticação via token JWT.

### Funcionalidades

- ✅ Cadastro de novo tópico
- ✅ Listagem de todos os tópicos (com paginação e filtros)
- ✅ Detalhamento de um tópico específico
- ✅ Atualização de tópico
- ✅ Exclusão de tópico
- ✅ Autenticação com JWT
- ✅ Validação de dados e regras de negócio
- ✅ Migrations com Flyway

---

## 🛠️ Tecnologias

| Tecnologia | Versão | Uso |
|---|---|---|
| Java | 17+ | Linguagem principal |
| Spring Boot | 4.0.3 | Framework principal |
| Spring Security | — | Autenticação e autorização |
| Spring Data JPA | — | Acesso ao banco de dados |
| Hibernate | 7.2.4 | ORM |
| PostgreSQL | 18+ | Banco de dados |
| Flyway | — | Migrations do banco |
| Auth0 Java JWT | 4.5.1 | Geração e validação de tokens JWT |
| Lombok | — | Redução de boilerplate |
| Maven | — | Gerenciamento de dependências |
| pgAdmin 4 | — | Interface visual do PostgreSQL |
| Postman | — | Testes da API |

---

## 📁 Estrutura do Projeto

```
src/main/java/com/alura/forumhub/
│
├── ForumhubApplication.java
│
├── controller/
│   ├── TopicoController.java          ← GET, POST, PUT, DELETE /topicos
│   └── AutenticacaoController.java    ← POST /login
│
├── domain/
│   ├── topico/
│   │   ├── Topico.java
│   │   ├── TopicoRepository.java
│   │   ├── StatusTopico.java          ← Enum: ABERTO, RESPONDIDO, FECHADO, SOLUCIONADO
│   │   ├── DadosCadastroTopico.java
│   │   ├── DadosListagemTopico.java
│   │   ├── DadosDetalhamentoTopico.java
│   │   └── DadosAtualizacaoTopico.java
│   ├── curso/
│   │   ├── Curso.java
│   │   └── CursoRepository.java
│   ├── perfil/
│   │   └── Perfil.java
│   ├── usuario/
│   │   ├── Usuario.java               ← implements UserDetails
│   │   └── UsuarioRepository.java
│   └── resposta/
│       └── Resposta.java
│
├── dto/
│   ├── DadosAutenticacao.java
│   └── DadosTokenJWT.java
│
├── security/
│   ├── SecurityConfigurations.java    ← Cadeia de filtros JWT
│   ├── SecurityFilter.java            ← Valida Bearer token por requisição
│   ├── TokenService.java              ← Gera e valida JWT com HMAC256
│   └── AutenticacaoService.java       ← UserDetailsService
│
└── infra/
    └── exception/
        └── TratadorDeErros.java       ← Handler global: 400, 401, 403, 404

src/main/resources/
├── application.properties
└── db/migration/
    ├── V1__create-table-cursos.sql
    ├── V2__create-table-perfis.sql
    ├── V3__create-table-usuarios.sql
    ├── V4__create-table-topicos.sql
    └── V5__create-table-respostas.sql
```

---

## 🗄️ Diagrama do Banco de Dados

```
┌─────────────┐       ┌──────────────────────────────────────────┐
│   cursos    │       │                  topicos                 │
├─────────────┤       ├──────────────────────────────────────────┤
│ id (PK)     │◄──────│ id (PK)                                  │
│ nome        │       │ titulo          (UNIQUE com mensagem)    │
│ categoria   │       │ mensagem        (UNIQUE com titulo)      │
└─────────────┘       │ data_criacao                             │
                       │ status          ABERTO|RESPONDIDO|...   │
┌─────────────┐       │ autor_id (FK) ──────────────────────────►│
│  usuarios   │       │ curso_id  (FK) ─────────────────────────►│
├─────────────┤       └──────────────────────────────────────────┘
│ id (PK)     │
│ nome        │       ┌──────────────────────────────────────────┐
│ email       │       │                 respostas                │
│ senha       │       ├──────────────────────────────────────────┤
└──────┬──────┘       │ id (PK)                                  │
       │              │ mensagem                                 │
       │  ┌───────────│ topico_id (FK)                           │
       │  │           │ data_criacao                             │
       ▼  ▼           │ autor_id  (FK)                           │
┌──────────────────┐  │ solucao   (boolean)                      │
│ usuarios_perfis  │  └──────────────────────────────────────────┘
├──────────────────┤
│ usuario_id (FK)  │  ┌─────────────┐
│ perfil_id  (FK)  │  │   perfis    │
└──────────────────┘  ├─────────────┤
                       │ id (PK)     │
                       │ nome        │
                       └─────────────┘
```

---

## ✅ Pré-requisitos

- Java 17 ou superior instalado
- Maven instalado
- PostgreSQL instalado e rodando
- pgAdmin 4 (opcional, para visualizar o banco)
- Postman (para testar a API)

---

## ⚙️ Configuração e Instalação

### 1. Clone o repositório

```bash
git clone https://github.com/seu-usuario/forumhub.git
cd forumhub
```

### 2. Crie o banco de dados no PostgreSQL

Abra o pgAdmin 4 ou o terminal do PostgreSQL e execute:

```sql
CREATE DATABASE forumhub;
```

### 3. Configure o `application.properties`

```properties
spring.application.name=forumhub

# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/forumhub
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.username=postgres
spring.datasource.password=SUA_SENHA_AQUI

# JPA / Hibernate
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.defer-datasource-initialization=true

# Flyway
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true

# JWT
api.security.token.secret=${JWT_SECRET:forumhub-secret-key}
```

### 4. Dependências do `pom.xml`

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-core</artifactId>
    </dependency>
    <dependency>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-database-postgresql</artifactId>
    </dependency>
    <dependency>
        <groupId>com.auth0</groupId>
        <artifactId>java-jwt</artifactId>
        <version>4.5.1</version>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <scope>runtime</scope>
        <optional>true</optional>
    </dependency>
</dependencies>
```

### 5. Execute o projeto

```bash
mvn spring-boot:run
```

O Flyway criará automaticamente todas as tabelas ao subir. A API estará disponível em `http://localhost:8080`.

### 6. Insira um usuário de teste no banco

Para poder fazer login, insira um usuário manualmente (senha hasheada com BCrypt):

```sql
-- Insira um perfil
INSERT INTO perfis (nome) VALUES ('ROLE_USER');

-- Insira um usuário (senha: 123456 hasheada com BCrypt link: https://bcrypt-generator.com/)
INSERT INTO usuarios (nome, email, senha)
VALUES (
  'Admin',
  'admin@forumhub.com',
  '$2a$10$Y9CdHPPtbJh.ySMk.x3w5.v9Zq2zGVE4MFsOhRoE7Ay5CjCLvGFu'
);

-- Vincule o usuário ao perfil
INSERT INTO usuarios_perfis (usuario_id, perfil_id) VALUES (1, 1);

-- Insira um curso para testes
INSERT INTO cursos (nome, categoria) VALUES ('Java', 'Programação');
```

---

## 🌐 Endpoints da API

### Base URL
```
http://localhost:8080
```

---

### 🔐 Autenticação

| Método | URL | Descrição | Auth necessária |
|--------|-----|-----------|----------------|
| POST | `/login` | Realiza login e retorna token JWT | ❌ Não |

---

### 📝 Tópicos

| Método | URL | Descrição | Auth necessária |
|--------|-----|-----------|----------------|
| POST | `/topicos` | Cria um novo tópico | ✅ Sim |
| GET | `/topicos` | Lista todos os tópicos (paginado) | ✅ Sim |
| GET | `/topicos/{id}` | Detalha um tópico específico | ✅ Sim |
| PUT | `/topicos/{id}` | Atualiza um tópico existente | ✅ Sim |
| DELETE | `/topicos/{id}` | Remove um tópico | ✅ Sim |

---

## 📬 Testando no Postman

### PASSO 1 — Login (obtenha o token primeiro)

```
POST http://localhost:8080/login
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "login": "admin@forumhub.com",
  "senha": "123456"
}
```

**Resposta de sucesso (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

> ⚠️ Copie o token recebido. Ele será usado em todas as próximas requisições.

---

### Como configurar o token no Postman

Em cada requisição protegida, vá na aba **Authorization**:
- Type: `Bearer Token`
- Token: `cole o token aqui`

Ou adicione manualmente no header:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

### PASSO 2 — Criar Tópico

```
POST http://localhost:8080/topicos
Content-Type: application/json
Authorization: Bearer {token}
```

**Body (JSON):**
```json
{
  "titulo": "Como usar Streams em Java?",
  "mensagem": "Não entendi como funciona a API de Streams do Java 8.",
  "autorId": 1,
  "cursoId": 1
}
```

**Resposta de sucesso (201 Created):**
```json
{
  "id": 1,
  "titulo": "Como usar Streams em Java?",
  "mensagem": "Não entendi como funciona a API de Streams do Java 8.",
  "dataCriacao": "2024-11-10T14:30:00",
  "status": "ABERTO",
  "nomeAutor": "Admin",
  "nomeCurso": "Java"
}
```

**Erro — campos obrigatórios ausentes (400):**
```json
{
  "status": 400,
  "erro": "Erro de validação nos campos enviados",
  "campos": [
    { "campo": "titulo", "mensagem": "Título é obrigatório" }
  ]
}
```

**Erro — tópico duplicado (400):**
```json
{
  "status": 400,
  "erro": "Erro na requisição",
  "mensagem": "Já existe um tópico com este título e mensagem"
}
```

---

### PASSO 3 — Listar Todos os Tópicos

```
GET http://localhost:8080/topicos
Authorization: Bearer {token}
```

**Resposta de sucesso (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "titulo": "Como usar Streams em Java?",
      "mensagem": "Não entendi como funciona a API de Streams do Java 8.",
      "dataCriacao": "2024-11-10T14:30:00",
      "status": "ABERTO",
      "nomeAutor": "Admin",
      "nomeCurso": "Java"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10
  },
  "totalElements": 1,
  "totalPages": 1
}
```

#### Variações com filtros e paginação:

```
# Filtrar por nome do curso
GET http://localhost:8080/topicos?nomeCurso=Java

# Filtrar por ano
GET http://localhost:8080/topicos?ano=2024

# Filtrar por curso E ano
GET http://localhost:8080/topicos?nomeCurso=Java&ano=2024

# Paginação: página 0, 5 itens por página
GET http://localhost:8080/topicos?page=0&size=5

# Combinando filtro + paginação
GET http://localhost:8080/topicos?nomeCurso=Java&ano=2024&page=0&size=5
```

---

### PASSO 4 — Detalhar um Tópico Específico

```
GET http://localhost:8080/topicos/1
Authorization: Bearer {token}
```

**Resposta de sucesso (200 OK):**
```json
{
  "id": 1,
  "titulo": "Como usar Streams em Java?",
  "mensagem": "Não entendi como funciona a API de Streams do Java 8.",
  "dataCriacao": "2024-11-10T14:30:00",
  "status": "ABERTO",
  "nomeAutor": "Admin",
  "nomeCurso": "Java"
}
```

**Erro — tópico não encontrado (404):**
```json
{
  "status": 404,
  "erro": "Recurso não encontrado",
  "mensagem": null
}
```

---

### PASSO 5 — Atualizar um Tópico

```
PUT http://localhost:8080/topicos/1
Content-Type: application/json
Authorization: Bearer {token}
```

**Body — atualizar apenas o status:**
```json
{
  "id": 1,
  "status": "RESPONDIDO"
}
```

**Body — atualizar título e mensagem:**
```json
{
  "id": 1,
  "titulo": "Como usar Streams em Java? (atualizado)",
  "mensagem": "Encontrei mais dúvidas sobre Collectors e Reduce."
}
```

**Body — atualizar tudo:**
```json
{
  "id": 1,
  "titulo": "Novo título",
  "mensagem": "Nova mensagem atualizada",
  "status": "SOLUCIONADO"
}
```

**Resposta de sucesso (200 OK):**
```json
{
  "id": 1,
  "titulo": "Novo título",
  "mensagem": "Nova mensagem atualizada",
  "dataCriacao": "2024-11-10T14:30:00",
  "status": "SOLUCIONADO",
  "nomeAutor": "Admin",
  "nomeCurso": "Java"
}
```

> Os campos `titulo`, `mensagem` e `status` são **opcionais** no PUT.
> Envie apenas o que deseja alterar.

---

### PASSO 6 — Excluir um Tópico

```
DELETE http://localhost:8080/topicos/1
Authorization: Bearer {token}
```

**Resposta de sucesso (204 No Content):**
```
[sem corpo na resposta]
```

**Erro — tópico não encontrado (404):**
```json
{
  "status": 404,
  "erro": "Recurso não encontrado",
  "mensagem": null
}
```

---

## 📊 Tabela Resumo de Respostas HTTP

| Situação | Código HTTP |
|---|---|
| Tópico criado com sucesso | 201 Created |
| Listagem / Detalhamento / Atualização OK | 200 OK |
| Tópico excluído com sucesso | 204 No Content |
| Campo obrigatório ausente ou inválido | 400 Bad Request |
| Tópico duplicado (mesmo título e mensagem) | 400 Bad Request |
| Login ou senha incorretos | 401 Unauthorized |
| Token ausente ou inválido | 403 Forbidden |
| Tópico não encontrado pelo ID | 404 Not Found |

---

## 📐 Regras de Negócio

### Cadastro de Tópico (POST)
- Todos os campos são obrigatórios: `titulo`, `mensagem`, `autorId`, `cursoId`
- Não é permitido cadastrar dois tópicos com o mesmo `titulo` **e** a mesma `mensagem`
- O status é definido automaticamente como `ABERTO` na criação
- A `dataCriacao` é preenchida automaticamente com o momento atual

### Atualização de Tópico (PUT)
- Os campos `titulo`, `mensagem` e `status` são opcionais — só atualiza o que for enviado
- A mesma regra de duplicidade do cadastro é aplicada na atualização
- O `@Transactional` garante o `UPDATE` automático sem precisar chamar `save()`

### Status disponíveis
```
ABERTO       → tópico recém criado
RESPONDIDO   → recebeu ao menos uma resposta
FECHADO      → encerrado sem solução
SOLUCIONADO  → uma resposta foi marcada como solução
```

---

## 🗃️ Migrations

As migrations são executadas automaticamente pelo **Flyway** ao iniciar a aplicação.

| Arquivo | Descrição                                                                               |
|---|-----------------------------------------------------------------------------------------|
| `V1__create-table.sql` | Cria tabelas `cursos`, `perfis`, `usuarios`, `usuarios_perfis`, `topicos` e `respostas` |

---

## 🔒 Segurança JWT

### Fluxo de autenticação

```
1. POST /login  →  retorna token JWT válido por 2 horas
2. Todas as rotas /topicos exigem: Authorization: Bearer {token}
3. SecurityFilter valida o token a cada requisição
4. Token expirado ou inválido → 403 Forbidden
```

### Como funciona internamente

```
Requisição chega
      │
      ▼
SecurityFilter (OncePerRequestFilter)
      ├─ Lê header Authorization: Bearer <token>
      ├─ TokenService.getSubject(token) → valida HMAC256
      ├─ UsuarioRepository.findByEmail(subject)
      └─ Autentica no SecurityContextHolder
            │
            ▼
      Controller executa normalmente
```
