package com.rtseki.witch.backend.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rtseki.witch.backend.domain.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
