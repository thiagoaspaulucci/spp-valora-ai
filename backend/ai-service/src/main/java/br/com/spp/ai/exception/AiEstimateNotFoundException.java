package br.com.spp.ai.exception;

import java.util.UUID;

/**
 * Lançada quando a estimativa não existe ou não pertence ao usuário.
 */
public class AiEstimateNotFoundException extends RuntimeException {

    public AiEstimateNotFoundException(UUID id) {
        super("Estimativa não encontrada: " + id);
    }
}
