# Task Manager API

API REST para gerenciamento de tarefas, desenvolvida com Java 25, Spring Boot 4 e PostgreSQL 17.

## Tecnologias

- Java 25
- Spring Boot 4.1.2
- Spring Web MVC
- Spring Data JPA / Hibernate
- PostgreSQL 17
- Flyway
- Jakarta Bean Validation
- JUnit 5, Mockito e MockMvc
- Maven Wrapper
- Docker Compose

## Funcionalidades

- Criar tarefas
- Listar tarefas
- Consultar tarefa por ID
- Atualizar tarefas
- Concluir tarefas
- Excluir tarefas
- Validar dados de entrada
- Retornar erros HTTP padronizados com ProblemDetail

## Status das tarefas

- `PENDENTE`
- `EM_ANDAMENTO`
- `CONCLUIDA`

## Endpoints

| Metodo | Endpoint | Descricao |
| --- | --- | --- |
| POST | `/api/tarefas` | Criar tarefa |
| GET | `/api/tarefas` | Listar tarefas |
| GET | `/api/tarefas/{id}` | Buscar por ID |
| PUT | `/api/tarefas/{id}` | Atualizar tarefa |
| PATCH | `/api/tarefas/{id}/concluir` | Concluir tarefa |
| DELETE | `/api/tarefas/{id}` | Excluir tarefa |

### Exemplo de criacao

`POST /api/tarefas`

```json
{
  "titulo": "Estudar Spring Boot",
  "descricao": "Implementar uma API REST"
}
```

Resposta de sucesso: HTTP `201 Created`, com cabecalho `Location`.

### Exemplo de atualizacao

`PUT /api/tarefas/1`

```json
{
  "titulo": "Estudar Spring Boot",
  "descricao": "Revisar testes automatizados",
  "status": "EM_ANDAMENTO"
}
```

## Executar localmente

### Requisitos

- JDK 25
- Docker Desktop com Docker Compose
- Porta 5432 disponivel para o PostgreSQL
- Porta 18080 disponivel para a API

### 1. Configurar o ambiente

No PowerShell, na raiz do projeto:

```powershell
Copy-Item .env.example .env
```

Substitua a senha demonstrativa do arquivo `.env` por uma senha local.

### 2. Iniciar o PostgreSQL

```powershell
docker compose --env-file .env up -d
```

### 3. Configurar a senha da aplicacao

Na mesma sessao do PowerShell:

```powershell
$env:DB_PASSWORD = (Get-Content .env | Where-Object { $_ -match '^POSTGRES_PASSWORD=' } | Select-Object -First 1).Split('=', 2)[1]
```

### 4. Executar a API

```powershell
.\mvnw.cmd spring-boot:run
```

A API estara disponivel em:

`http://localhost:18080/api/tarefas`

## Testes

Com PostgreSQL em execucao e `DB_PASSWORD` configurada:

```powershell
.\mvnw.cmd clean verify
```

Na validacao local de 16/11/2026:

- 10 testes unitarios do Service
- 12 testes HTTP do Controller
- 1 teste de inicializacao do Spring
- 23 testes aprovados, sem falhas ou erros

## Banco de dados

O Flyway gerencia a estrutura do banco por migrations versionadas.

A migration inicial cria a tabela `tarefas`, com campos de identificacao, titulo, descricao, status e timestamps.

O Hibernate utiliza `ddl-auto=validate`, verificando a compatibilidade do esquema sem gerar automaticamente as tabelas.

## Estrutura

```text
src/main/java/br/com/portfolio/taskmanager/
  controller/
  dto/
  entity/
  exception/
  repository/
  service/

src/main/resources/
  db/migration/

src/test/java/br/com/portfolio/taskmanager/
  controller/
  service/
```

## Seguranca

O arquivo `.env` contem configuracoes locais e nao deve ser versionado.

Utilize `.env.example` como referencia, sem armazenar senhas reais no repositorio.