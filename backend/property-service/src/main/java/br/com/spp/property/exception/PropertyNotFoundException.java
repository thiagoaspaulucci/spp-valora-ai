package br.com.spp.property.exception;

import java.util.UUID;

/**
 * Lançada quando o imóvel não existe ou não pertence ao usuário.
 */
public class PropertyNotFoundException extends RuntimeException {

    public PropertyNotFoundException(UUID id) {
        super("Imóvel não encontrado: " + id);
    }
}
