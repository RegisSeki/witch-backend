package com.rtseki.witch.backend.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rtseki.witch.backend.domain.model.Subcategory;

@Repository
public interface SubcategoryRepository extends JpaRepository<Subcategory, Long> {	
	Optional<Subcategory> findByName(String name);
}
