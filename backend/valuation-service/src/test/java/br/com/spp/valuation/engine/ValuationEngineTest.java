package br.com.spp.valuation.engine;

import br.com.spp.valuation.client.PropertyView;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ValuationEngineTest {

    private ValuationEngine engine() {
        ValuationProperties props = new ValuationProperties(
                new BigDecimal("8000"),
                Map.of("APARTMENT", new BigDecimal("1.00"), "HOUSE", new BigDecimal("1.05")));
        return new ValuationEngine(props);
    }

    @Test
    void shouldApplyAreaRateAndConditionFactors() {
        // área 100, 1 vaga (+2%), 2 quartos (+2%), 1 banheiro (+0,5%) => fator 1,045; sem depreciação
        PropertyView property = new PropertyView(
                java.util.UUID.randomUUID(), "APARTMENT", new BigDecimal("100"),
                2, 1, 1, null, "São Paulo", "SP");

        ValuationEngine.Result result = engine().estimate(property);

        assertThat(result.pricePerSqm()).isEqualByComparingTo("8000.00");
        // 100 * 8000 * 1.045 = 836000.00
        assertThat(result.estimatedValue()).isEqualByComparingTo("836000.00");
    }

    @Test
    void shouldApplyTypeMultiplier() {
        PropertyView house = new PropertyView(
                java.util.UUID.randomUUID(), "HOUSE", new BigDecimal("100"),
                0, 0, 0, null, "São Paulo", "SP");

        ValuationEngine.Result result = engine().estimate(house);

        // 8000 * 1.05 = 8400/m²; fator 1.0; 100 * 8400 = 840000.00
        assertThat(result.pricePerSqm()).isEqualByComparingTo("8400.00");
        assertThat(result.estimatedValue()).isEqualByComparingTo("840000.00");
    }

    @Test
    void unknownTypeShouldUseMultiplierOne() {
        PropertyView land = new PropertyView(
                java.util.UUID.randomUUID(), "LAND", new BigDecimal("100"),
                0, 0, 0, null, "São Paulo", "SP");

        ValuationEngine.Result result = engine().estimate(land);

        assertThat(result.pricePerSqm()).isEqualByComparingTo("8000.00");
    }
}
