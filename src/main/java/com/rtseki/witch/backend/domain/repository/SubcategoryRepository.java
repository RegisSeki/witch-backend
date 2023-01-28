package com.rtseki.witch.backend.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rtseki.witch.backend.domain.model.Subcategory;

@Repository
public interface SubcategoryRepository extends JpaRepository<Subcategory, Long> {

}
