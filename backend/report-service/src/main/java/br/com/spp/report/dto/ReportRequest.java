package br.com.spp.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Requisição para gerar um laudo a partir de uma avaliação existente.
 */
@Schema(description = "Requisição de geração de laudo")
public record ReportRequest(

        @NotNull(message = "O id da avaliação é obrigatório")
        UUID valuationId
) {
}
