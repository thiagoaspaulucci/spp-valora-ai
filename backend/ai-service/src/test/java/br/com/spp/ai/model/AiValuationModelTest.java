package br.com.spp.ai.model;

import br.com.spp.ai.client.PropertyView;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AiValuationModelTest {

    private AiValuationModel model() {
        return new AiValuationModel(new AiModelProperties(
                "heuristic-v1", new BigDecimal("8200"), Map.of("APARTMENT", new BigDecimal("1.00"))));
    }

    @Test
    void shouldEstimateWithConfidenceAndFactors() {
        // área 100, 1 vaga (+0,025), 2 quartos (+0,030), 1 banheiro (+0,010) => 0,065; sem idade
        PropertyView p = new PropertyView(
                UUID.randomUUID(), "APARTMENT", new BigDecimal("100"), 2, 1, 1, null, "São Paulo", "SP");

        AiValuationModel.Result result = model().estimate(p);

        // 100 * 8200 * 1.065 = 873300.00
        assertThat(result.estimatedValue()).isEqualByComparingTo("873300.00");
        // 0.70 + 0.10 (tipo conhecido) + 0 (sem ano) = 0.800
        assertThat(result.confidence()).isEqualByComparingTo("0.800");
        assertThat(result.modelVersion()).isEqualTo("heuristic-v1");
        assertThat(result.factors()).contains("Confiança: 0.800");
    }

    @Test
    void knownTypeAndYearRaiseConfidence() {
        PropertyView p = new PropertyView(
                UUID.randomUUID(), "APARTMENT", new BigDecimal("100"), 0, 0, 0, 2015, "São Paulo", "SP");

        AiValuationModel.Result result = model().estimate(p);

        // 0.70 + 0.10 + 0.20 = 1.00 -> limitado a 0.950
        assertThat(result.confidence()).isEqualByComparingTo("0.950");
    }
}
