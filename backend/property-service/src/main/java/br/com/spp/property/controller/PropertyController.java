package br.com.spp.property.controller;

import br.com.spp.property.dto.PageResponse;
import br.com.spp.property.dto.PropertyRequest;
import br.com.spp.property.dto.PropertyResponse;
import br.com.spp.property.service.PropertyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/properties")
@Tag(name = "Imóveis", description = "Cadastro de imóveis do usuário autenticado")
@SecurityRequirement(name = "bearer-jwt")
public class PropertyController {

    private final PropertyService service;

    public PropertyController(PropertyService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Cadastra um novo imóvel")
    public ResponseEntity<PropertyResponse> create(@Valid @RequestBody PropertyRequest request,
                                                   @AuthenticationPrincipal Jwt jwt) {
        PropertyResponse created = service.create(request, jwt.getSubject());
        return ResponseEntity.created(URI.create("/api/v1/properties/" + created.id())).body(created);
    }

    @GetMapping
    @Operation(summary = "Lista os imóveis do usuário (paginado)")
    public ResponseEntity<PageResponse<PropertyResponse>> list(@AuthenticationPrincipal Jwt jwt,
                                                               Pageable pageable) {
        return ResponseEntity.ok(service.list(jwt.getSubject(), pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um imóvel por id")
    public ResponseEntity<PropertyResponse> findById(@PathVariable UUID id,
                                                     @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(service.findById(id, jwt.getSubject()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um imóvel")
    public ResponseEntity<PropertyResponse> update(@PathVariable UUID id,
                                                   @Valid @RequestBody PropertyRequest request,
                                                   @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(service.update(id, request, jwt.getSubject()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um imóvel")
    public ResponseEntity<Void> delete(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        service.delete(id, jwt.getSubject());
        return ResponseEntity.noContent().build();
    }
}
