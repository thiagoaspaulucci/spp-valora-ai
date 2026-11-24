package br.com.spp.valuation.engine;

import br.com.spp.valuation.client.PropertyView;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Year;

/**
 * Motor de avaliação paramétrica.
 *
 * <pre>
 * pricePerSqm   = basePricePerSqm × multiplicadorDoTipo
 * valorBase     = área × pricePerSqm
 * fatorCondição = 1 + 0,02·vagas + 0,01·quartos + 0,005·banheiros − depreciação
 * depreciação   = min(0,30; 0,005 · idadeEmAnos)
 * valorEstimado = valorBase × fatorCondição
 * </pre>
 */
@Component
public class ValuationEngine {

    private static final BigDecimal PARKING_BONUS = new BigDecimal("0.02");
    private static final BigDecimal BEDROOM_BONUS = new BigDecimal("0.01");
    private static final BigDecimal BATHROOM_BONUS = new BigDecimal("0.005");
    private static final BigDecimal DEPRECIATION_PER_YEAR = new BigDecimal("0.005");
    private static final BigDecimal MAX_DEPRECIATION = new BigDecimal("0.30");

    private final ValuationProperties properties;

    public ValuationEngine(ValuationProperties properties) {
        this.properties = properties;
    }

    public record Result(BigDecimal estimatedValue, BigDecimal pricePerSqm) {
    }

    /**
     * Estima o valor do imóvel. Se {@code marketPricePerSqm} for informado
     * (preço de referência do market-service), ele é usado como preço por m²;
     * caso contrário, aplica-se o preço base de configuração × multiplicador.
     */
    public Result estimate(PropertyView property, BigDecimal marketPricePerSqm) {
        BigDecimal pricePerSqm = (marketPricePerSqm != null)
                ? marketPricePerSqm.setScale(2, RoundingMode.HALF_UP)
                : properties.basePricePerSqm()
                        .multiply(properties.multiplierFor(property.type()))
                        .setScale(2, RoundingMode.HALF_UP);

        BigDecimal baseValue = property.areaSqm().multiply(pricePerSqm);

        BigDecimal conditionFactor = BigDecimal.ONE
                .add(PARKING_BONUS.multiply(BigDecimal.valueOf(property.parkingSpots())))
                .add(BEDROOM_BONUS.multiply(BigDecimal.valueOf(property.bedrooms())))
                .add(BATHROOM_BONUS.multiply(BigDecimal.valueOf(property.bathrooms())))
                .subtract(depreciation(property.yearBuilt()));

        BigDecimal estimated = baseValue.multiply(conditionFactor).setScale(2, RoundingMode.HALF_UP);
        return new Result(estimated, pricePerSqm);
    }

    private BigDecimal depreciation(Integer yearBuilt) {
        if (yearBuilt == null) {
            return BigDecimal.ZERO;
        }
        int age = Year.now().getValue() - yearBuilt;
        if (age <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal value = DEPRECIATION_PER_YEAR.multiply(BigDecimal.valueOf(age));
        return value.min(MAX_DEPRECIATION);
    }
}
