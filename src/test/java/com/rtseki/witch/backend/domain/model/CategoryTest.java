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

import com.rtseki.witch.backend.domain.exception.ResourceNotFoundException;
import com.rtseki.witch.backend.domain.repository.CategoryRepository;

import jakarta.persistence.PersistenceException;

@DataJpaTest
public class CategoryTest {

	@Autowired
	private TestEntityManager testEntityManager;
	
	@Autowired
	private CategoryRepository repository;
	
	Category category;
	
	@BeforeEach
	void setup() {
		category = new Category();
		category.setName("Food");
		category.setDescription("Things to eat");
	}
	
	@Test
	@DisplayName("Create category")
	void testCreateCategory_whenGivenCorrectDetails_thenReturnStoredCategoryDetails() {
		// Arrange
		
		// Act
		Category storedCategory = testEntityManager.persistAndFlush(category);
		
		// Assert
		assertTrue(storedCategory.getId() > 0);
		assertEquals(category.getName(), storedCategory.getName());
		assertEquals(category.getDescription(), storedCategory.getDescription());
	}
	
	@Test
	@DisplayName("Do not create category when repeated name")
	void testCreateCategory_whenGivenRepeatedDetails_thenThrowException() {
		// Arrange
		Category oldCategory = new Category();
		oldCategory.setName(category.getName());
		testEntityManager.persistAndFlush(oldCategory);
		
		// Act and Assert
		assertThrows(PersistenceException.class, () ->{
			testEntityManager.persistAndFlush(category);
		}, "Was expecting a PersistenceException to be thrown." );
	}
	
	@Test
	@DisplayName("Find all categories")
	void testFindAll_whenFindAll_thenReturnCategoryList() {
		// Arrange
		testEntityManager.persistAndFlush(category);
		Category category1 = new Category(null, "Health", "For a helathy life");
		testEntityManager.persistAndFlush(category1);
		Category category2 = new Category(null, "Eletronic Games", "For fun moments");
		testEntityManager.persistAndFlush(category2);
		
		// Act
		List<Category> categories = repository.findAll();
		
		// Assert
		assertThat(categories).hasSize(3).contains(category, category1, category2);
	}
	
	@Test
	@DisplayName("Find category by id")
	void testFindById_whenProvideCorrectId_thenReturnCategory() {
		// Arrange
		Category createdCategory = testEntityManager.persistAndFlush(category);
		
		// Act
		Optional<Category> foundCategory = repository.findById(createdCategory.getId());
		
		// Assert
		assertEquals(foundCategory.get(), createdCategory);
	}
	
	@Test
	@DisplayName("Update category")
	void testUpdateCategory_whenCorrectDetails_thenReturnUpdatedCategory() {
		// Arrange
		Category createdCategory = testEntityManager.persistAndFlush(category);
		createdCategory.setName("Updated Category Name");
		
		// Act
		Category updatedCategory = repository.save(createdCategory);
		
		// Act and Assert
		assertEquals(createdCategory, updatedCategory);
	}
	
	@Test
	@DisplayName("Delete category")
	void testDeleteCategory_whenProvidedId_thenNothing() {
		// Arrange
		Category createdCategory1 = testEntityManager.persistAndFlush(category);
		Category createdCategory2 = new Category(null, "Eletronic Games", "For fun moments");
		testEntityManager.persistAndFlush(createdCategory2);
		
		// Act
		repository.deleteById(createdCategory2.getId());
	    List<Category> categories = repository.findAll();
		
		//Assert
	    assertThat(categories).hasSize(1).contains(createdCategory1);
	}
}
