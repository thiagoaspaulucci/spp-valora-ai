package br.com.spp.ai.exception;

import java.util.UUID;

/**
 * Lançada quando o imóvel a estimar não existe (ou não pertence ao usuário).
 */
public class PropertyNotFoundException extends RuntimeException {

    public PropertyNotFoundException(UUID propertyId) {
        super("Imóvel não encontrado: " + propertyId);
    }
}
