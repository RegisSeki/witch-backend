package com.rtseki.witch.backend.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rtseki.witch.backend.domain.model.BusinessEstablishment;

@Repository
public interface BusinessEstablishmentRepository extends JpaRepository<BusinessEstablishment, Long> {
	Optional<BusinessEstablishment> findByComercialName(String comercialName);
}
