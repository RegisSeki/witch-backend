package com.rtseki.witch.backend.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rtseki.witch.backend.domain.exception.BusinessException;
import com.rtseki.witch.backend.domain.exception.DatabaseException;
import com.rtseki.witch.backend.domain.exception.ResourceNotFoundException;
import com.rtseki.witch.backend.domain.model.Category;
import com.rtseki.witch.backend.domain.model.Subcategory;
import com.rtseki.witch.backend.domain.repository.SubcategoryRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class SubcategoryService {
	
	@Autowired
	private SubcategoryRepository repository;
	
	@Autowired
	private CategoryService categoryService;
	
	@Transactional
	public Subcategory create(Subcategory subcategory) {
		checkDuplicateSubcategoryName(subcategory);
		loadCategoryData(subcategory);
		return repository.save(subcategory);
	}
	
	public Subcategory findById(Long subcategoryId) {
		return repository.findById(subcategoryId)
			.orElseThrow(() -> new ResourceNotFoundException(subcategoryId));
	}
	
	public Subcategory update(Long subcategoryId, Subcategory subcategory) {
		checkDuplicateSubcategoryName(subcategory);
		try {
			Subcategory entity = repository.getReferenceById(subcategoryId);
			updateSubcategoryData(entity, subcategory);
			loadCategoryData(entity);
			return repository.save(entity);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException(subcategoryId);
		}
	}
	
	public void delete(Long subcategoryId) {
		try {
			repository.deleteById(subcategoryId);
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException(subcategoryId);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException(e.getMessage());
		}		
	}
	
	private void checkDuplicateSubcategoryName(Subcategory subcategory) {
		boolean isSubcategoryExist = repository.findByName(subcategory.getName())
				.stream().anyMatch(existSubcategory -> !existSubcategory.equals(subcategory));
		
		if(isSubcategoryExist) {
			throw new BusinessException("Subcategory name is already taken");
		}
	}
	
	private void updateSubcategoryData(Subcategory entity, Subcategory subcategory) {
		entity.setCategory(subcategory.getCategory());
		entity.setName(subcategory.getName());
		entity.setDescription(subcategory.getDescription());
	}
	
	private void loadCategoryData(Subcategory subcategory) {
		Category category = null;
		try {
			category = categoryService.findById(subcategory.getCategory().getId());
		} catch(Exception e)  {
			throw new BusinessException(e.getMessage());
		}
		subcategory.setCategory(category);
	}
}
