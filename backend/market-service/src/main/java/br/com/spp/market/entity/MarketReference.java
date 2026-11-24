package br.com.spp.market.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Preço de referência de mercado (R$/m²) para uma combinação de
 * cidade + UF + tipo de imóvel, vigente a partir de uma data.
 */
@Entity
@Table(name = "market_references", indexes = {
        @Index(name = "idx_market_lookup", columnList = "city,state,property_type,effective_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketReference {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false, length = 2)
    private String state;

    @Column(name = "property_type", nullable = false)
    private String propertyType;

    @Column(name = "price_per_sqm", nullable = false, precision = 12, scale = 2)
    private BigDecimal pricePerSqm;

    @Column(name = "effective_date", nullable = false)
    private LocalDate effectiveDate;

    private String source;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Version
    @Column(nullable = false)
    private Long version;
}
