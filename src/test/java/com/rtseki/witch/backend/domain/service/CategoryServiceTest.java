package com.rtseki.witch.backend.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.rtseki.witch.backend.domain.exception.BusinessException;
import com.rtseki.witch.backend.domain.exception.ResourceNotFoundException;
import com.rtseki.witch.backend.domain.model.Category;
import com.rtseki.witch.backend.domain.repository.CategoryRepository;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
	
	@Mock
	private CategoryRepository categoryRepository;
	
	@InjectMocks
	private CategoryService categoryService;
	
	private Category category;
	
	@BeforeEach
	void setup() {
		category = Category.builder().
				id(1L).
				name("Food").
				description("Things to eat").
				build();
	}
	
	@Test
	@DisplayName("Create category")
	void createCategory_whenGivenCorrectDetails_thenReturnCreatedCategory() {
		// Arrange
		when(categoryRepository.save(category)).thenReturn(category);
		
		// Act
		Category createdCategory = categoryService.create(category);
		
		// Assert		
		verify(categoryRepository).save(any(Category.class));
		assertNotNull(createdCategory);
		assertEquals(category.getName(), createdCategory.getName());
		assertEquals(category.getDescription(), createdCategory.getDescription());
	}
	
	@Disabled
	@Test
	@DisplayName("Do not create category")
	void createCategory_whenGivenRepeatedName_thenThrowsException() {
		// Arrange
		when(categoryRepository.findByName(category.getName())).thenReturn(Optional.of(category));

		// Act & Assert
		BusinessException businessException = assertThrows(BusinessException.class, () -> {
			categoryService.create(category);
		});
		
		// Assert
		assertTrue(businessException.getMessage().contentEquals("Category name is already taken"));
		verify(categoryRepository, never()).save(any(Category.class));
	}
	
	@Test
	@DisplayName("Find all categories")
	void findAllCategories_whenFindAll_thenReturnCategoriesList() {
		// Arrange
		Category category1 = Category.builder().
				id(2L).
				name("Eletronic Games").
				description("For fun moments").
				build();

		PageRequest pageable = PageRequest.of(1, 2);
		
		Page<Category> categories = new PageImpl<>(List.of(category, category1), pageable, 0);
		
		when(categoryRepository.findAll(pageable)).thenReturn(categories);
		
		// Act

		Page<Category> categoryList = categoryService.findAll(pageable);
		
		// Assert
		assertNotNull(categoryList);
		assertEquals(categoryList.getSize(), 2);
		assertEquals(categoryList.getContent().get(0), category);
		assertEquals(categoryList.getContent().get(1), category1);
	}
	
	@Test
	@DisplayName("Find by id")
	void findById_whenGivenCorrectId_thenReturnCategoryDetails() {
		// Arrange
		when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
		
		// Act
		Category existedCategory = categoryService.findById(category.getId());
		
		// Assert
		assertNotNull(existedCategory);
		assertEquals(existedCategory, category);
	}
	
	@Test
	@DisplayName("Do not find by id")
	void findById_whenGivenInexistentId_thenthrowException() {
		// Arrange
		Long inexistentId = 10L;
		when(categoryRepository.findById(inexistentId)).thenReturn(Optional.empty());
		
		// Act
		ResourceNotFoundException resourceNotFoundException = assertThrows(ResourceNotFoundException.class, () -> {
			categoryService.findById(inexistentId);
		});
		
		// Assert
		assertTrue(resourceNotFoundException.getMessage().
				contentEquals("Resource not found. Id: " + inexistentId));
	}
	
	@Disabled
	@Test
	@DisplayName("Update category")
	void updateCategory_whenProvidedCorrectDetails_thenReturnUpdatedCategory() {
		// Arrange
		when(categoryRepository.save(category)).thenReturn(category);
		Category updateCategory = new Category();
		updateCategory.setName("Technology");
		updateCategory.setDescription("Fancy stuffs");
		
		// Act
		Category updatedCategory = categoryService.update(1L, updateCategory);
		
		// Assert
		verify(categoryRepository).save(any(Category.class));
		assertNotNull(updatedCategory);
	}
	
	@Test
	@DisplayName("Delete category")
	void deleteCategory_whenExistCategory_thenReturnNothing() {
		// Arrange
		Long categoryId = 1L;
		doNothing().when(categoryRepository).deleteById(categoryId);
		
		// Act
		categoryService.delete(categoryId);
		
		// Assert
		verify(categoryRepository, times(1)).deleteById(categoryId);		
	}
	
	@Disabled
	@Test
	@DisplayName("Do not delete category")
	void deleteCategory_whenInexistentCategory_thenReturnNothing() {
		// Arrange
		Long inexistentId = 10L;
		doNothing().when(categoryRepository).deleteById(inexistentId);
		
		// Act
		
		EmptyResultDataAccessException exception = assertThrows(EmptyResultDataAccessException.class, () -> {
			categoryService.delete(inexistentId);
		});
		
		// Assert
		verify(categoryRepository, times(0)).deleteById(inexistentId);		
	}
}
