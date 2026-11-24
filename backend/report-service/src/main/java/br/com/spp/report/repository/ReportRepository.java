package br.com.spp.report.repository;

import br.com.spp.report.entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ReportRepository extends JpaRepository<Report, UUID> {

    Page<Report> findByRequestedBy(String requestedBy, Pageable pageable);

    Optional<Report> findByIdAndRequestedBy(UUID id, String requestedBy);
}
