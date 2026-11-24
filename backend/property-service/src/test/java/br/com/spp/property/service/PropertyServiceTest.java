package br.com.spp.property.service;

import br.com.spp.property.dto.PropertyRequest;
import br.com.spp.property.dto.PropertyResponse;
import br.com.spp.property.entity.Property;
import br.com.spp.property.entity.PropertyStatus;
import br.com.spp.property.entity.PropertyType;
import br.com.spp.property.exception.PropertyNotFoundException;
import br.com.spp.property.mapper.PropertyMapper;
import br.com.spp.property.repository.PropertyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PropertyServiceTest {

    @Mock
    private PropertyRepository repository;

    private final PropertyMapper mapper = new PropertyMapper();

    private PropertyService service() {
        return new PropertyService(repository, mapper);
    }

    private PropertyRequest sampleRequest() {
        return new PropertyRequest("Casa", PropertyType.HOUSE, "Rua A", "100", null,
                "Centro", "São Paulo", "SP", "01000-000",
                new BigDecimal("120.00"), 3, 2, 1, 2010, new BigDecimal("500000"));
    }

    private Property owned(UUID id, String owner) {
        Property p = mapper.toEntity(sampleRequest(), owner);
        p.setId(id);
        return p;
    }

    @Test
    void createShouldPersistWithOwnerAndDraftStatus() {
        when(repository.save(any(Property.class))).thenAnswer(inv -> {
            Property p = inv.getArgument(0);
            p.setId(UUID.randomUUID());
            return p;
        });

        PropertyResponse response = service().create(sampleRequest(), "maria@exemplo.com");

        assertThat(response.ownerSubject()).isEqualTo("maria@exemplo.com");
        assertThat(response.status()).isEqualTo(PropertyStatus.DRAFT);
        assertThat(response.title()).isEqualTo("Casa");
        verify(repository).save(any(Property.class));
    }

    @Test
    void findByIdShouldReturnOwnedProperty() {
        UUID id = UUID.randomUUID();
        when(repository.findByIdAndOwnerSubject(id, "maria@exemplo.com"))
                .thenReturn(Optional.of(owned(id, "maria@exemplo.com")));

        PropertyResponse response = service().findById(id, "maria@exemplo.com");

        assertThat(response.id()).isEqualTo(id);
    }

    @Test
    void findByIdShouldThrowWhenNotOwnedOrMissing() {
        UUID id = UUID.randomUUID();
        when(repository.findByIdAndOwnerSubject(id, "maria@exemplo.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service().findById(id, "maria@exemplo.com"))
                .isInstanceOf(PropertyNotFoundException.class);
    }

    @Test
    void updateShouldApplyChanges() {
        UUID id = UUID.randomUUID();
        when(repository.findByIdAndOwnerSubject(id, "maria@exemplo.com"))
                .thenReturn(Optional.of(owned(id, "maria@exemplo.com")));

        PropertyRequest changed = new PropertyRequest("Casa reformada", PropertyType.HOUSE, "Rua A", "100",
                null, "Centro", "São Paulo", "SP", "01000-000",
                new BigDecimal("130.00"), 4, 2, 1, 2010, new BigDecimal("550000"));

        PropertyResponse response = service().update(id, changed, "maria@exemplo.com");

        assertThat(response.title()).isEqualTo("Casa reformada");
        assertThat(response.bedrooms()).isEqualTo(4);
    }

    @Test
    void deleteShouldRemoveOwnedProperty() {
        UUID id = UUID.randomUUID();
        Property property = owned(id, "maria@exemplo.com");
        when(repository.findByIdAndOwnerSubject(id, "maria@exemplo.com")).thenReturn(Optional.of(property));

        service().delete(id, "maria@exemplo.com");

        verify(repository).delete(property);
    }

    @Test
    void deleteShouldThrowWhenMissing() {
        UUID id = UUID.randomUUID();
        when(repository.findByIdAndOwnerSubject(id, "maria@exemplo.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service().delete(id, "maria@exemplo.com"))
                .isInstanceOf(PropertyNotFoundException.class);
        verify(repository, never()).delete(any());
    }
}
