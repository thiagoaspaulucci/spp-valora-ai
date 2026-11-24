package br.com.spp.valuation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Requisição para avaliar um imóvel já cadastrado.
 */
@Schema(description = "Requisição de avaliação de imóvel")
public record ValuationRequest(

        @NotNull(message = "O id do imóvel é obrigatório")
        UUID propertyId
) {
}
