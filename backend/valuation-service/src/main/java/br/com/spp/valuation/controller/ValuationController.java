package br.com.spp.valuation.controller;

import br.com.spp.valuation.dto.PageResponse;
import br.com.spp.valuation.dto.ValuationRequest;
import br.com.spp.valuation.dto.ValuationResponse;
import br.com.spp.valuation.service.ValuationService;
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
@RequestMapping("/api/v1/valuations")
@Tag(name = "Avaliações", description = "Avaliação (valoração) de imóveis")
@SecurityRequirement(name = "bearer-jwt")
public class ValuationController {

    private final ValuationService service;

    public ValuationController(ValuationService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Avalia um imóvel já cadastrado e persiste o laudo")
    public ResponseEntity<ValuationResponse> create(@Valid @RequestBody ValuationRequest request,
                                                    @AuthenticationPrincipal Jwt jwt) {
        ValuationResponse created = service.create(request, jwt.getSubject(), jwt.getTokenValue());
        return ResponseEntity.created(URI.create("/api/v1/valuations/" + created.id())).body(created);
    }

    @GetMapping
    @Operation(summary = "Lista as avaliações do usuário (paginado)")
    public ResponseEntity<PageResponse<ValuationResponse>> list(@AuthenticationPrincipal Jwt jwt,
                                                                Pageable pageable) {
        return ResponseEntity.ok(service.list(jwt.getSubject(), pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma avaliação por id")
    public ResponseEntity<ValuationResponse> findById(@PathVariable UUID id,
                                                      @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(service.findById(id, jwt.getSubject()));
    }
}
