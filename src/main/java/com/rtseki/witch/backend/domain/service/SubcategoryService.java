package com.rtseki.witch.backend.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rtseki.witch.backend.domain.exception.BusinessException;
import com.rtseki.witch.backend.domain.exception.ResourceNotFoundException;
import com.rtseki.witch.backend.domain.model.Category;
import com.rtseki.witch.backend.domain.model.Subcategory;
import com.rtseki.witch.backend.domain.repository.SubcategoryRepository;

@Service
public class SubcategoryService {
	
	@Autowired
	private SubcategoryRepository repository;
	
	@Autowired
	private CategoryService categoryService;
	
	@Transactional
	public Subcategory create(Subcategory subcategory) {
		checkDuplicateSubcategoryName(subcategory);
		Category category = categoryService.findById(subcategory.getCategory().getId());
		subcategory.setCategory(category);
		return repository.save(subcategory);
	}
	
	public Subcategory findById(Long subcategoryId) {
		return repository.findById(subcategoryId)
			.orElseThrow(() -> new ResourceNotFoundException(subcategoryId));
	}
	
	private void checkDuplicateSubcategoryName(Subcategory subcategory) {
		boolean isSubcategoryExist = repository.findByName(subcategory.getName())
				.stream().anyMatch(existSubcategory -> !existSubcategory.equals(subcategory));
		
		if(isSubcategoryExist) {
			throw new BusinessException("Subcategory name is already taken");
		}
	}
}
