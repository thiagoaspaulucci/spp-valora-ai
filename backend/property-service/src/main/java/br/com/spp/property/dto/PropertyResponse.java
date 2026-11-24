package br.com.spp.property.dto;

import br.com.spp.property.entity.PropertyStatus;
import br.com.spp.property.entity.PropertyType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Representação de um imóvel.
 */
@Schema(description = "Imóvel")
public record PropertyResponse(

        UUID id,
        String title,
        PropertyType type,
        String street,
        String number,
        String complement,
        String neighborhood,
        String city,
        String state,
        String zipCode,
        BigDecimal areaSqm,
        int bedrooms,
        int bathrooms,
        int parkingSpots,
        Integer yearBuilt,
        BigDecimal askingPrice,
        PropertyStatus status,
        String ownerSubject,
        Instant createdAt,
        Instant updatedAt
) {
}
