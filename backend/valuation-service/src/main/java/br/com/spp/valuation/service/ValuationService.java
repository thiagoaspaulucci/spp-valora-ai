package br.com.spp.valuation.service;

import br.com.spp.valuation.client.MarketClient;
import br.com.spp.valuation.client.PropertyClient;
import br.com.spp.valuation.client.PropertyView;
import br.com.spp.valuation.dto.PageResponse;
import br.com.spp.valuation.dto.ValuationRequest;
import br.com.spp.valuation.dto.ValuationResponse;
import br.com.spp.valuation.engine.ValuationEngine;
import br.com.spp.valuation.entity.Valuation;
import br.com.spp.valuation.entity.ValuationMethod;
import br.com.spp.valuation.entity.ValuationStatus;
import br.com.spp.valuation.exception.ValuationNotFoundException;
import br.com.spp.valuation.mapper.ValuationMapper;
import br.com.spp.valuation.repository.ValuationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Regras de negócio da avaliação: consulta o imóvel no property-service
 * (propagando o JWT), aplica a fórmula de valoração e persiste o laudo.
 */
@Service
public class ValuationService {

    private static final Logger log = LoggerFactory.getLogger(ValuationService.class);

    private final ValuationRepository repository;
    private final PropertyClient propertyClient;
    private final MarketClient marketClient;
    private final ValuationEngine engine;
    private final ValuationMapper mapper;

    public ValuationService(ValuationRepository repository, PropertyClient propertyClient,
                            MarketClient marketClient, ValuationEngine engine, ValuationMapper mapper) {
        this.repository = repository;
        this.propertyClient = propertyClient;
        this.marketClient = marketClient;
        this.engine = engine;
        this.mapper = mapper;
    }

    @Transactional
    public ValuationResponse create(ValuationRequest request, String requester, String bearerToken) {
        PropertyView property = propertyClient.fetch(request.propertyId(), bearerToken);
        BigDecimal marketRate = marketClient
                .fetchRate(property.city(), property.state(), property.type(), bearerToken)
                .orElse(null);
        ValuationEngine.Result result = engine.estimate(property, marketRate);

        Valuation valuation = Valuation.builder()
                .propertyId(property.id())
                .requestedBy(requester)
                .method(ValuationMethod.SIMPLE_FORMULA)
                .estimatedValue(result.estimatedValue())
                .pricePerSqm(result.pricePerSqm())
                .areaSqm(property.areaSqm())
                .propertyType(property.type())
                .city(property.city())
                .state(property.state())
                .status(ValuationStatus.COMPLETED)
                .build();

        repository.save(valuation);
        log.info("Avaliação {} do imóvel {} = {} por {}",
                valuation.getId(), property.id(), result.estimatedValue(), requester);
        return mapper.toResponse(valuation);
    }

    @Transactional(readOnly = true)
    public ValuationResponse findById(UUID id, String requester) {
        return mapper.toResponse(repository.findByIdAndRequestedBy(id, requester)
                .orElseThrow(() -> new ValuationNotFoundException(id)));
    }

    @Transactional(readOnly = true)
    public PageResponse<ValuationResponse> list(String requester, Pageable pageable) {
        return PageResponse.from(
                repository.findByRequestedBy(requester, pageable).map(mapper::toResponse));
    }
}
