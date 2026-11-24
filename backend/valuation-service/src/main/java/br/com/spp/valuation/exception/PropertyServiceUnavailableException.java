package br.com.spp.valuation.exception;

/**
 * Lançada quando o property-service está indisponível ou responde com erro.
 */
public class PropertyServiceUnavailableException extends RuntimeException {

    public PropertyServiceUnavailableException(Throwable cause) {
        super("Serviço de imóveis indisponível no momento", cause);
    }
}
