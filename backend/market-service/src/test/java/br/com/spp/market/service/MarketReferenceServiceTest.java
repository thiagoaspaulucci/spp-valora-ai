package br.com.spp.market.service;

import br.com.spp.market.dto.MarketReferenceRequest;
import br.com.spp.market.dto.RateResponse;
import br.com.spp.market.entity.MarketReference;
import br.com.spp.market.exception.MarketReferenceNotFoundException;
import br.com.spp.market.mapper.MarketReferenceMapper;
import br.com.spp.market.repository.MarketReferenceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MarketReferenceServiceTest {

    @Mock
    private MarketReferenceRepository repository;

    private final MarketReferenceMapper mapper = new MarketReferenceMapper();

    private MarketReferenceService service() {
        return new MarketReferenceService(repository, mapper);
    }

    @Test
    void createShouldPersistWithCreator() {
        var request = new MarketReferenceRequest("São Paulo", "SP", "APARTMENT",
                new BigDecimal("9000.00"), LocalDate.of(2026, 10, 1), "internal");
        when(repository.save(any(MarketReference.class))).thenAnswer(inv -> {
            MarketReference r = inv.getArgument(0);
            r.setId(UUID.randomUUID());
            return r;
        });

        var response = service().create(request, "admin@exemplo.com");

        assertThat(response.createdBy()).isEqualTo("admin@exemplo.com");
        assertThat(response.pricePerSqm()).isEqualByComparingTo("9000.00");
    }

    @Test
    void getRateShouldReturnLatestMatch() {
        MarketReference ref = MarketReference.builder()
                .id(UUID.randomUUID()).city("São Paulo").state("SP").propertyType("APARTMENT")
                .pricePerSqm(new BigDecimal("9500.00")).effectiveDate(LocalDate.of(2026, 10, 1))
                .createdBy("admin@exemplo.com").build();
        when(repository.findTopByCityIgnoreCaseAndStateIgnoreCaseAndPropertyTypeOrderByEffectiveDateDesc(
                "São Paulo", "SP", "APARTMENT")).thenReturn(Optional.of(ref));

        RateResponse rate = service().getRate("São Paulo", "SP", "APARTMENT");

        assertThat(rate.pricePerSqm()).isEqualByComparingTo("9500.00");
    }

    @Test
    void getRateShouldThrowWhenNoMatch() {
        when(repository.findTopByCityIgnoreCaseAndStateIgnoreCaseAndPropertyTypeOrderByEffectiveDateDesc(
                any(), any(), any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service().getRate("Curitiba", "PR", "HOUSE"))
                .isInstanceOf(MarketReferenceNotFoundException.class);
    }

    @Test
    void findByIdShouldThrowWhenMissing() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service().findById(id))
                .isInstanceOf(MarketReferenceNotFoundException.class);
    }
}
