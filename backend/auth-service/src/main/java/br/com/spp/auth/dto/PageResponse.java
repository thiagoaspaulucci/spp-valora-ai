package br.com.spp.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Envelope padrão para respostas paginadas — a ser usado por qualquer
 * endpoint de coleção da plataforma (evita expor o {@code Page} do Spring).
 */
@Schema(description = "Resposta paginada padronizada")
public record PageResponse<T>(

        List<T> content,

        int page,

        int size,

        long totalElements,

        int totalPages,

        boolean last
) {
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast());
    }
}
