# SPP Valora AI

Plataforma corporativa baseada em microserviços.

## Stack

- **Backend:** Java 21, Spring Boot 4, Spring Security (OAuth2 Resource Server + JWT RSA + JWKS)
- **Banco:** PostgreSQL 17 + Flyway (migrations)
- **Cache / Token store:** Redis 8 (refresh tokens revogáveis + throttling de login)
- **Observabilidade:** Actuator + Micrometer/Prometheus + correlation id (`X-Request-Id`)
- **Qualidade:** JUnit 5, Mockito, Testcontainers, OpenAPI/Swagger, Lombok
- **Infra:** Docker, Docker Compose
- **Frontend:** React + TypeScript *(a iniciar)*
- **Automação:** CLI Python (scaffolding de código)

## Estrutura

```
.
├── backend/                 # Microserviços (Maven multi-módulo)
│   ├── pom.xml              # Parent + agregador (dependencyManagement compartilhado)
│   └── auth-service/        # Serviço de autenticação
├── infrastructure/
│   └── compose/            # docker-compose (Postgres, Redis, auth-service)
└── automation/             # CLI de geração de código (Python + Click)
```

## Como rodar (ambiente local)

### 1. Subir tudo com Docker Compose

```bash
cp infrastructure/compose/.env.example infrastructure/compose/.env
docker compose -f infrastructure/compose/docker-compose.yml up -d --build
```

Sobe PostgreSQL (`5432`), Redis (`6379`, com senha) e o **auth-service** (`8080`),
que aguarda Postgres e Redis ficarem saudáveis antes de iniciar.

### 2. Rodar o auth-service localmente (alternativa)

```bash
docker compose -f infrastructure/compose/docker-compose.yml up -d postgres redis
cd backend/auth-service && ./mvnw spring-boot:run
```

Serviço em `http://localhost:8080` · Swagger em `http://localhost:8080/swagger-ui.html`
· JWKS em `http://localhost:8080/oauth2/jwks`.

### 3. Testes (requer Docker para os testes de integração)

```bash
cd backend/auth-service && ./mvnw clean verify
```

## Perfis e segredos

- **`dev`** (padrão): defaults locais; chave RSA **efêmera** gerada na inicialização.
- **`prod`**: todos os segredos e as **chaves RSA** vêm de variáveis de ambiente
  (a app falha ao iniciar se ausentes). Nada sensível fica versionado.

Gerar um par de chaves RSA para produção:

```bash
openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 -out private.pem
openssl rsa -pubout -in private.pem -out public.pem
# Aponte JWT_PRIVATE_KEY_LOCATION / JWT_PUBLIC_KEY_LOCATION para esses arquivos.
```

## API (auth-service)

| Método | Rota | Acesso |
|---|---|---|
| POST | `/api/v1/auth/register` | público |
| POST | `/api/v1/auth/login` | público |
| POST | `/api/v1/auth/refresh` | público (rotação de refresh) |
| POST | `/api/v1/auth/logout` | público (revoga refresh) |
| GET | `/api/v1/auth/me` | JWT |
| GET | `/oauth2/jwks` | público (chaves de validação) |

## Convenções

- Migrations Flyway em `src/main/resources/db/migration` — **nunca** alterar o banco manualmente.
- Entidades com UUID; `FetchType.LAZY` por padrão (roles em EAGER, por serem sempre necessárias na autenticação).
- API expõe DTOs (Request/Response), nunca entidades; erros no formato `ApiError` (com `traceId`).
- Commits seguem [Conventional Commits](https://www.conventionalcommits.org/).
