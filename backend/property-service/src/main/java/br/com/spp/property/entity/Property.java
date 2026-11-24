package br.com.spp.property.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Imóvel cadastrado na plataforma, base para avaliação (valuation).
 */
@Entity
@Table(name = "properties", indexes = {
        @Index(name = "idx_properties_owner", columnList = "owner_subject"),
        @Index(name = "idx_properties_city_state", columnList = "city,state")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PropertyType type;

    @Column(nullable = false)
    private String street;

    @Column(name = "street_number")
    private String number;

    private String complement;

    private String neighborhood;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false, length = 2)
    private String state;

    @Column(name = "zip_code", nullable = false)
    private String zipCode;

    @Column(name = "area_sqm", nullable = false, precision = 12, scale = 2)
    private BigDecimal areaSqm;

    @Column(nullable = false)
    @Builder.Default
    private int bedrooms = 0;

    @Column(nullable = false)
    @Builder.Default
    private int bathrooms = 0;

    @Column(name = "parking_spots", nullable = false)
    @Builder.Default
    private int parkingSpots = 0;

    @Column(name = "year_built")
    private Integer yearBuilt;

    @Column(name = "asking_price", precision = 15, scale = 2)
    private BigDecimal askingPrice;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PropertyStatus status = PropertyStatus.DRAFT;

    /** Identificador do dono (subject do JWT — e-mail do usuário). */
    @Column(name = "owner_subject", nullable = false)
    private String ownerSubject;

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
