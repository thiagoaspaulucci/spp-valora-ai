package br.com.spp.valuation.exception;

import java.util.UUID;

/**
 * Lançada quando o imóvel a avaliar não existe (ou não pertence ao usuário).
 */
public class PropertyNotFoundException extends RuntimeException {

    public PropertyNotFoundException(UUID propertyId) {
        super("Imóvel não encontrado: " + propertyId);
    }
}
