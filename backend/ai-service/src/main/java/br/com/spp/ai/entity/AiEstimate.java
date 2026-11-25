package br.com.spp.ai.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Estimativa de valor assistida por IA, com score de confiança e um
 * detalhamento (explicabilidade) dos fatores que compuseram o valor.
 */
@Entity
@Table(name = "ai_estimates", indexes = {
        @Index(name = "idx_ai_requester", columnList = "requested_by"),
        @Index(name = "idx_ai_property", columnList = "property_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiEstimate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "property_id", nullable = false)
    private UUID propertyId;

    @Column(name = "requested_by", nullable = false)
    private String requestedBy;

    @Column(name = "estimated_value", nullable = false, precision = 15, scale = 2)
    private BigDecimal estimatedValue;

    /** Confiança do modelo, de 0 a 1. */
    @Column(nullable = false, precision = 4, scale = 3)
    private BigDecimal confidence;

    @Column(name = "model_version", nullable = false)
    private String modelVersion;

    /** Detalhamento legível dos fatores (explicabilidade). */
    @Column(nullable = false, columnDefinition = "text")
    private String factors;

    // --- snapshot do imóvel ---
    @Column(name = "property_type", nullable = false)
    private String propertyType;

    @Column(name = "area_sqm", nullable = false, precision = 12, scale = 2)
    private BigDecimal areaSqm;

    private String city;

    @Column(length = 2)
    private String state;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Version
    @Column(nullable = false)
    private Long version;
}
