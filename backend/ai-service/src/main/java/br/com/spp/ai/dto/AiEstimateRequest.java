package br.com.spp.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Requisição para estimar o valor de um imóvel com o modelo de IA.
 */
@Schema(description = "Requisição de estimativa por IA")
public record AiEstimateRequest(

        @NotNull(message = "O id do imóvel é obrigatório")
        UUID propertyId
) {
}
