package br.com.spp.report.exception;

import java.util.UUID;

/**
 * Lançada quando o laudo não existe ou não pertence ao usuário.
 */
public class ReportNotFoundException extends RuntimeException {

    public ReportNotFoundException(UUID id) {
        super("Laudo não encontrado: " + id);
    }
}
