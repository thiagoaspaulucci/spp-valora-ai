package br.com.spp.report.generator;

import br.com.spp.report.client.ValuationView;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ReportGeneratorTest {

    @Test
    void shouldComposeTitleAndSummary() {
        ValuationView v = new ValuationView(
                UUID.randomUUID(), UUID.randomUUID(),
                new BigDecimal("836000.00"), new BigDecimal("8000.00"), new BigDecimal("100.00"),
                "APARTMENT", "São Paulo", "SP", "SIMPLE_FORMULA");

        ReportGenerator.GeneratedReport report = new ReportGenerator().generate(v);

        assertThat(report.title()).isEqualTo("Laudo de Avaliação — APARTMENT em São Paulo/SP");
        assertThat(report.summary())
                .contains("VALOR ESTIMADO: R$ 836000.00")
                .contains("Valor por m²: R$ 8000.00")
                .contains("Área: 100.00 m²")
                .contains("São Paulo/SP");
    }
}
