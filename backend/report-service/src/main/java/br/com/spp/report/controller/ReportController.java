package br.com.spp.report.controller;

import br.com.spp.report.dto.PageResponse;
import br.com.spp.report.dto.ReportRequest;
import br.com.spp.report.dto.ReportResponse;
import br.com.spp.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reports")
@Tag(name = "Laudos", description = "Geração de laudos de avaliação")
@SecurityRequirement(name = "bearer-jwt")
public class ReportController {

    private final ReportService service;

    public ReportController(ReportService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Gera um laudo a partir de uma avaliação e persiste")
    public ResponseEntity<ReportResponse> create(@Valid @RequestBody ReportRequest request,
                                                 @AuthenticationPrincipal Jwt jwt) {
        ReportResponse created = service.create(request, jwt.getSubject(), jwt.getTokenValue());
        return ResponseEntity.created(URI.create("/api/v1/reports/" + created.id())).body(created);
    }

    @GetMapping
    @Operation(summary = "Lista os laudos do usuário (paginado)")
    public ResponseEntity<PageResponse<ReportResponse>> list(@AuthenticationPrincipal Jwt jwt,
                                                             Pageable pageable) {
        return ResponseEntity.ok(service.list(jwt.getSubject(), pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um laudo por id")
    public ResponseEntity<ReportResponse> findById(@PathVariable UUID id,
                                                   @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(service.findById(id, jwt.getSubject()));
    }
}
