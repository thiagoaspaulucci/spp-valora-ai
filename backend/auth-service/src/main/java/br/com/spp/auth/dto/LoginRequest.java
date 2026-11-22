package br.com.spp.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Credenciais para autenticação.
 */
@Schema(description = "Requisição de login")
public record LoginRequest(

        @Schema(example = "maria@exemplo.com")
        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "E-mail inválido")
        String email,

        @Schema(example = "senhaForte123")
        @NotBlank(message = "A senha é obrigatória")
        String password
) {
}
