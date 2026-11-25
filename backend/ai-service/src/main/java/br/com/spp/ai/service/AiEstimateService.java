package br.com.spp.ai.service;

import br.com.spp.ai.client.PropertyClient;
import br.com.spp.ai.client.PropertyView;
import br.com.spp.ai.dto.AiEstimateRequest;
import br.com.spp.ai.dto.AiEstimateResponse;
import br.com.spp.ai.dto.PageResponse;
import br.com.spp.ai.entity.AiEstimate;
import br.com.spp.ai.exception.AiEstimateNotFoundException;
import br.com.spp.ai.mapper.AiEstimateMapper;
import br.com.spp.ai.model.AiValuationModel;
import br.com.spp.ai.repository.AiEstimateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Regras de negócio da estimativa por IA: busca o imóvel no property-service
 * (propagando o JWT), executa o modelo e persiste o resultado explicável.
 */
@Service
public class AiEstimateService {

    private static final Logger log = LoggerFactory.getLogger(AiEstimateService.class);

    private final AiEstimateRepository repository;
    private final PropertyClient propertyClient;
    private final AiValuationModel model;
    private final AiEstimateMapper mapper;

    public AiEstimateService(AiEstimateRepository repository, PropertyClient propertyClient,
                             AiValuationModel model, AiEstimateMapper mapper) {
        this.repository = repository;
        this.propertyClient = propertyClient;
        this.model = model;
        this.mapper = mapper;
    }

    @Transactional
    public AiEstimateResponse create(AiEstimateRequest request, String requester, String bearerToken) {
        PropertyView property = propertyClient.fetch(request.propertyId(), bearerToken);
        AiValuationModel.Result result = model.estimate(property);

        AiEstimate estimate = AiEstimate.builder()
                .propertyId(property.id())
                .requestedBy(requester)
                .estimatedValue(result.estimatedValue())
                .confidence(result.confidence())
                .modelVersion(result.modelVersion())
                .factors(result.factors())
                .propertyType(property.type())
                .areaSqm(property.areaSqm())
                .city(property.city())
                .state(property.state())
                .build();

        repository.save(estimate);
        log.info("Estimativa IA {} do imóvel {} = {} (confiança {}) por {}",
                estimate.getId(), property.id(), result.estimatedValue(), result.confidence(), requester);
        return mapper.toResponse(estimate);
    }

    @Transactional(readOnly = true)
    public AiEstimateResponse findById(UUID id, String requester) {
        return mapper.toResponse(repository.findByIdAndRequestedBy(id, requester)
                .orElseThrow(() -> new AiEstimateNotFoundException(id)));
    }

    @Transactional(readOnly = true)
    public PageResponse<AiEstimateResponse> list(String requester, Pageable pageable) {
        return PageResponse.from(
                repository.findByRequestedBy(requester, pageable).map(mapper::toResponse));
    }
}
