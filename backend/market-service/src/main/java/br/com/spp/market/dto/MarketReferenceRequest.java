package br.com.spp.market.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Dados para criar/atualizar um preço de referência de mercado.
 */
@Schema(description = "Requisição de preço de referência de mercado")
public record MarketReferenceRequest(

        @NotBlank(message = "A cidade é obrigatória")
        String city,

        @NotBlank(message = "O estado é obrigatório")
        @Size(min = 2, max = 2, message = "O estado deve ter 2 letras (UF)")
        String state,

        @NotBlank(message = "O tipo de imóvel é obrigatório")
        String propertyType,

        @NotNull(message = "O preço por m² é obrigatório")
        @Positive(message = "O preço por m² deve ser positivo")
        BigDecimal pricePerSqm,

        @NotNull(message = "A data de vigência é obrigatória")
        LocalDate effectiveDate,

        String source
) {
}
