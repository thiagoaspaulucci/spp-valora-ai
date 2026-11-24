package br.com.spp.market.controller;

import br.com.spp.market.dto.*;
import br.com.spp.market.service.MarketReferenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/market-references")
@Tag(name = "Mercado", description = "Preços de referência de mercado (escrita restrita a ADMIN)")
@SecurityRequirement(name = "bearer-jwt")
public class MarketReferenceController {

    private final MarketReferenceService service;

    public MarketReferenceController(MarketReferenceService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cria um preço de referência (ADMIN)")
    public ResponseEntity<MarketReferenceResponse> create(@Valid @RequestBody MarketReferenceRequest request,
                                                          @AuthenticationPrincipal Jwt jwt) {
        MarketReferenceResponse created = service.create(request, jwt.getSubject());
        return ResponseEntity.created(URI.create("/api/v1/market-references/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualiza um preço de referência (ADMIN)")
    public ResponseEntity<MarketReferenceResponse> update(@PathVariable UUID id,
                                                          @Valid @RequestBody MarketReferenceRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remove um preço de referência (ADMIN)")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Lista os preços de referência (paginado)")
    public ResponseEntity<PageResponse<MarketReferenceResponse>> list(Pageable pageable) {
        return ResponseEntity.ok(service.list(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um preço de referência por id")
    public ResponseEntity<MarketReferenceResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/rate")
    @Operation(summary = "Consulta o preço vigente para cidade/UF/tipo")
    public ResponseEntity<RateResponse> rate(@RequestParam String city,
                                             @RequestParam String state,
                                             @RequestParam String propertyType) {
        return ResponseEntity.ok(service.getRate(city, state, propertyType));
    }
}
