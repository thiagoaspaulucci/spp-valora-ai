package br.com.spp.market.exception;

import java.util.UUID;

/**
 * Lançada quando o preço de referência não existe.
 */
public class MarketReferenceNotFoundException extends RuntimeException {

    public MarketReferenceNotFoundException(UUID id) {
        super("Preço de referência não encontrado: " + id);
    }

    public MarketReferenceNotFoundException(String message) {
        super(message);
    }
}
