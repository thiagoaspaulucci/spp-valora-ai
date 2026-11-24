package br.com.spp.report.exception;

import java.util.UUID;

/**
 * Lançada quando a avaliação a laudar não existe (ou não pertence ao usuário).
 */
public class ValuationNotFoundException extends RuntimeException {

    public ValuationNotFoundException(UUID valuationId) {
        super("Avaliação não encontrada: " + valuationId);
    }
}
