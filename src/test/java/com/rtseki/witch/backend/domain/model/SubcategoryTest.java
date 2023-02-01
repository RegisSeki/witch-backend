package com.rtseki.witch.backend.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.rtseki.witch.backend.domain.repository.CategoryRepository;
import com.rtseki.witch.backend.domain.repository.SubcategoryRepository;

import jakarta.persistence.PersistenceException;

@DataJpaTest
public class SubcategoryTest {
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Autowired
	private SubcategoryRepository repository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	Category category;
	Subcategory subcategory;
	
	@BeforeEach
	void init() {
		category = categoryRepository.save(new Category(null, "Health", "To keep the life healthy"));
		subcategory = new Subcategory(null, "Medice", "Needed Drugs", category);
	}
	
	@Test
	@DisplayName("Create subcategory")
	void testCreateSubcategory_whenGivenCorrectDetails_thenReturnStoredSubcategoryDetails() {
		// Arrange
		
		// Act
		Subcategory storedSubcategory = entityManager.persistAndFlush(subcategory);
		
		// Assert
		assertTrue(storedSubcategory.getId() > 0);
		assertEquals(subcategory.getName(), storedSubcategory.getName());
		assertEquals(subcategory.getDescription(), storedSubcategory.getDescription());
		assertEquals(subcategory.getCategory().getId(), storedSubcategory.getCategory().getId());
	}
	
	@Test
	@DisplayName("Do not create subcategory when repeated name")
	void testCreateSubcategory_whenGivenExistedName_thenThrowException() {
		// Arrange
		Subcategory existedSubcategory = new Subcategory();
		existedSubcategory.setCategory(category);
		existedSubcategory.setName(subcategory.getName());
		entityManager.persistAndFlush(existedSubcategory);
		
		// Act and Assert
		assertThrows(PersistenceException.class, () ->{
			entityManager.persistAndFlush(subcategory);
		}, "Was expecting a PersistenceException to be thrown." );
	}
	
	@Test
	@DisplayName("Find subcategory by id")
	void testFindById_whenProvideCorrectId_thenReturnSubcategory() {
		// Arrange
		Subcategory createdSubcategory = entityManager.persistAndFlush(subcategory);
		
		// Act
		Optional<Subcategory> foundSubcategory = repository.findById(createdSubcategory.getId());
		
		// Assert
		assertEquals(foundSubcategory.get(), createdSubcategory);
	}
	
	@Test
	@DisplayName("Update subcategory")
	void testUpdateSubcategory_whenCorrectDetails_thenReturnUpdatedSubcategory() {
		// Arrange
		Subcategory createdSubcategory = entityManager.persistAndFlush(subcategory);
		createdSubcategory.setName("Updated Subcategory Name");
		
		// Act
		Subcategory updatedSubcategory = repository.save(createdSubcategory);
		
		// Act and Assert
		assertEquals(updatedSubcategory, createdSubcategory);
	}
	
	@Test
	@DisplayName("Delete subcategory")
	void testDeleteSubcategory_whenProvidedId_thenNothing() {
		// Arrange
		Subcategory createdSubcategory1 = entityManager.persistAndFlush(subcategory);
		Subcategory createdSubcategory2 = new Subcategory(null, "Bandage", "To patch up the broken body", category);
		entityManager.persistAndFlush(createdSubcategory2);
		
		// Act
		repository.deleteById(createdSubcategory2.getId());
	    List<Subcategory> subcategories = repository.findAll();
		
		//Assert
	    assertThat(subcategories).hasSize(1).contains(createdSubcategory1);
	}
	
	@Test
	@DisplayName("Find all subcategories")
	void testFindAll_whenFindAll_thenReturnSubcategoryList() {
		// Arrange
		entityManager.persistAndFlush(subcategory);
		Subcategory subcategory1 = new Subcategory(null, "Vitamin", "If the body don't produces by his own", category);
		entityManager.persistAndFlush(subcategory1);
		Subcategory subcategory2 = new Subcategory(null, "Bandage", "To patch up the broken body", category);
		entityManager.persistAndFlush(subcategory2);
		
		// Act
		List<Subcategory> subcategories = repository.findAll();
		
		// Assert
		assertThat(subcategories).hasSize(3).contains(subcategory, subcategory1, subcategory2);
	}
}
