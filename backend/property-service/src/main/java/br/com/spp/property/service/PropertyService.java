package br.com.spp.property.service;

import br.com.spp.property.dto.PageResponse;
import br.com.spp.property.dto.PropertyRequest;
import br.com.spp.property.dto.PropertyResponse;
import br.com.spp.property.entity.Property;
import br.com.spp.property.exception.PropertyNotFoundException;
import br.com.spp.property.mapper.PropertyMapper;
import br.com.spp.property.repository.PropertyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Regras de negócio do cadastro de imóveis, sempre escopadas ao dono
 * (o {@code subject} do JWT), garantindo isolamento entre usuários.
 */
@Service
public class PropertyService {

    private static final Logger log = LoggerFactory.getLogger(PropertyService.class);

    private final PropertyRepository repository;
    private final PropertyMapper mapper;

    public PropertyService(PropertyRepository repository, PropertyMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public PropertyResponse create(PropertyRequest request, String ownerSubject) {
        Property property = repository.save(mapper.toEntity(request, ownerSubject));
        log.info("Imóvel {} criado por {}", property.getId(), ownerSubject);
        return mapper.toResponse(property);
    }

    @Transactional(readOnly = true)
    public PropertyResponse findById(UUID id, String ownerSubject) {
        return mapper.toResponse(getOwned(id, ownerSubject));
    }

    @Transactional(readOnly = true)
    public PageResponse<PropertyResponse> list(String ownerSubject, Pageable pageable) {
        return PageResponse.from(
                repository.findByOwnerSubject(ownerSubject, pageable).map(mapper::toResponse));
    }

    @Transactional
    public PropertyResponse update(UUID id, PropertyRequest request, String ownerSubject) {
        Property property = getOwned(id, ownerSubject);
        mapper.update(property, request);
        return mapper.toResponse(property);
    }

    @Transactional
    public void delete(UUID id, String ownerSubject) {
        Property property = getOwned(id, ownerSubject);
        repository.delete(property);
        log.info("Imóvel {} removido por {}", id, ownerSubject);
    }

    private Property getOwned(UUID id, String ownerSubject) {
        return repository.findByIdAndOwnerSubject(id, ownerSubject)
                .orElseThrow(() -> new PropertyNotFoundException(id));
    }
}
