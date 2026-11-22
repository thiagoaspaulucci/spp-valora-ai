package br.com.spp.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.slf4j.MDC;

import java.time.Instant;

/**
 * Corpo padronizado de resposta de erro.
 */
@Schema(description = "Resposta de erro padronizada")
public record ApiError(

        Instant timestamp,

        int status,

        String error,

        String message,

        String path,

        String traceId
) {
    public static ApiError of(int status, String error, String message, String path) {
        return new ApiError(Instant.now(), status, error, message, path, MDC.get("traceId"));
    }
}
