package com.rtseki.witch.backend.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rtseki.witch.backend.domain.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	Optional<Product> findByBarcode(String barcode);
	
	Optional<Product> findByName(String name);
}
