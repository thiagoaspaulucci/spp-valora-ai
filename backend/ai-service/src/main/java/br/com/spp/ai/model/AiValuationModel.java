package br.com.spp.ai.model;

import br.com.spp.ai.client.PropertyView;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Year;

/**
 * Modelo de estimativa assistida por IA.
 *
 * <p>Esta implementação é uma heurística determinística e <b>explicável</b>:
 * combina preço/m² por tipo, ajuste de condição (quartos/banheiros/vagas) e
 * depreciação por idade, além de um score de confiança baseado na completude
 * dos dados. É o ponto de integração onde um modelo de ML/LLM treinado pode
 * substituir a heurística, mantendo o mesmo contrato.</p>
 */
@Component
public class AiValuationModel {

    private static final BigDecimal PARKING_ADJ = new BigDecimal("0.025");
    private static final BigDecimal BEDROOM_ADJ = new BigDecimal("0.015");
    private static final BigDecimal BATHROOM_ADJ = new BigDecimal("0.010");
    private static final BigDecimal DEPRECIATION_PER_YEAR = new BigDecimal("0.004");
    private static final BigDecimal MAX_DEPRECIATION = new BigDecimal("0.25");

    private final AiModelProperties properties;

    public AiValuationModel(AiModelProperties properties) {
        this.properties = properties;
    }

    public record Result(
            BigDecimal estimatedValue,
            BigDecimal confidence,
            String modelVersion,
            String factors) {
    }

    public Result estimate(PropertyView p) {
        BigDecimal pricePerSqm = properties.basePricePerSqm()
                .multiply(properties.weightFor(p.type()))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal baseValue = p.areaSqm().multiply(pricePerSqm);

        BigDecimal conditionAdj = PARKING_ADJ.multiply(BigDecimal.valueOf(p.parkingSpots()))
                .add(BEDROOM_ADJ.multiply(BigDecimal.valueOf(p.bedrooms())))
                .add(BATHROOM_ADJ.multiply(BigDecimal.valueOf(p.bathrooms())));
        BigDecimal ageAdj = depreciation(p.yearBuilt());

        BigDecimal factor = BigDecimal.ONE.add(conditionAdj).subtract(ageAdj);
        BigDecimal estimated = baseValue.multiply(factor).setScale(2, RoundingMode.HALF_UP);

        BigDecimal confidence = confidence(p);

        String factors = """
                Preço base: R$ %s/m² (peso de tipo aplicado)
                Valor base (área × preço/m²): R$ %s
                Ajuste de condição: +%s (vagas/quartos/banheiros)
                Depreciação por idade: -%s
                Fator final: %s
                Confiança: %s"""
                .formatted(
                        pricePerSqm.toPlainString(),
                        baseValue.setScale(2, RoundingMode.HALF_UP).toPlainString(),
                        conditionAdj.toPlainString(),
                        ageAdj.toPlainString(),
                        factor.toPlainString(),
                        confidence.toPlainString());

        return new Result(estimated, confidence, properties.modelVersion(), factors);
    }

    private BigDecimal depreciation(Integer yearBuilt) {
        if (yearBuilt == null) {
            return BigDecimal.ZERO;
        }
        int age = Year.now().getValue() - yearBuilt;
        if (age <= 0) {
            return BigDecimal.ZERO;
        }
        return DEPRECIATION_PER_YEAR.multiply(BigDecimal.valueOf(age)).min(MAX_DEPRECIATION);
    }

    /** Confiança cresce com a completude dos dados; limitada a 0,95. */
    private BigDecimal confidence(PropertyView p) {
        BigDecimal score = new BigDecimal("0.70");
        if (properties.knowsType(p.type())) {
            score = score.add(new BigDecimal("0.10"));
        }
        if (p.yearBuilt() != null) {
            score = score.add(new BigDecimal("0.20"));
        }
        return score.min(new BigDecimal("0.95")).setScale(3, RoundingMode.HALF_UP);
    }
}
