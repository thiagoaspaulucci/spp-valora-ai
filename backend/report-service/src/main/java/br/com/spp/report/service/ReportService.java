package br.com.spp.report.service;

import br.com.spp.report.client.ValuationClient;
import br.com.spp.report.client.ValuationView;
import br.com.spp.report.dto.PageResponse;
import br.com.spp.report.dto.ReportRequest;
import br.com.spp.report.dto.ReportResponse;
import br.com.spp.report.entity.Report;
import br.com.spp.report.exception.ReportNotFoundException;
import br.com.spp.report.generator.ReportGenerator;
import br.com.spp.report.mapper.ReportMapper;
import br.com.spp.report.repository.ReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Regras de negócio do laudo: busca a avaliação no valuation-service
 * (propagando o JWT), gera o texto do laudo e persiste.
 */
@Service
public class ReportService {

    private static final Logger log = LoggerFactory.getLogger(ReportService.class);

    private final ReportRepository repository;
    private final ValuationClient valuationClient;
    private final ReportGenerator generator;
    private final ReportMapper mapper;

    public ReportService(ReportRepository repository, ValuationClient valuationClient,
                         ReportGenerator generator, ReportMapper mapper) {
        this.repository = repository;
        this.valuationClient = valuationClient;
        this.generator = generator;
        this.mapper = mapper;
    }

    @Transactional
    public ReportResponse create(ReportRequest request, String requester, String bearerToken) {
        ValuationView valuation = valuationClient.fetch(request.valuationId(), bearerToken);
        ReportGenerator.GeneratedReport generated = generator.generate(valuation);

        Report report = Report.builder()
                .valuationId(valuation.id())
                .propertyId(valuation.propertyId())
                .requestedBy(requester)
                .title(generated.title())
                .propertyType(valuation.propertyType())
                .city(valuation.city())
                .state(valuation.state())
                .areaSqm(valuation.areaSqm())
                .pricePerSqm(valuation.pricePerSqm())
                .estimatedValue(valuation.estimatedValue())
                .summary(generated.summary())
                .build();

        repository.save(report);
        log.info("Laudo {} gerado para a avaliação {} por {}",
                report.getId(), valuation.id(), requester);
        return mapper.toResponse(report);
    }

    @Transactional(readOnly = true)
    public ReportResponse findById(UUID id, String requester) {
        return mapper.toResponse(repository.findByIdAndRequestedBy(id, requester)
                .orElseThrow(() -> new ReportNotFoundException(id)));
    }

    @Transactional(readOnly = true)
    public PageResponse<ReportResponse> list(String requester, Pageable pageable) {
        return PageResponse.from(
                repository.findByRequestedBy(requester, pageable).map(mapper::toResponse));
    }
}
