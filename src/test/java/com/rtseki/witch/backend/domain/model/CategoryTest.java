package com.rtseki.witch.backend.domain.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import jakarta.persistence.PersistenceException;

@DataJpaTest
public class CategoryTest {

	@Autowired
	private TestEntityManager testEntityManager;
	
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
		Assertions.assertTrue(storedCategory.getId() > 0);
		Assertions.assertEquals(category.getName(), storedCategory.getName());
		Assertions.assertEquals(category.getDescription(), storedCategory.getDescription());
	}
	
	@Test
	@DisplayName("Do not create category")
	void testCreateCategory_whenGivenRepeatedDetails_thenThrowException() {
		// Arrange
		Category oldCategory = new Category();
		oldCategory.setName(category.getName());
		testEntityManager.persistAndFlush(oldCategory);
		
		// Act and Assert
		Assertions.assertThrows(PersistenceException.class, () ->{
			testEntityManager.persistAndFlush(category);
		}, "Was expecting a PersistenceException to be thrown." );
	}
}
