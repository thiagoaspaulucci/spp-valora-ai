package br.com.spp.market.service;

import br.com.spp.market.dto.*;
import br.com.spp.market.entity.MarketReference;
import br.com.spp.market.exception.MarketReferenceNotFoundException;
import br.com.spp.market.mapper.MarketReferenceMapper;
import br.com.spp.market.repository.MarketReferenceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Regras de negócio dos preços de referência de mercado.
 * Escrita é restrita a administradores (ver controller); leitura/consulta
 * ficam disponíveis a qualquer usuário autenticado.
 */
@Service
public class MarketReferenceService {

    private static final Logger log = LoggerFactory.getLogger(MarketReferenceService.class);

    private final MarketReferenceRepository repository;
    private final MarketReferenceMapper mapper;

    public MarketReferenceService(MarketReferenceRepository repository, MarketReferenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public MarketReferenceResponse create(MarketReferenceRequest request, String createdBy) {
        MarketReference saved = repository.save(mapper.toEntity(request, createdBy));
        log.info("Preço de referência {} criado por {}", saved.getId(), createdBy);
        return mapper.toResponse(saved);
    }

    @Transactional
    public MarketReferenceResponse update(UUID id, MarketReferenceRequest request) {
        MarketReference ref = get(id);
        mapper.update(ref, request);
        return mapper.toResponse(ref);
    }

    @Transactional
    public void delete(UUID id) {
        repository.delete(get(id));
        log.info("Preço de referência {} removido", id);
    }

    @Transactional(readOnly = true)
    public MarketReferenceResponse findById(UUID id) {
        return mapper.toResponse(get(id));
    }

    @Transactional(readOnly = true)
    public PageResponse<MarketReferenceResponse> list(Pageable pageable) {
        return PageResponse.from(repository.findAll(pageable).map(mapper::toResponse));
    }

    @Transactional(readOnly = true)
    public RateResponse getRate(String city, String state, String propertyType) {
        return repository
                .findTopByCityIgnoreCaseAndStateIgnoreCaseAndPropertyTypeOrderByEffectiveDateDesc(
                        city, state, propertyType)
                .map(mapper::toRate)
                .orElseThrow(() -> new MarketReferenceNotFoundException(
                        "Sem preço de referência para %s/%s (%s)".formatted(city, state, propertyType)));
    }

    private MarketReference get(UUID id) {
        return repository.findById(id).orElseThrow(() -> new MarketReferenceNotFoundException(id));
    }
}
