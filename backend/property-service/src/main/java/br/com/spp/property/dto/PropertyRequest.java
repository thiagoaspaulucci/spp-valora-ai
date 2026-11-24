package br.com.spp.property.dto;

import br.com.spp.property.entity.PropertyType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * Dados para criação/atualização de um imóvel.
 */
@Schema(description = "Requisição de cadastro/atualização de imóvel")
public record PropertyRequest(

        @NotBlank(message = "O título é obrigatório")
        String title,

        @NotNull(message = "O tipo é obrigatório")
        PropertyType type,

        @NotBlank(message = "A rua é obrigatória")
        String street,

        String number,

        String complement,

        String neighborhood,

        @NotBlank(message = "A cidade é obrigatória")
        String city,

        @NotBlank(message = "O estado é obrigatório")
        @Size(min = 2, max = 2, message = "O estado deve ter 2 letras (UF)")
        String state,

        @NotBlank(message = "O CEP é obrigatório")
        String zipCode,

        @NotNull(message = "A área é obrigatória")
        @Positive(message = "A área deve ser positiva")
        BigDecimal areaSqm,

        @PositiveOrZero(message = "Quartos não pode ser negativo")
        int bedrooms,

        @PositiveOrZero(message = "Banheiros não pode ser negativo")
        int bathrooms,

        @PositiveOrZero(message = "Vagas não pode ser negativo")
        int parkingSpots,

        @Min(value = 1800, message = "Ano de construção inválido")
        Integer yearBuilt,

        @PositiveOrZero(message = "O preço não pode ser negativo")
        BigDecimal askingPrice
) {
}
