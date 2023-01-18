package com.rtseki.witch.backend.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.rtseki.witch.backend.domain.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

	Page<Category> findAll(Pageable pageable);
}
