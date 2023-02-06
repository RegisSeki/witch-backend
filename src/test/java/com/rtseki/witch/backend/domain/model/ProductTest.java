package com.rtseki.witch.backend.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.rtseki.witch.backend.domain.repository.CategoryRepository;
import com.rtseki.witch.backend.domain.repository.ProductRepository;
import com.rtseki.witch.backend.domain.repository.SubcategoryRepository;

import jakarta.persistence.PersistenceException;

@DataJpaTest
public class ProductTest {
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Autowired
	private ProductRepository repository;
	
	@Autowired
	private SubcategoryRepository subcategoryRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	Category category;
	Subcategory subcategory;
	Product product;
	
	@BeforeEach
	void init() {
		category = categoryRepository.save(new Category(null, "Cat", "Things for the cat"));
		subcategory = subcategoryRepository.save(new Subcategory(null, "Cleaning", "To keep the cat fresh and clean", category));
		product = new Product(null, "7898960782356", "Areia Higiênica Viva Verde 4Kg", "Ease to clean", subcategory);
	}
	
	@Test
	@DisplayName("Create product")
	void testCreateProduct_whenGivenCorrectDetails_thenReturnStoredProductDetails() {
		// Arrange
		
		// Act
		Product storedproduct = entityManager.persistAndFlush(product);
		
		// Assert
		assertTrue(storedproduct.getId() > 0);
		assertEquals(product.getName(), storedproduct.getName());
		assertEquals(product.getDescription(), storedproduct.getDescription());
		assertEquals(product.getSubcategory().getId(), storedproduct.getSubcategory().getId());
	}
	
	@Test
	@DisplayName("Do not create product when repeated barcode")
	void testCreateProduct_whenGivenExistedBarcode_thenThrowException() {
		// Arrange
		Product existedProduct = new Product();
		existedProduct.setSubcategory(subcategory);
		existedProduct.setBarcode(product.getBarcode());
		existedProduct.setName(product.getName());
		entityManager.persistAndFlush(existedProduct);
		
		// Act and Assert
		assertThrows(PersistenceException.class, () ->{
			entityManager.persistAndFlush(product);
		}, "Was expecting a PersistenceException to be thrown." );
	}
	
	@Test
	@DisplayName("Find product by id")
	void testProductFindById_whenProvideCorrectId_thenReturnProduct() {
		// Arrange
		Product createdProduct = entityManager.persistAndFlush(product);
		
		// Act
		Optional<Product> foundProduct = repository.findById(createdProduct.getId());
		
		// Assert
		assertEquals(foundProduct.get(), createdProduct);
	}
}
