package br.com.spp.report.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Laudo de avaliação, gerado a partir de uma avaliação (valuation).
 * Guarda um retrato dos dados e o texto do laudo.
 */
@Entity
@Table(name = "reports", indexes = {
        @Index(name = "idx_reports_requester", columnList = "requested_by"),
        @Index(name = "idx_reports_valuation", columnList = "valuation_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "valuation_id", nullable = false)
    private UUID valuationId;

    @Column(name = "property_id", nullable = false)
    private UUID propertyId;

    @Column(name = "requested_by", nullable = false)
    private String requestedBy;

    @Column(nullable = false)
    private String title;

    @Column(name = "property_type", nullable = false)
    private String propertyType;

    private String city;

    @Column(length = 2)
    private String state;

    @Column(name = "area_sqm", nullable = false, precision = 12, scale = 2)
    private BigDecimal areaSqm;

    @Column(name = "price_per_sqm", nullable = false, precision = 12, scale = 2)
    private BigDecimal pricePerSqm;

    @Column(name = "estimated_value", nullable = false, precision = 15, scale = 2)
    private BigDecimal estimatedValue;

    @Column(nullable = false, columnDefinition = "text")
    private String summary;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ReportStatus status = ReportStatus.GENERATED;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Version
    @Column(nullable = false)
    private Long version;
}
