package br.com.spp.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Requisição de renovação de tokens a partir de um refresh token válido.
 */
@Schema(description = "Requisição de refresh de tokens")
public record RefreshRequest(

        @NotBlank(message = "O refresh token é obrigatório")
        String refreshToken
) {
}
