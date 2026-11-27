#!/usr/bin/env bash
# ============================================================================
# SPP Valora AI — bootstrap de VM (Ubuntu/Debian)
#
# Instala Docker, clona o repositório, gera as chaves RSA, cria o .env com
# senhas fortes, detecta o IP público e sobe toda a stack de produção.
#
# Uso na VM (como usuário com sudo):
#   curl -fsSL https://raw.githubusercontent.com/thiagoaspaulucci/spp-valora-ai/master/infrastructure/deploy/bootstrap-vm.sh -o bootstrap-vm.sh
#   bash bootstrap-vm.sh
#
# Reexecutar é seguro: não recria chaves nem .env já existentes.
# ============================================================================
set -euo pipefail

REPO_URL="https://github.com/thiagoaspaulucci/spp-valora-ai.git"
APP_DIR="${APP_DIR:-$HOME/spp-valora-ai}"

say() { printf '\n\033[1;36m==> %s\033[0m\n' "$*"; }

# --- 1. Dependências ---
say "Instalando dependências (docker, git, openssl, curl)..."
if ! command -v docker >/dev/null 2>&1; then
  curl -fsSL https://get.docker.com | sudo sh
  sudo usermod -aG docker "$USER" || true
fi
sudo apt-get update -y >/dev/null 2>&1 || true
sudo apt-get install -y git openssl curl >/dev/null 2>&1 || true

# docker compose v2 (plugin) precisa estar presente
if ! docker compose version >/dev/null 2>&1; then
  echo "ERRO: 'docker compose' (v2) não disponível. Instale o plugin compose e rode de novo."
  exit 1
fi

# wrapper que usa sudo se o usuário ainda não estiver no grupo docker nesta sessão
DOCKER="docker"
if ! docker info >/dev/null 2>&1; then DOCKER="sudo docker"; fi

# --- 2. Código ---
if [[ -d "$APP_DIR/.git" ]]; then
  say "Atualizando repositório em $APP_DIR..."
  git -C "$APP_DIR" pull --ff-only
else
  say "Clonando repositório em $APP_DIR..."
  git clone "$REPO_URL" "$APP_DIR"
fi

# --- 3. Chaves RSA ---
KEYS_DIR="$APP_DIR/infrastructure/keys"
if [[ -f "$KEYS_DIR/private.pem" ]]; then
  say "Chaves RSA já existem — mantendo (trocar invalidaria os tokens)."
else
  say "Gerando par de chaves RSA..."
  ( cd "$KEYS_DIR" && bash generate-keys.sh )
fi

# --- 4. .env ---
COMPOSE_DIR="$APP_DIR/infrastructure/compose"
ENV_FILE="$COMPOSE_DIR/.env"
if [[ -f "$ENV_FILE" ]]; then
  say ".env já existe — mantendo as senhas atuais."
else
  say "Criando .env com senhas fortes e IP público detectado..."
  PUBLIC_IP="$(curl -fsS https://api.ipify.org || echo '')"
  ORIGIN="http://${PUBLIC_IP:-localhost}"
  PG_PW="$(openssl rand -base64 24)"
  REDIS_PW="$(openssl rand -base64 24)"
  cat > "$ENV_FILE" <<EOF
POSTGRES_DB=spp_valora
POSTGRES_USER=spp
POSTGRES_PASSWORD=${PG_PW}
REDIS_PASSWORD=${REDIS_PW}
PUBLIC_ORIGIN=${ORIGIN}
EOF
  chmod 600 "$ENV_FILE"
  echo "    PUBLIC_ORIGIN=${ORIGIN}"
fi

# --- 5. Subir a stack ---
say "Subindo a stack (primeira vez compila tudo — pode demorar vários minutos)..."
cd "$COMPOSE_DIR"
$DOCKER compose -f docker-compose.prod.yml --env-file .env up -d --build

say "Status dos serviços:"
$DOCKER compose -f docker-compose.prod.yml ps

PUBLIC_IP="$(curl -fsS https://api.ipify.org || echo 'SEU_IP')"
cat <<EOF

============================================================
  SPP Valora AI no ar 🎉
  Acesse:  http://${PUBLIC_IP}/

  Ver logs de um serviço:
    cd $COMPOSE_DIR
    $DOCKER compose -f docker-compose.prod.yml logs -f auth-service

  Se algum serviço não ficar 'healthy', copie os logs e me mande.
============================================================
EOF
