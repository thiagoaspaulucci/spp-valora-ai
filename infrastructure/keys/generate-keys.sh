#!/usr/bin/env bash
# ============================================================================
# Gera o par de chaves RSA usado pelo auth-service para assinar os JWT.
# Rode na VM, dentro de infrastructure/keys, ANTES do primeiro `up`.
#
#   ./generate-keys.sh
#
# Produz:
#   private.pem  (PKCS#8 — assina os tokens; NÃO versionar, NÃO compartilhar)
#   public.pem   (SPKI/X.509 — publicado no /oauth2/jwks; pode ser público)
#
# O docker-compose.prod.yml monta esta pasta em /keys:ro no auth-service via
# JWT_PRIVATE_KEY_LOCATION=file:/keys/private.pem e .../public.pem.
# ============================================================================
set -euo pipefail

cd "$(dirname "$0")"

if [[ -f private.pem || -f public.pem ]]; then
  echo "ERRO: já existe private.pem/public.pem nesta pasta."
  echo "Trocar as chaves invalida todos os tokens emitidos. Apague manualmente se for intencional."
  exit 1
fi

# Chave privada RSA 2048 em PKCS#8
openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 -out private.pem

# Chave pública correspondente (SPKI)
openssl rsa -in private.pem -pubout -out public.pem

chmod 600 private.pem
chmod 644 public.pem

echo "OK: chaves geradas."
echo "  private.pem  -> mantenha em segredo (640/600, nunca no git)"
echo "  public.pem   -> exposta via /oauth2/jwks"
