# SPP Valora AI — Deploy em produção (1 VM + Docker Compose)

Guia para subir **toda a plataforma** (7 microserviços + gateway + Postgres + Redis + frontend) numa única VM Linux, com o **nginx** como único ponto público na porta 80.

```
                            Internet
                               │  :80
                        ┌──────▼───────┐
                        │   frontend   │  nginx: serve o SPA
                        │   (nginx)    │  proxy /api, /oauth2 → gateway
                        └──────┬───────┘
                               │ (rede interna)
                        ┌──────▼───────┐
                        │   gateway    │  :8090  valida JWT (JWKS)
                        └──────┬───────┘
      ┌──────────┬───────────┼───────────┬──────────┬──────────┐
   auth:8080  property  valuation     report     market       ai
   (RSA/JWKS)  :8081     :8082         :8083      :8084      :8085
      └──────────┴───────────┴───────────┴──────────┴──────────┘
                     │                         │
                 Postgres:5432             Redis:6379
```

Só a porta **80** fica exposta. Postgres, Redis, gateway e os serviços **não** publicam portas no host.

---

## 1. Pré-requisitos na VM

- Linux (Ubuntu 22.04+ recomendado), 2+ vCPU, **4 GB RAM no mínimo** (8 GB folgado — são 8 JVMs), ~10 GB de disco.
- Docker Engine + plugin Compose v2 e OpenSSL:

```bash
curl -fsSL https://get.docker.com | sh
sudo usermod -aG docker "$USER"   # saia e entre de novo na sessão
docker compose version
openssl version
```

- Portas: libere a **80/tcp** no firewall/security group. (443 fica para a etapa de TLS, abaixo.)

---

## 2. Obter o código

```bash
git clone https://github.com/thiagoaspaulucci/spp-valora-ai.git
cd spp-valora-ai
```

> Use o `master` (ou a tag de release). Após o merge do deploy, tudo que o compose precisa está no repositório.

---

## 3. Gerar as chaves RSA (assinam os JWT)

```bash
cd infrastructure/keys
./generate-keys.sh
cd ../..
```

Cria `infrastructure/keys/private.pem` e `public.pem`. O compose monta essa pasta em `/keys:ro` no auth-service.

- `private.pem` é **segredo** — já está no `.gitignore`, nunca suba para o git nem compartilhe.
- Trocar as chaves depois **invalida todos os tokens** emitidos (todo mundo é deslogado). Faça backup seguro do par.

---

## 4. Configurar segredos (`.env`)

```bash
cd infrastructure/compose
cp .env.prod.example .env
openssl rand -base64 24   # gere uma senha para o Postgres
openssl rand -base64 24   # gere uma senha para o Redis
nano .env
```

Preencha no `.env`:

| Variável | O que é |
|---|---|
| `POSTGRES_DB` / `POSTGRES_USER` / `POSTGRES_PASSWORD` | banco e credenciais |
| `REDIS_PASSWORD` | senha do Redis |
| `PUBLIC_ORIGIN` | URL pública pela qual o navegador acessa (ex.: `https://valora.suaempresa.com.br`, ou `http://SEU_IP` em teste) — usada no CORS |

O `.env` real **não** é versionado (`.gitignore`).

---

## 5. Subir a stack

```bash
# ainda em infrastructure/compose
docker compose -f docker-compose.prod.yml --env-file .env up -d --build
```

A primeira execução **compila cada serviço dentro do Docker** (Maven + build do frontend) — pode levar vários minutos e baixar bastante. Acompanhe:

```bash
docker compose -f docker-compose.prod.yml ps
docker compose -f docker-compose.prod.yml logs -f auth-service
```

Ordem de inicialização (orquestrada por healthcheck): Postgres/Redis → auth-service → demais serviços → gateway → frontend. É normal os serviços reiniciarem algumas vezes enquanto o Postgres/auth ficam `healthy`.

### Validar

```bash
# Na VM:
curl -i http://localhost/                      # SPA (200 + HTML)
curl -s http://localhost/oauth2/jwks           # JWKS via nginx→gateway→auth
curl -i -X POST http://localhost/api/v1/auth/login \
  -H 'Content-Type: application/json' -d '{"email":"x","password":"y"}'   # 401/400 = rota viva
```

No navegador: `http://SEU_IP/` → tela de login do SPP Valora. Registre um usuário e percorra imóvel → avaliação → laudo.

---

## 6. Operação

```bash
# status / logs
docker compose -f docker-compose.prod.yml ps
docker compose -f docker-compose.prod.yml logs -f <serviço>

# atualizar após git pull
git pull
docker compose -f docker-compose.prod.yml --env-file .env up -d --build

# parar / remover (mantém o volume do Postgres)
docker compose -f docker-compose.prod.yml down

# backup do banco
docker exec spp-postgres pg_dump -U "$POSTGRES_USER" "$POSTGRES_DB" > backup_$(date +%F).sql
```

Os dados do Postgres ficam no volume nomeado `postgres-data` e sobrevivem a `down`/`up`. `down -v` **apaga** o banco.

---

## 7. TLS / HTTPS (recomendado antes de expor na internet)

O compose entrega HTTP na 80. Para produção real, ponha HTTPS na frente. Opção mais simples: **Caddy** como reverse proxy com certificado automático (Let's Encrypt), apontando para o `frontend`. Alternativa: Traefik, ou nginx + certbot. Ao ativar HTTPS, ajuste `PUBLIC_ORIGIN` para `https://seu-dominio` e recrie a stack.

---

## ⚠️ Aviso importante — primeira execução de verdade

Os `Dockerfile` dos serviços e o `docker-compose.prod.yml` foram escritos e revisados, mas **nunca foram executados ponta a ponta**: o Docker do ambiente de desenvolvimento está quebrado (bug de socket no Docker Desktop/Windows), então toda a verificação até aqui foi via **CI** (compilação + testes de cada serviço, incluindo Testcontainers). Este deploy na VM é o **primeiro teste real do sistema rodando junto**.

Portanto, trate o primeiro `up` como shakedown: suba, leia os logs de cada serviço, e me traga qualquer erro de inicialização (conexão com Postgres/Redis, Flyway, JWKS, build do frontend) que eu ajusto os artefatos. É esperado aparecer um ou outro ajuste fino nesta primeira vez.

---

## Referência rápida de portas (internas)

| Serviço | Porta | Pública? |
|---|---|---|
| frontend (nginx) | 80 | **sim** |
| gateway | 8090 | não |
| auth-service | 8080 | não |
| property-service | 8081 | não |
| valuation-service | 8082 | não |
| report-service | 8083 | não |
| market-service | 8084 | não |
| ai-service | 8085 | não |
| postgres | 5432 | não |
| redis | 6379 | não |
