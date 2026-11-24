package br.com.spp.report.service;

import br.com.spp.report.client.ValuationClient;
import br.com.spp.report.client.ValuationView;
import br.com.spp.report.dto.ReportRequest;
import br.com.spp.report.dto.ReportResponse;
import br.com.spp.report.entity.Report;
import br.com.spp.report.exception.ReportNotFoundException;
import br.com.spp.report.generator.ReportGenerator;
import br.com.spp.report.mapper.ReportMapper;
import br.com.spp.report.repository.ReportRepository;
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
class ReportServiceTest {

    @Mock
    private ReportRepository repository;
    @Mock
    private ValuationClient valuationClient;

    private final ReportGenerator generator = new ReportGenerator();
    private final ReportMapper mapper = new ReportMapper();

    private ReportService service() {
        return new ReportService(repository, valuationClient, generator, mapper);
    }

    @Test
    void createShouldFetchValuationGenerateAndPersist() {
        UUID valuationId = UUID.randomUUID();
        UUID propertyId = UUID.randomUUID();
        ValuationView valuation = new ValuationView(valuationId, propertyId,
                new BigDecimal("836000.00"), new BigDecimal("8000.00"), new BigDecimal("100.00"),
                "APARTMENT", "São Paulo", "SP", "SIMPLE_FORMULA");
        when(valuationClient.fetch(valuationId, "token-123")).thenReturn(valuation);
        when(repository.save(any(Report.class))).thenAnswer(inv -> {
            Report r = inv.getArgument(0);
            r.setId(UUID.randomUUID());
            return r;
        });

        ReportResponse response = service()
                .create(new ReportRequest(valuationId), "maria@exemplo.com", "token-123");

        assertThat(response.valuationId()).isEqualTo(valuationId);
        assertThat(response.propertyId()).isEqualTo(propertyId);
        assertThat(response.requestedBy()).isEqualTo("maria@exemplo.com");
        assertThat(response.estimatedValue()).isEqualByComparingTo("836000.00");
        assertThat(response.summary()).contains("VALOR ESTIMADO: R$ 836000.00");
        verify(repository).save(any(Report.class));
    }

    @Test
    void findByIdShouldThrowWhenMissing() {
        UUID id = UUID.randomUUID();
        when(repository.findByIdAndRequestedBy(id, "maria@exemplo.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service().findById(id, "maria@exemplo.com"))
                .isInstanceOf(ReportNotFoundException.class);
    }
}
