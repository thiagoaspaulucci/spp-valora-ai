package br.com.spp.report.exception;

/**
 * Lançada quando o valuation-service está indisponível ou responde com erro.
 */
public class ValuationServiceUnavailableException extends RuntimeException {

    public ValuationServiceUnavailableException(Throwable cause) {
        super("Serviço de avaliações indisponível no momento", cause);
    }
}
