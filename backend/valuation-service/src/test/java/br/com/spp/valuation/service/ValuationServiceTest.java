package br.com.spp.valuation.service;

import br.com.spp.valuation.client.MarketClient;
import br.com.spp.valuation.client.PropertyClient;
import br.com.spp.valuation.client.PropertyView;
import br.com.spp.valuation.dto.ValuationRequest;
import br.com.spp.valuation.dto.ValuationResponse;
import br.com.spp.valuation.engine.ValuationEngine;
import br.com.spp.valuation.engine.ValuationProperties;
import br.com.spp.valuation.entity.Valuation;
import br.com.spp.valuation.exception.ValuationNotFoundException;
import br.com.spp.valuation.mapper.ValuationMapper;
import br.com.spp.valuation.repository.ValuationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValuationServiceTest {

    @Mock
    private ValuationRepository repository;
    @Mock
    private PropertyClient propertyClient;
    @Mock
    private MarketClient marketClient;

    private final ValuationEngine engine = new ValuationEngine(
            new ValuationProperties(new BigDecimal("8000"), Map.of("APARTMENT", new BigDecimal("1.00"))));
    private final ValuationMapper mapper = new ValuationMapper();

    private ValuationService service() {
        return new ValuationService(repository, propertyClient, marketClient, engine, mapper);
    }

    @Test
    void createShouldFetchPropertyComputeAndPersist() {
        UUID propertyId = UUID.randomUUID();
        PropertyView property = new PropertyView(propertyId, "APARTMENT", new BigDecimal("100"),
                2, 1, 1, null, "São Paulo", "SP");
        when(propertyClient.fetch(propertyId, "token-123")).thenReturn(property);
        when(marketClient.fetchRate(any(), any(), any(), any())).thenReturn(java.util.Optional.empty());
        when(repository.save(any(Valuation.class))).thenAnswer(inv -> {
            Valuation v = inv.getArgument(0);
            v.setId(UUID.randomUUID());
            return v;
        });

        ValuationResponse response = service()
                .create(new ValuationRequest(propertyId), "maria@exemplo.com", "token-123");

        assertThat(response.propertyId()).isEqualTo(propertyId);
        assertThat(response.requestedBy()).isEqualTo("maria@exemplo.com");
        assertThat(response.estimatedValue()).isEqualByComparingTo("836000.00");
        assertThat(response.pricePerSqm()).isEqualByComparingTo("8000.00");
        verify(propertyClient).fetch(propertyId, "token-123");
        verify(repository).save(any(Valuation.class));
    }

    @Test
    void findByIdShouldThrowWhenMissing() {
        UUID id = UUID.randomUUID();
        when(repository.findByIdAndRequestedBy(eq(id), eq("maria@exemplo.com"))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service().findById(id, "maria@exemplo.com"))
                .isInstanceOf(ValuationNotFoundException.class);
    }
}
