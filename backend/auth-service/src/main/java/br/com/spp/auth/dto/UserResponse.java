package br.com.spp.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;
import java.util.UUID;

/**
 * Representação pública de um usuário (nunca expõe a senha).
 */
@Schema(description = "Dados públicos do usuário autenticado")
public record UserResponse(

        UUID id,

        String name,

        String email,

        Set<String> roles,

        boolean enabled
) {
}
