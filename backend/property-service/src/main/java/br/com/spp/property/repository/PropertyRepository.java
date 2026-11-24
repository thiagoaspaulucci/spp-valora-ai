package br.com.spp.property.repository;

import br.com.spp.property.entity.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PropertyRepository extends JpaRepository<Property, UUID> {

    Page<Property> findByOwnerSubject(String ownerSubject, Pageable pageable);

    Optional<Property> findByIdAndOwnerSubject(UUID id, String ownerSubject);
}
