package br.com.spp.valuation.exception;

import java.util.UUID;

/**
 * Lançada quando a avaliação não existe ou não pertence ao usuário.
 */
public class ValuationNotFoundException extends RuntimeException {

    public ValuationNotFoundException(UUID id) {
        super("Avaliação não encontrada: " + id);
    }
}
