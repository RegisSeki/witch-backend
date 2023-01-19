package com.rtseki.witch.backend.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rtseki.witch.backend.domain.exception.BusinessException;
import com.rtseki.witch.backend.domain.exception.DatabaseException;
import com.rtseki.witch.backend.domain.exception.ResourceNotFoundException;
import com.rtseki.witch.backend.domain.model.Category;
import com.rtseki.witch.backend.domain.repository.CategoryRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;

	@Transactional
	public Category create(Category category) {
		checkDuplicateCategoryName(category);
		
		return categoryRepository.save(category);
	}
	
	public Page<Category> findAll(Pageable pageable) {
		return categoryRepository.findAll(pageable);
	}
	
	public Category findById(Long categoryId) {
		return categoryRepository.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException(categoryId));
	}
	
	public Category update(Long categoryId, Category category) {
		try {
			checkDuplicateCategoryName(category);
			Category entity = categoryRepository.getReferenceById(categoryId);
			updateCategoryData(entity, category);
			return categoryRepository.save(entity);
		} catch(EntityNotFoundException e) {
			throw new ResourceNotFoundException(categoryId);
		}
	}
	
	public void delete(Long categoryId) {
		try {
			categoryRepository.deleteById(categoryId);
	
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException(categoryId);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException(e.getMessage());
		}
	}
	
	private void updateCategoryData(Category entity, Category category) {
		entity.setName(category.getName());
		entity.setDescription(category.getDescription());
	}
	
	private void checkDuplicateCategoryName(Category category) {
		boolean isCategoryExist = categoryRepository.findByName(category.getName())
				.stream().anyMatch(existCategory -> !existCategory.equals(category));
		
		if(isCategoryExist) {
			throw new BusinessException("Category name is already taken");
		}
	}
}
