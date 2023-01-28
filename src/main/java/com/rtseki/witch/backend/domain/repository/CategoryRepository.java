package com.rtseki.witch.backend.domain.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rtseki.witch.backend.domain.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
	
	Page<Category> findAll(Pageable pageable);
	
	Optional<Category> findByName(String name);
}
