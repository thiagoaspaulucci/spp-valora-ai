package br.com.spp.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Dados para registro de um novo usuário.
 */
@Schema(description = "Requisição de registro de usuário")
public record RegisterRequest(

        @Schema(example = "Maria Silva")
        @NotBlank(message = "O nome é obrigatório")
        String name,

        @Schema(example = "maria@exemplo.com")
        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "E-mail inválido")
        String email,

        @Schema(example = "senhaForte123")
        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 8, message = "A senha deve ter ao menos 8 caracteres")
        String password
) {
}
