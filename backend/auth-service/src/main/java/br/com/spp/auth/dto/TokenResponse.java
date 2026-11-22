package br.com.spp.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Par de tokens emitido após autenticação bem-sucedida.
 */
@Schema(description = "Resposta com tokens de acesso e renovação")
public record TokenResponse(

        String accessToken,

        String refreshToken,

        @Schema(example = "Bearer")
        String tokenType,

        @Schema(description = "Validade do access token em segundos", example = "900")
        long expiresIn
) {
}
