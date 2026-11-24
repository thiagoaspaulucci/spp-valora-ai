package br.com.spp.valuation.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Laudo de avaliação de um imóvel. Guarda um retrato (snapshot) dos dados do
 * imóvel usados no cálculo, tornando a avaliação reproduzível e auditável.
 */
@Entity
@Table(name = "valuations", indexes = {
        @Index(name = "idx_valuations_requester", columnList = "requested_by"),
        @Index(name = "idx_valuations_property", columnList = "property_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Valuation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "property_id", nullable = false)
    private UUID propertyId;

    /** Subject do JWT que solicitou a avaliação. */
    @Column(name = "requested_by", nullable = false)
    private String requestedBy;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ValuationMethod method;

    @Column(name = "estimated_value", nullable = false, precision = 15, scale = 2)
    private BigDecimal estimatedValue;

    @Column(name = "price_per_sqm", nullable = false, precision = 12, scale = 2)
    private BigDecimal pricePerSqm;

    // --- snapshot do imóvel avaliado ---
    @Column(name = "area_sqm", nullable = false, precision = 12, scale = 2)
    private BigDecimal areaSqm;

    @Column(name = "property_type", nullable = false)
    private String propertyType;

    private String city;

    @Column(length = 2)
    private String state;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ValuationStatus status = ValuationStatus.COMPLETED;

    @Column(columnDefinition = "text")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Version
    @Column(nullable = false)
    private Long version;
}
