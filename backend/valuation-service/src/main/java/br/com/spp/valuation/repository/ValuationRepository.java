package br.com.spp.valuation.repository;

import br.com.spp.valuation.entity.Valuation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ValuationRepository extends JpaRepository<Valuation, UUID> {

    Page<Valuation> findByRequestedBy(String requestedBy, Pageable pageable);

    Optional<Valuation> findByIdAndRequestedBy(UUID id, String requestedBy);
}
