package br.com.spp.ai.controller;

import br.com.spp.ai.dto.AiEstimateRequest;
import br.com.spp.ai.dto.AiEstimateResponse;
import br.com.spp.ai.dto.PageResponse;
import br.com.spp.ai.service.AiEstimateService;
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
@RequestMapping("/api/v1/ai/estimates")
@Tag(name = "IA", description = "Estimativas de valor assistidas por IA")
@SecurityRequirement(name = "bearer-jwt")
public class AiEstimateController {

    private final AiEstimateService service;

    public AiEstimateController(AiEstimateService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Gera uma estimativa de valor por IA para um imóvel")
    public ResponseEntity<AiEstimateResponse> create(@Valid @RequestBody AiEstimateRequest request,
                                                     @AuthenticationPrincipal Jwt jwt) {
        AiEstimateResponse created = service.create(request, jwt.getSubject(), jwt.getTokenValue());
        return ResponseEntity.created(URI.create("/api/v1/ai/estimates/" + created.id())).body(created);
    }

    @GetMapping
    @Operation(summary = "Lista as estimativas do usuário (paginado)")
    public ResponseEntity<PageResponse<AiEstimateResponse>> list(@AuthenticationPrincipal Jwt jwt,
                                                                 Pageable pageable) {
        return ResponseEntity.ok(service.list(jwt.getSubject(), pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma estimativa por id")
    public ResponseEntity<AiEstimateResponse> findById(@PathVariable UUID id,
                                                       @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(service.findById(id, jwt.getSubject()));
    }
}
