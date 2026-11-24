package br.com.spp.auth.controller;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Publica o conjunto de chaves públicas (JWKS) para que outros
 * microserviços possam validar os JWT emitidos por este serviço.
 * Expõe apenas parâmetros públicos — a chave privada nunca é serializada.
 */
@RestController
@Tag(name = "JWKS", description = "Chaves públicas para validação de tokens")
public class JwksController {

    private final JWKSet publicJwkSet;

    public JwksController(RSAKey rsaKey) {
        this.publicJwkSet = new JWKSet(rsaKey.toPublicJWK());
    }

    @GetMapping("/oauth2/jwks")
    @Operation(summary = "Retorna o JWK Set público (RFC 7517)")
    public Map<String, Object> keys() {
        return publicJwkSet.toJSONObject();
    }
}
