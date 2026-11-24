package br.com.spp.valuation.mapper;

import br.com.spp.valuation.dto.ValuationResponse;
import br.com.spp.valuation.entity.Valuation;
import org.springframework.stereotype.Component;

/**
 * Conversão da entidade {@link Valuation} para o DTO de saída.
 */
@Component
public class ValuationMapper {

    public ValuationResponse toResponse(Valuation v) {
        return new ValuationResponse(
                v.getId(),
                v.getPropertyId(),
                v.getRequestedBy(),
                v.getMethod(),
                v.getEstimatedValue(),
                v.getPricePerSqm(),
                v.getAreaSqm(),
                v.getPropertyType(),
                v.getCity(),
                v.getState(),
                v.getStatus(),
                v.getNotes(),
                v.getCreatedAt());
    }
}
