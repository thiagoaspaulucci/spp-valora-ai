package br.com.spp.market.mapper;

import br.com.spp.market.dto.MarketReferenceRequest;
import br.com.spp.market.dto.MarketReferenceResponse;
import br.com.spp.market.dto.RateResponse;
import br.com.spp.market.entity.MarketReference;
import org.springframework.stereotype.Component;

/**
 * Conversões da entidade {@link MarketReference} e seus DTOs.
 */
@Component
public class MarketReferenceMapper {

    public MarketReference toEntity(MarketReferenceRequest request, String createdBy) {
        return MarketReference.builder()
                .city(request.city())
                .state(request.state())
                .propertyType(request.propertyType())
                .pricePerSqm(request.pricePerSqm())
                .effectiveDate(request.effectiveDate())
                .source(request.source())
                .createdBy(createdBy)
                .build();
    }

    public void update(MarketReference ref, MarketReferenceRequest request) {
        ref.setCity(request.city());
        ref.setState(request.state());
        ref.setPropertyType(request.propertyType());
        ref.setPricePerSqm(request.pricePerSqm());
        ref.setEffectiveDate(request.effectiveDate());
        ref.setSource(request.source());
    }

    public MarketReferenceResponse toResponse(MarketReference r) {
        return new MarketReferenceResponse(
                r.getId(), r.getCity(), r.getState(), r.getPropertyType(),
                r.getPricePerSqm(), r.getEffectiveDate(), r.getSource(),
                r.getCreatedBy(), r.getCreatedAt(), r.getUpdatedAt());
    }

    public RateResponse toRate(MarketReference r) {
        return new RateResponse(
                r.getCity(), r.getState(), r.getPropertyType(),
                r.getPricePerSqm(), r.getEffectiveDate(), r.getSource());
    }
}
