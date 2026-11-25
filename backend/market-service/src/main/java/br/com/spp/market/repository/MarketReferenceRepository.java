package br.com.spp.market.repository;

import br.com.spp.market.entity.MarketReference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MarketReferenceRepository extends JpaRepository<MarketReference, UUID> {

    /** Preço vigente mais recente para a combinação cidade + UF + tipo. */
    Optional<MarketReference> findTopByCityIgnoreCaseAndStateIgnoreCaseAndPropertyTypeOrderByEffectiveDateDesc(
            String city, String state, String propertyType);
}
