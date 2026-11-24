package br.com.spp.report.generator;

import br.com.spp.report.client.ValuationView;
import org.springframework.stereotype.Component;

/**
 * Compõe o texto do laudo a partir dos dados de uma avaliação.
 */
@Component
public class ReportGenerator {

    public record GeneratedReport(String title, String summary) {
    }

    public GeneratedReport generate(ValuationView v) {
        String location = nvl(v.city()) + "/" + nvl(v.state());
        String title = "Laudo de Avaliação — %s em %s".formatted(v.propertyType(), location);

        String summary = """
                LAUDO DE AVALIAÇÃO DE IMÓVEL

                Imóvel: %s em %s
                Área: %s m²
                Valor por m²: R$ %s
                Método: %s

                VALOR ESTIMADO: R$ %s
                """.formatted(
                v.propertyType(),
                location,
                v.areaSqm().toPlainString(),
                v.pricePerSqm().toPlainString(),
                v.method(),
                v.estimatedValue().toPlainString());

        return new GeneratedReport(title, summary);
    }

    private String nvl(String value) {
        return value == null ? "-" : value;
    }
}
