package br.com.spp.ai.mapper;

import br.com.spp.ai.dto.AiEstimateResponse;
import br.com.spp.ai.entity.AiEstimate;
import org.springframework.stereotype.Component;

/**
 * Conversão da entidade {@link AiEstimate} para o DTO de saída.
 */
@Component
public class AiEstimateMapper {

    public AiEstimateResponse toResponse(AiEstimate e) {
        return new AiEstimateResponse(
                e.getId(),
                e.getPropertyId(),
                e.getRequestedBy(),
                e.getEstimatedValue(),
                e.getConfidence(),
                e.getModelVersion(),
                e.getFactors(),
                e.getPropertyType(),
                e.getAreaSqm(),
                e.getCity(),
                e.getState(),
                e.getCreatedAt());
    }
}
