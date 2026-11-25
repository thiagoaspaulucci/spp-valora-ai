package br.com.spp.ai.repository;

import br.com.spp.ai.entity.AiEstimate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AiEstimateRepository extends JpaRepository<AiEstimate, UUID> {

    Page<AiEstimate> findByRequestedBy(String requestedBy, Pageable pageable);

    Optional<AiEstimate> findByIdAndRequestedBy(UUID id, String requestedBy);
}
