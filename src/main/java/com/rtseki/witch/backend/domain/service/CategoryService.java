package com.rtseki.witch.backend.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rtseki.witch.backend.domain.exception.BusinessException;
import com.rtseki.witch.backend.domain.exception.ResourceNotFoundException;
import com.rtseki.witch.backend.domain.model.Category;
import com.rtseki.witch.backend.domain.repository.CategoryRepository;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;
	
	@Transactional
	public Category create(Category category) {
		boolean isCategoryExist = categoryRepository.findByName(category.getName())
				.stream().anyMatch(existCategory -> !existCategory.equals(category));
		
		if(isCategoryExist) {
			throw new BusinessException("Category name is already taken");
		}
		
		return categoryRepository.save(category);
	}
	
	public Page<Category> findAll(Pageable pageable) {
		return categoryRepository.findAll(pageable);
	}
	
	public Category findById(Long categoryId) {
		return categoryRepository.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException(categoryId));
	}
}
