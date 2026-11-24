package br.com.spp.report.mapper;

import br.com.spp.report.dto.ReportResponse;
import br.com.spp.report.entity.Report;
import org.springframework.stereotype.Component;

/**
 * Conversão da entidade {@link Report} para o DTO de saída.
 */
@Component
public class ReportMapper {

    public ReportResponse toResponse(Report r) {
        return new ReportResponse(
                r.getId(),
                r.getValuationId(),
                r.getPropertyId(),
                r.getRequestedBy(),
                r.getTitle(),
                r.getPropertyType(),
                r.getCity(),
                r.getState(),
                r.getAreaSqm(),
                r.getPricePerSqm(),
                r.getEstimatedValue(),
                r.getSummary(),
                r.getStatus(),
                r.getCreatedAt());
    }
}
